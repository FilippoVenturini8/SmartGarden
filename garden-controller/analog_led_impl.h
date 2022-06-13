#ifndef __ANALOGLED_IMPL__
#define __ANALOGLED_IMPL__

#include <arduino.h>
#include "analog_led.h"

class AnalogLedImpl: public AnalogLed {

public:
  AnalogLedImpl(int pin);

  void setIntensity(int intensity);
    
private:
  int pin;
};

#endif
