#include "temp_sensor.h"
#include "temp_sensor_impl.h"
#include "Arduino.h"

TempSensorImpl::TempSensorImpl(int tempSensorPin){
    this->tempSensorPin = tempSensorPin;
    
}


float TempSensorImpl::getTemperature(){
    
    this->tempVal = analogRead(this->tempSensorPin);

    this->volts = tempVal/1023.0;           

    this->temp = (volts - 0.5) * 100 ;
    return temp;
}
