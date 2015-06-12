#include "phSensor.h"


phSensor::phSensor() {}

phSensor::phSensor(AnalogIn *a) {
    phIn = a;
}

float phSensor::getCurrentPH() {   
    return currentPH;
}

// Read and calculate the temperature from the ADC. Return the read temperature
float phSensor::phFromADC() {
    currentPH = 14.0 * phIn->read();
        
    return currentPH;
}
 
int8_t * phSensor::dispString() {
    int8_t tens = (int8_t) (currentPH / 10); 
    int8_t ones = (int8_t)(currentPH - ((float)tens*10));
    int8_t tenths = (int8_t)((currentPH - ((float)tens*10) - ((float)ones))*10);
    tens = tens? tens : 0x7F;
    
    pString[0] = tens;
    pString[1] = ones;
    pString[2] = tenths;
    pString[3] = 0x7F;
    
    return pString;  
}    