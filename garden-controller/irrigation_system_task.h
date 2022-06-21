#ifndef __IRRIGATIONSYSTEMTASK__
#define __IRRIGATIONSYSTEMTASK__
#include "Task.h"
#include "servo_motor.h"
#include <Arduino.h>

class IrrigationSystemTask: public Task {

 enum { CLOSED, OPEN} state;
 int pinServo;
 ServoMotor* servo;
 int totServoStep;
 int currentServoStep;
 int delta;
 
public:

 IrrigationSystemTask(int pinServo); 
 void init(int period); 
 void tick();
 
};
#endif
