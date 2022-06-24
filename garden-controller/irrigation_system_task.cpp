#include "irrigation_system_task.h"
#include "servo_motor_impl.h"
#include "shared_variables.h"
#include <Arduino.h>

IrrigationSystemTask::IrrigationSystemTask(int pinServo){
  this->state = CLOSED;
  this->pinServo = pinServo;
  this->totServoStep = 0;
  this->delta = 1;
  this->lastSleep = millis();
}
 
void IrrigationSystemTask::init(int period){
  Task::init(period);
  this->servo = new ServoMotorImpl(this->pinServo);
}
 
void IrrigationSystemTask::tick(){
  switch(state){
    case SLEEP_STATE: 
      if(millis() - startSleep >= SLEEP_TIME){
        lastSleep = millis();
        state = lastState;
        isIrrigationSleeping = 0;
      }
      break;
    case CLOSED:
      if(millis()-lastSleep >= WORK_TIME){
        lastState = state;
        state = SLEEP_STATE;
        startSleep = millis();
        isIrrigationSleeping = 1;
        break;
      }
      if(openIrrigation == 1){
        currentServoStep = 0;
        this->totServoStep = irrigationSpeed;
        this->servo->on();
        state = OPEN;
      }
      break;
    case OPEN:
      if(millis()-lastSleep >= WORK_TIME){
        lastState = state;
        state = SLEEP_STATE;
        startSleep = millis();
        isIrrigationSleeping = 1;
        break;
      }
      if(openIrrigation == 0 or irrigationSpeed == 0){
        this->servo->off();
        state = CLOSED;
      }
      if(irrigationSpeed != totServoStep){
        totServoStep = irrigationSpeed;
      }
      if(delta == 1 and currentServoStep == totServoStep){
        delta = -1;
      }else if(delta == -1 and currentServoStep == 0){
        delta = 1;
      }
      currentServoStep += delta;
      long angle = map (currentServoStep, 0, totServoStep, 0, 180);
      servo->setPosition(angle);
      break;
    
  }
}
