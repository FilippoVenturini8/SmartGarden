#include "app_communication_task.h"
#include "shared_variables.h"
#include <Arduino.h>

SoftwareSerial channel(2, 3);
bool isMsgAvailable;
String msg;

AppCommunicationTask::AppCommunicationTask(){
  this->state = RECEIVING;
  this->isMsgAvailable = false;
}
 
void AppCommunicationTask::init(int period){
  Task::init(period);
  channel.begin(9600);
}

String splitMsg(String str, char sep, int index)
{
 int found = 0;
 int strIdx[] = { 0, -1 };
 int maxIdx = str.length() - 1;

 for (int i = 0; i <= maxIdx && found <= index; i++)
 {
    if (str.charAt(i) == sep || i == maxIdx)
    {
      found++;
      strIdx[0] = strIdx[1] + 1;
      strIdx[1] = (i == maxIdx) ? i+1 : i;
    }
 }
 return found > index ? str.substring(strIdx[0], strIdx[1]) : "";
}

void AppCommunicationTask::checkMsg(){
  msg = "";
  while (channel.available()) {
    char ch = (char) channel.read();
    if (ch == '\n'){ 
      isMsgAvailable = true;  
    } else {
      msg += ch;      
    }
  }
}

void AppCommunicationTask::tick(){
  switch(state){
    case RECEIVING:
      checkMsg();
      if (isMsgAvailable) { 
        String dL1 = splitMsg(msg, '|', 0);  
        String dL2 = splitMsg(msg, '|', 1);
        String aL1 = splitMsg(msg, '|', 2);
        String aL2 = splitMsg(msg, '|', 3);
        String openIrrigationStr = splitMsg(msg, '|', 4);
        String irrigationSpeedStr = splitMsg(msg, '|', 5);
    
        if(dL1 != "-1"){
          switchDL1 = dL1.toInt();
        }
        if(dL2 != "-1"){
          switchDL2 = dL2.toInt();
        }
        if(aL1 != "-1"){
          valueAL1 = aL1.toInt();
        }
        if(aL2 != "-1"){
          valueAL2 = aL2.toInt();
        }
        if(openIrrigationStr != "-1"){
          openIrrigation = openIrrigationStr.toInt();
        }
        if(irrigationSpeedStr != "-1"){
          irrigationSpeed = irrigationSpeedStr.toInt();
        }
        isMsgAvailable = false;
     }
     break;
  }
}
