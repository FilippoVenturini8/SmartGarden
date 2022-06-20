#ifndef __SERIALCOMMUNICATIONTASK__
#define __SERIALCOMMUNICATIONTASK__
#include "Task.h"
#include <Arduino.h>

class SerialCommunicationTask: public Task {

 enum {WAITING, RECEIVE} state;
  
public:

 SerialCommunicationTask(); 
 void init(int period); 
 void tick();
 void readMsg();
 
};

extern SerialCommunicationTask serialCommunicationTask;
#endif
