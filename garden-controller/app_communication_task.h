#ifndef __APPCOMUNICATIONTASK__
#define __APPCOMUNICATIONTASK__
#include "Task.h"
#include <Arduino.h>
#include "SoftwareSerial.h"
#include <Wire.h>

class AppCommunicationTask: public Task {

 enum { RECEIVING } state;
 int RXD = 2;
 int TXD = 3;
 bool isMsgAvailable;
 String msg;
 
public:

 AppCommunicationTask(); 
 void init(int period); 
 void tick();
 void checkMsg();
 
};

extern AppCommunicationTask appCommunicationTask;
#endif
