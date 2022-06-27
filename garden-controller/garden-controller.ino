#include "shared_variables.h"
#include "scheduler.h"
#include "task.h"
#include "service_communication_task.h"
#include "app_communication_task.h"
#include "light_system_task.h"
#include "irrigation_system_task.h"

Scheduler sched;

int switchDL1 = 0;
int switchDL2 = 0;
int valueAL1 = 0;
int valueAL2 = 0;
int openIrrigation = 0;
int irrigationSpeed = 0;
int isIrrigationSleeping = 0;
String modality = "AUT";

void setup() {
  sched.init(50);

  Task* serviceCommunicationTask = new ServiceCommunicationTask();
  serviceCommunicationTask->init(100);
  sched.addTask(serviceCommunicationTask);

  Task* appCommunicationTask = new AppCommunicationTask();
  appCommunicationTask->init(50);
  sched.addTask(appCommunicationTask);

  Task* lightSystemTask = new LightSystemTask(8,7,6,5);
  lightSystemTask->init(50);
  sched.addTask(lightSystemTask);

  Task* irrigationSystemTask = new IrrigationSystemTask(9);
  irrigationSystemTask->init(50);
  sched.addTask(irrigationSystemTask);
  
  Serial.begin(9600);
}

void loop() {
  sched.schedule();
}
