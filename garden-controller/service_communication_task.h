#ifndef __SERVICECOMMUNICATIONTASK__
#define __SERVICECOMMUNICATIONTASK__
#include "Task.h"
#include <Arduino.h>

class ServiceCommunicationTask: public Task {

 enum {WAITING, RECEIVE, SEND} state;
  
public:

 ServiceCommunicationTask(); 
 void init(int period); 
 void tick();
 void readMsg();
 String lastModality;
 
};

extern ServiceCommunicationTask serviceCommunicationTask;
#endif
