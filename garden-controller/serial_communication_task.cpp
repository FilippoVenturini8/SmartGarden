#include "serial_communication_task.h"
#include "shared_variables.h"
#include <Arduino.h>
#include <LiquidCrystal_I2C.h>

bool msgAvailable;
String receivedMsg;
LiquidCrystal_I2C lcd = LiquidCrystal_I2C(0x27,20,4); 

SerialCommunicationTask::SerialCommunicationTask(){
  state = WAITING;
  msgAvailable = false;
  receivedMsg = "";
  lcd.init();
  lcd.backlight();
  lcd.setCursor(2, 1);
}
 
void SerialCommunicationTask::init(int period){
  Task::init(period);
}
 
void SerialCommunicationTask::tick(){
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

void SerialCommunicationTask::readMsg(){
  if(receivedMsg != ""){
    lcd.print(receivedMsg);
  }
  switchDL1 = true;
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
