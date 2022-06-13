#include "analog_led_impl.h"
#include "Arduino.h"

AnalogLedImpl::AnalogLedImpl(int pin){
  this->pin = pin;  
  pinMode(this->pin, OUTPUT);
} 

void AnalogLedImpl::setIntensity(int intensity){
  analogWrite(pin, intensity);    
}
