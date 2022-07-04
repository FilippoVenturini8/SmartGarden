#ifndef __TEMP_IMPL__
#define __TEMP_IMPL__

#include "temp_sensor.h"

class TempSensorImpl: public TempSensor{
    public:
        TempSensorImpl(int pin);
        float getTemperature();
    private:
        int tempSensorPin = 0;
        int tempVal = 0;
        float volts =0;
        float temp = 0;

};

#endif
