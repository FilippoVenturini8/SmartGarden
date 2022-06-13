#ifndef __DIGITALLED__
#define __DIGITALLED__

class DigitalLed {

public:
  virtual void switchOn() = 0;
  virtual void switchOff() = 0;
};

#endif
