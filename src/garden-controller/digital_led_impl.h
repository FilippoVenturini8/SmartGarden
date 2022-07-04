#ifndef __DIGITALLED_IMPL__
#define __DIGITALLED_IMPL__

#include <arduino.h>
#include "digital_led.h"

class DigitalLedImpl: public DigitalLed {

public:
  DigitalLedImpl(int pin);

  void switchOn();
  void switchOff();
    
private:
  int pin;
};

#endif
