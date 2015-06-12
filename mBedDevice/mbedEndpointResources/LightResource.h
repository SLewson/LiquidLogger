/**
 * @file    LightResource.h
 * @brief   mbed CoAP Endpoint Light resource supporting CoAP GET and PUT
 * @author  Doug Anson, Michael Koster
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

#ifndef __LIGHT_RESOURCE_H__
#define __LIGHT_RESOURCE_H__


// Base class
#include "DynamicResource.h"

// our Light
#include "ChainableLED.h"
#define LED_COUNT 1

ChainableLED led_chain(D4, D5, LED_COUNT);

static char * led_value = {"0000000"}; //RRGGBBII

void set_led_array(char * color_string)
{
    static uint8_t red, green, blue, index;
    int color_int;

    sscanf(color_string, "%X", &color_int);
    
    index = color_int & 255;
    blue = color_int >> 8 & 255;
    green = color_int >> 16 & 255;
    red = color_int >> 24 & 255;
        
    if(index > 0 and index <= LED_COUNT) {
        led_chain.setColorRGB(index-1, red, green, blue);    
    }
    else if(index == 0){
        for(int i = 0; i < LED_COUNT; i++){
            led_chain.setColorRGB(i, red, green, blue);    
        }
    }    
}

/** LightResource class
 */
class LightResource : public DynamicResource
{

public:
    /**
    Default constructor
    @param logger input logger instance for this resource
    @param name input the Light resource name
    @param observable input the resource is Observable (default: FALSE)
    */
    LightResource(const Logger *logger,const char *name,const bool observable = false) : DynamicResource(logger,name,"Light",SN_GRS_GET_ALLOWED|SN_GRS_PUT_ALLOWED,observable) {
    set_led_array("00000000");
    wait(0.5);
    set_led_array("FF000000");
    wait(0.5);
    set_led_array("00FF0000");
    wait(0.5);
    set_led_array("0000FF00");
    wait(0.5);
    set_led_array(led_value);
    }

    /**
    Get the value of the Light
    @returns string containing either "0" (light off) or "1" (light on)
    */
    virtual string get() {
        return(led_value);
    }

    /**
    Set the value of the Light
    @param string input the string containing "0" (light off) or "1" (light on)
    */
    virtual void put(const string value) {
        if( sizeof(value) == sizeof(led_value) ){
            led_value = (char *)value.c_str();
            set_led_array(led_value);
        }
    }
};

#endif // __LIGHT_RESOURCE_H__
