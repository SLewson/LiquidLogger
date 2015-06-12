#include "tempSensor.h"


TempSensor::TempSensor() {    
    B = 3975;                  //B value of the thermistor  
}

TempSensor::TempSensor(AnalogIn *a) {
    tempIn = a;
    B = 3975;                  //B value of the thermistor    
}

float TempSensor::getCurrentTemp() { 
    return currentTemp; 
}

// Read and calculate the temperature from the ADC. Return the read temperature
float TempSensor::tempFromADC() {
    float tempRead;
    float resistance;


    tempRead = tempIn->read();
    //get the resistance of the sensor;
    resistance = (1-tempRead)*10000/tempRead; 
    //convert to temperature via datasheet ;
    currentTemp = (float)((1.0 / (log2(resistance/10000)*(1.0/B) + 1.0/298.15)) - 273.15);    
        
    return currentTemp;
}

int8_t * TempSensor::dispString() {
    int8_t tens = (int8_t) (currentTemp / 10); 
    int8_t ones = (int8_t)(currentTemp - ((float)tens*10));
    int8_t tenths = (int8_t)((currentTemp - ((float)tens*10) - ((float)ones))*10);
    tens = tens? tens : 0x7F;       // Clear if zero
    
    dString[0] = tens;
    dString[1] = ones;
    dString[2] = tenths;
    dString[3] = (int8_t) 0xC;
   
    
    return dString;    
}    


// Our log2 funciton because log is broker
double TempSensor::log2(double x) {
    return log10(x) / log10(2.0);
}  