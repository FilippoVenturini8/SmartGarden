#include "service_communication_task.h"
#include "shared_variables.h"
#include <Arduino.h>
#include <LiquidCrystal_I2C.h>

bool msgAvailable;
String receivedMsg;
//LiquidCrystal_I2C lcd = LiquidCrystal_I2C(0x27,20,4); 

ServiceCommunicationTask::ServiceCommunicationTask(){
  state = WAITING;
  msgAvailable = false;
  receivedMsg = "";
  //lcd.init();
  //lcd.backlight();
}
 
void ServiceCommunicationTask::init(int period){
  Task::init(period);
}
 
void ServiceCommunicationTask::tick(){
  switch(state){    
    case WAITING: 
      if(msgAvailable){
        state = RECEIVE;
      }
      break;
      
    case RECEIVE:
      readMsg();
      state = WAITING;
      break;
  }
  
}

String splitString(String str, char sep, int index)
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

void ServiceCommunicationTask::readMsg(){
  if(receivedMsg != ""){
    //lcd.clear();
    //lcd.print(receivedMsg);
    String dL1 = splitString(receivedMsg, '|', 0);
    String dL2 = splitString(receivedMsg, '|', 1);
    String aL1 = splitString(receivedMsg, '|', 2);
    String aL2 = splitString(receivedMsg, '|', 3);
    String openIrrigationStr = splitString(receivedMsg, '|', 4);
    String irrigationSpeedStr = splitString(receivedMsg, '|', 5);

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
  }
  msgAvailable = false;
  receivedMsg = "";
}

void serialEvent(){
  while(Serial.available()){
    char ch = (char) Serial.read();
    if (ch == '\n'){
      msgAvailable = true;      
    } else {
      receivedMsg += ch;      
    }
  }
}
