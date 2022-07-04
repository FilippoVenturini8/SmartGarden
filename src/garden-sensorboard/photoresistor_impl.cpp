#include "photoresistor_impl.h"
#include "Arduino.h"

PhotoresistorImpl::PhotoresistorImpl(int photoresistorPin){
    this->photoresistorPin = photoresistorPin;
    pinMode(this->photoresistorPin, INPUT);
}

int PhotoresistorImpl::getLux(){
  int value = analogRead(this->photoresistorPin);
  return map(value, 0, 2500, 0, 7);
}
