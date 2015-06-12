/**
 * @file    SliderResource.h
 * @brief   mbed CoAP Endpoint
 * @author  Michael Koster
 * @version 1.0
 * @see
 *
 * Copyright (c) 2015
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

#ifndef __TEMP_RESOURCE_H__
#define __TEMP_RESOURCE_H__

// Base class
#include "DynamicResource.h"


/** TemperatureResource class
 */

extern RawSerial pc;
extern TempSensor ts;
class TempResource : public DynamicResource
{
public:
    /**
    Default constructor
    @param logger input logger instance for this resource
    @param name input the resource name
    @param observable input the resource is Observable (default: FALSE)
    */
    TempResource(const Logger *logger,const char *name,const bool observable = false) : DynamicResource(logger,name,"Temp", SN_GRS_GET_ALLOWED,observable) {
    }

    /**
    Get the value of the slide potentiometer
    @returns string containing the slider value from 0-1.00
    */
    virtual string get() {
        char curTemp[7];
        sprintf(curTemp,"%3.2f", ts.getCurrentTemp());        
        return string(curTemp);
    }
};

#endif // __SLIDER_RESOURCE_H__
