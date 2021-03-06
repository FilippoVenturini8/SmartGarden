#ifndef __IRRIGATIONSYSTEMTASK__
#define __IRRIGATIONSYSTEMTASK__
#include "Task.h"
#include "servo_motor.h"
#include <Arduino.h>

class IrrigationSystemTask: public Task {

 const long WORK_TIME = 20000;
 const long SLEEP_TIME = 10000;
 enum { CLOSED, OPEN, SLEEP_STATE} state;
 int pinServo;
 ServoMotor* servo;
 int totServoStep;
 int currentServoStep;
 int delta;
 unsigned long lastSleep;
 unsigned long startSleep;
 int lastState;
 
public:

 IrrigationSystemTask(int pinServo); 
 void init(int period); 
 void tick();
 
};
#endif
