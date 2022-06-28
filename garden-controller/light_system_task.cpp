#include "light_system_task.h"
#include "digital_led_impl.h"
#include "analog_led_impl.h"
#include "shared_variables.h"
#include <Arduino.h> 

LightSystemTask::LightSystemTask(int pinDL1, int pinDL2, int pinAL1, int pinAL2){
  this->state = IDLE_STATE;
  
  this->pinDL1 = pinDL1;
  this->pinDL2 = pinDL2;
  this->pinAL1 = pinAL1;
  this->pinAL2 = pinAL2;

  this->lastDL1 = 0;
  this->lastDL2 = 0;
  this->lastAL1 = 0;
  this->lastAL2 = 0; 
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
      if(modality.equals("AUT") and switchDL1 != lastDL1){
        state = AUTO_MANAGEMENT;
      }else if(switchDL1 != lastDL1){
        state = DL1_SWITCHING;
        lastDL1 = switchDL1;
      }else if(switchDL2 != lastDL2){
        state = DL2_SWITCHING;
        lastDL2 = switchDL2;
      }else if(valueAL1 != lastAL1){
        state = AL1_MANAGEMENT;
        lastAL1 = valueAL1;
      }else if(valueAL2 != lastAL2){
        state = AL2_MANAGEMENT;
        lastAL2 = valueAL2;
      }
      break;
    case DL1_SWITCHING:
      if(switchDL1 == 1){
        this->digitalLight1->switchOn();
      }else if(switchDL1 == 0){
        this->digitalLight1->switchOff();
      }
      state = IDLE_STATE;
      break;
    case DL2_SWITCHING:
      if(switchDL2 == 1){
        this->digitalLight2->switchOn();
      }else if(switchDL2 == 0){
        this->digitalLight2->switchOff();
      }
      state = IDLE_STATE;
      break;
    case AL1_MANAGEMENT:
      if(valueAL1 == 0){
        this->analogLight1->setIntensity(0);
      }else{
        int mappedValue1 = map(valueAL1, 0, 4, 0, 255);
        this->analogLight1->setIntensity(mappedValue1);
      }
      state = IDLE_STATE;
      break;
    case AL2_MANAGEMENT:
      if(valueAL1 == 0){
        this->analogLight2->setIntensity(0);
      }else{
        int mappedValue2 = map(valueAL2, 0, 4, 0, 255);
        this->analogLight2->setIntensity(mappedValue2);
      }
      state = IDLE_STATE;
      break;

    case AUTO_MANAGEMENT:
      if(switchDL1 == 1){
        this->digitalLight1->switchOn();
        this->digitalLight2->switchOn();

        int mappedValue1 = map(valueAL1, 0, 4, 0, 255);
        this->analogLight1->setIntensity(mappedValue1);
        int mappedValue2 = map(valueAL2, 0, 4, 0, 255);
        this->analogLight2->setIntensity(mappedValue2);
      }else{
        this->digitalLight1->switchOff();
        this->digitalLight2->switchOff();
        this->analogLight1->setIntensity(0);
        this->analogLight2->setIntensity(0);
      }
      lastDL1 = switchDL1;
      lastDL2 = switchDL2;
      lastAL1 = valueAL1;
      lastAL2 = valueAL2;
      state = IDLE_STATE;
      break;
  }
}
