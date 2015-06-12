/**
 * @file    main.cpp
 * @brief   mbed Connected Home Endpoint main entry point
 * @author  Doug Anson
 * @version 1.0
 * @see
 *
 * Copyright (c) 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// mbed Connector Interface (configuration)
#include "mbedConnectorInterface.h"

// Ethernet Interface
#include "EthernetInterface.h"
EthernetInterface ethernet;

// mbed Connector Endpoint includes
#include "ConnectorEndpoint.h"
#include "OptionsBuilder.h"

// Hardware Header files
#include "phSensor.h"
#include "tempSensor.h"
#include "TM1637.h"

// USB Serial port access for debugging/logging
RawSerial pc(USBTX,USBRX);

// Logging facility
Logger logger(&pc);

// Static Resources
#include "StaticResource.h"
StaticResource mfg(&logger,"3/0/0","Freescale");
StaticResource model(&logger,"3/0/1","K64F mbed Ethernet demo");

//
// Dynamic Resource Note:
//
//  mbedConnectorInterface supports up to IPT_MAX_ENTRIES 
//  (currently 5) independent dynamic resources.
//
//  You can increase this (at the cost of memory) 
//  in mbedConnectorinterface.h
//

// Light Resource
#include "LightResource.h"
LightResource light(&logger,"3311/0/5706");

// LED Resource
#include "OnBoardLED.h"
LEDResource led(&logger,"3311/1/5706");

// ph Meter Resource
#include "phResource.h"
phResource phMeter(&logger,"3202/0/5600", true); /* true if observable */

// Temperature Resource
#include "TempResource.h"
TempResource temperature(&logger, "3303/0/5700", true); 

// Set our own unique endpoint name
#define MY_ENDPOINT_NAME                       "liquid"

// My NSP Domain
#define MY_NSP_DOMAIN                          "coffee"                               

// Customization Example: My custom NSP IPv4 or IPv6 address and NSP CoAP port 
//uint8_t my_nsp_address[NSP_IP_ADDRESS_LENGTH] = {192,168,1,199}; /* local */
//http://23.253.213.123:1880/#   54.191.98.247:8080
uint8_t my_nsp_address[NSP_IP_ADDRESS_LENGTH] = {54,191,98,247}; /* smartobjectservice.com */
int my_nsp_coap_port                          = 5683;

// called from the Endpoint::start() below to create resources and the endpoint internals...
Connector::Options *configure_endpoint(Connector::OptionsBuilder &config)
{
    // Build the endpoint configuration parameters
    logger.log("configure_endpoint: building endpoint configuration...");
    phMeter.setMaxAge(0); /* MaxAge = 0 to disable caching of the slide value in the Device Server */
    temperature.setMaxAge(0); /* MaxAge = 0 to disable caching of the slide value in the Device Server */
    return config.setEndpointNodename(MY_ENDPOINT_NAME)                   // custom endpoint name
                 .setNSPAddress(my_nsp_address)                           // custom NSP address
                 .setDomain(MY_NSP_DOMAIN)                                // custom NSP domain
                 .setNSPPortNumber(my_nsp_coap_port)                      // custom NSP CoAP port
                 
                 // add the static resource representing this endpoint
                 .addResource(&mfg)
                 .addResource(&model)
                                    
                 // Add my specific physical dynamic resources...
                 .addResource(&light)
                 .addResource(&phMeter, 10000)
                 .addResource(&led)
                 .addResource(&temperature, 10000) 
                   
                 // finalize the configuration...
                 .build();
}
// ADC Ports
AnalogIn tempIn(A2);
AnalogIn phIn(A3);  

// Digital Ports for Display
DigitalOut dClk(D2);
DigitalInOut dIO(D3);
DigitalOut phLED(A4);

// Buttons
InterruptIn button(SW2);

// Global Hardware Classes
TempSensor ts(&tempIn);    
phSensor ps(&phIn);
TM1637 disp(&dClk, &dIO);
RtosTimer *timer;
Thread *displayThread;
uint8_t dispMode = 0;


// pH Data Aquisition thread. Remove all print messages in produciton version
void phRead_thread(void const *args) {
    float phRead;    
    
    while ("George is awesome") {
        phRead = ps.phFromADC();
        pc.printf("ph value: %f\n", phRead);
        phLED = !phLED;        
        if (dispMode%3 == 2) {
            disp.point(true);
            disp.display4(ps.dispString());
        }
        
        Thread::wait(1000);
    }
}    

// Temperature Data Aquisition thread. Remove all print messages in produciton version
void tempRead_thread(void const *args) { 
    float temp;

    while (true) {
        temp = ts.tempFromADC();
        pc.printf("Temperature: %f\n", temp);
        if (dispMode%3 == 1) {
            disp.point(true);
            disp.display4(ts.dispString());
        }
        Thread::wait(1000);
    }
}

void display_off(void const *args) { disp.clearDisplay(); }

void button_press(void) {
    
    displayThread->signal_set(0x1);
}

void display_thread(void const *args) {
    
    while ("ITS OVER 9000!!!!!") {
        Thread::signal_wait(0x1);
        if(++dispMode % 3 == 0) {            
            disp.clearDisplay();
        }

//        timer = new RtosTimer(display_off, osTimerOnce);
//        timer->start(5000);        
    }
}



// main entry point... penetrate me.
int main() {  

      // Set up data display
    disp.init();   
    disp.set(5);    // Set brightness from 0 to 7    
    button.fall(&button_press);
     
    // Initiate data gathering threads
    Thread tempThread(tempRead_thread);
    Thread phThread(phRead_thread);
    displayThread = new Thread(display_thread);
             
    // Announce
    logger.log("\r\n\r\nmbed mDS Sample Endpoint v1.0 (Ethernet)");

    // we have to plumb our network first
    Connector::Endpoint::plumbNetwork();         
     
    // starts the endpoint by finalizing its configuration (configure_endpoint() above called),creating a Thread and reading NSP events...
    logger.log("Start the endpoint to finish setup and enter the main loop...");
    Connector::Endpoint::start();
    

}
