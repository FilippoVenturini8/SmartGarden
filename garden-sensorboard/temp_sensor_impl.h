#ifndef __TEMP__IMPL
#define __TEMP__IMPL

#include "temp_sensor.h"

class TempSensorImpl: public TempSensor{
    public:
        TempSensorImpl(int tempSensorPin);
        float getTemperature();
    private:
        int tempSensorPin;
        int Vo;
        float R1 = 100000;
        float logR2, R2, T;
        float c1 = 1.009249522e-03, c2 = 2.378405444e-04, c3 = 2.019202697e-07;
};

#endif
