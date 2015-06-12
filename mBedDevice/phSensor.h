#ifndef __PH_SENSOR___H
#define __PH_SENSOR___H
#include "mbed.h"

class phSensor {
public: 
    phSensor();
    phSensor(AnalogIn *a);
    float getCurrentPH();        
    float phFromADC();
    int8_t * dispString();

private:    
    float currentPH;
    AnalogIn * phIn;    
    int8_t pString[4];

};

#endif //  __PH_SENSOR___H