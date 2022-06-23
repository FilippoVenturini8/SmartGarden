#include "app_communication_task.h"
#include "shared_variables.h"
#include "MsgServiceBT.h"
#include "StringSplitter.h"
#include <Arduino.h>
#include <LiquidCrystal_I2C.h>
LiquidCrystal_I2C lcd = LiquidCrystal_I2C(0x27,20,4); 

MsgServiceBT msgService(2,3);

AppCommunicationTask::AppCommunicationTask(){
  this->state = RECEIVING;
  msgService.init();
  lcd.init();
  lcd.backlight();
}
 
void AppCommunicationTask::init(int period){
  Task::init(period);
}

/*String splitMsg(String str, char sep, int index)
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
}*/

String AppCommunicationTask::composeMsg(){
  return String(switchDL1)+"|"+String(switchDL2)+"|"+String(valueAL1)+"|"+String(valueAL2)+"|"+String(openIrrigation)+"|"+String(irrigationSpeed);
}

void AppCommunicationTask::tick(){
  switch(state){
    case RECEIVING:
      msgService.sendMsg(composeMsg());
      if (msgService.isMsgAvailable()) {
        //lcd.clear();
        Msg* msg = msgService.receiveMsg();
        String msgContent = msg->getContent();
        
        /*String dL1 = splitMsg(msgContent, '|', 1);  
        String dL2 = splitMsg(msgContent, '|', 2);
        String aL1 = splitMsg(msgContent, '|', 3);
        String aL2 = splitMsg(msgContent, '|', 4);
        String openIrrigationStr = splitMsg(msgContent, '|', 5);
        String irrigationSpeedStr = splitMsg(msgContent, '|', 6);
        String modalityStr = splitMsg(msgContent, '|', 7);*/
        StringSplitter *splitter = new StringSplitter(msgContent, '|', 7);
        String dL1 = splitter->getItemAtIndex(0);
        String dL2 = splitter->getItemAtIndex(1);
        String aL1 = splitter->getItemAtIndex(2);
        String aL2 = splitter->getItemAtIndex(3);
        String openIrrigationStr = splitter->getItemAtIndex(4);
        String irrigationSpeedStr = splitter->getItemAtIndex(5);
        String modalityStr = splitter->getItemAtIndex(6);

        lcd.print(msgContent);
         
        if(!dL1.equals("-1")){
          switchDL1 = dL1.toInt();
        }
        if(!dL2.equals("-1")){
          switchDL2 = dL2.toInt();
        }
        if(!aL1.equals("-1")){
          valueAL1 = aL1.toInt();
        }
        if(!aL2.equals("-1")){
          valueAL2 = aL2.toInt();
        }
        if(!openIrrigationStr.equals("-1")){
          openIrrigation = openIrrigationStr.toInt();
        }
        if(!irrigationSpeedStr.equals("-1")){
          irrigationSpeed = irrigationSpeedStr.toInt();
        }
        if(!modalityStr.equals("-1")){
          modality = modalityStr.toInt();
        }
     }
     break;
  }
}
