#ifndef __APPCOMUNICATIONTASK__
#define __APPCOMUNICATIONTASK__
#include "Task.h"
#include <Arduino.h>
#include "SoftwareSerial.h"
#include <Wire.h>

class AppCommunicationTask: public Task {

 const int UPDATE_TIME = 1000;

 enum { IDLE_STATE, RECEIVING, SENDING } state;
 int RXD = 2;
 int TXD = 3;
 bool isMsgAvailable;
 String msg;
 bool tryReceiving;
 unsigned long lastUpdateTime;
 
public:

 AppCommunicationTask(); 
 void init(int period); 
 void tick();
 void checkMsg();
 String composeMsg();
 
};

extern AppCommunicationTask appCommunicationTask;
#endif
