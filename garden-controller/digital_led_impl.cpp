#include "digital_led_impl.h"
#include "Arduino.h"

DigitalLedImpl::DigitalLedImpl(int pin){
  this->pin = pin;  
  pinMode(this->pin, OUTPUT);
} 

void DigitalLedImpl::switchOn(){
  digitalWrite(pin, HIGH);    
}

void DigitalLedImpl::switchOff(){
  digitalWrite(pin, LOW);    
}
