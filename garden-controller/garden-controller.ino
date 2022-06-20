#include "shared_variables.h"
#include "scheduler.h"
#include "task.h"
#include "serial_communication_task.h"

Scheduler sched;

bool switchDL1 = false;

void setup() {
  /*digitalLed1 = new DigitalLedImpl(8);
  digitalLed2 = new DigitalLedImpl(7);
  analogLed1 = new AnalogLedImpl(6);
  analogLed2 = new AnalogLedImpl(5);
  servoMotor = new ServoMotorImpl(3);*/

  sched.init(50);

  Task* serialCommunicationTask = new SerialCommunicationTask();
  serialCommunicationTask->init(100);
  sched.addTask(serialCommunicationTask);
  
  Serial.begin(9600);
}

void loop() {
  /*for(int i=0; i<255; i++){
    analogLed1->setIntensity(i);
    analogLed2->setIntensity(i);
    delay(10);
  }
  servoMotor->on();
  servoMotor->setPosition(180);
  delay(500);
  servoMotor->setPosition(0);
  delay(500);
  servoMotor->off();
  digitalLed1->switchOn();
  delay(500);
  digitalLed1->switchOff();
  delay(500);
  digitalLed2->switchOn();
  delay(500);
  digitalLed2->switchOff();
  delay(500);*/
  sched.schedule();
  if(switchDL1){
    digitalWrite(8, HIGH);
  }
}
