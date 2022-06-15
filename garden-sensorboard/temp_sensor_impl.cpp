#include "temp_sensor_impl.h"
#include "Arduino.h"

TempSensorImpl::TempSensorImpl(int tempSensorPin){
    this->tempSensorPin = tempSensorPin;
}

float TempSensorImpl::getTemperature(){
  Vo = analogRead(tempSensorPin);
  R2 = R1 * (1023.0 / (float)Vo - 1.0);
  logR2 = log(R2);
  //Serial.println(logR2);
  T = (1.0 / (c1 + c2*logR2 + c3*logR2*logR2*logR2));
  return T - 273.15;
}
