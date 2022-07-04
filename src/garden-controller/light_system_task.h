#ifndef __LIGHTSYSTMETASK__
#define __LIGHTSYSTMETASK__
#include "Task.h"
#include "digital_led.h"
#include "analog_led.h"
#include <Arduino.h>

class LightSystemTask: public Task {

 enum { IDLE_STATE, DL1_SWITCHING, DL2_SWITCHING, AL1_MANAGEMENT, AL2_MANAGEMENT, ALL_LIGHTS_MANAGEMENT} state;
 DigitalLed* digitalLight1;
 DigitalLed* digitalLight2;
 AnalogLed* analogLight1;
 AnalogLed* analogLight2;
 int pinDL1;
 int pinDL2;
 int pinAL1;
 int pinAL2;
 int lastDL1;
 int lastDL2;
 int lastAL1;
 int lastAL2;
 
public:

 LightSystemTask(int pinDL1, int pinDL2, int pinAL1, int pinAL2); 
 void init(int period); 
 void tick();
 
};
#endif
