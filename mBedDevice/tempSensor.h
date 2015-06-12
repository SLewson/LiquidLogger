#ifndef __TEMPSENSOR___H
#define __TEMPSENSOR___H
#include "mbed.h"

class TempSensor {
public: 
    TempSensor();
    TempSensor(AnalogIn *a);
    float getCurrentTemp();    
    int8_t * dispString();
    
    float tempFromADC();
    static double log2(double x);

private:    
    float currentTemp;
    AnalogIn * tempIn;       
    int B;                  // B value of the thermistor
    int8_t dString[4];

};

#endif //  __TEMPSENSOR___H