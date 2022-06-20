#include "light_system_task.h"
#include "digital_led_impl.h"
#include "analog_led_impl.h"
#include <Arduino.h>

LightSystemTask::LightSystemTask(int pinDL1, int pinDL2, int pinAL1, int pinAL2){
  this->pinDL1 = pinDL1;
  this->pinDL2 = pinDL2;
  this->pinAL1 = pinAL1;
  this->pinAL2 = pinAL2;
}
 
void LightSystemTask::init(int period){
  Task::init(period);
  this->digitalLight1 = new DigitalLedImpl(this->pinDL1);
  this->digitalLight2 = new DigitalLedImpl(this->pinDL2);
  this->analogLight1 = new AnalogLedImpl(this->pinAL1);
  this->analogLight2 = new AnalogLedImpl(this->pinAL2);
}
 
void LightSystemTask::tick(){
  switch(state){
    case IDLE_STATE:
      break;
    case DL1_SWITCHING:
      break;
    case DL2_SWITCHING:
      break;
    case AL1_MANAGEMENT:
      break;
    case AL2_MANAGEMENT:
      break;
  }
}
