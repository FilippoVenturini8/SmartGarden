#include "app_communication_task.h"
#include "shared_variables.h"
#include "MsgServiceBT.h"
#include "StringSplitter.h"
#include <Arduino.h> 

MsgServiceBT msgService(2,3);

AppCommunicationTask::AppCommunicationTask(){
  this->state = IDLE_STATE;
  this->tryReceiving = false;
  this->messageSent = false;
  this->lastUpdateTime = millis();
}
 
void AppCommunicationTask::init(int period){
  Task::init(period);
  msgService.init();
  while (!Serial){}
}

String AppCommunicationTask::composeMsg(){
  return String(switchDL1)+"|"+String(switchDL2)+"|"+String(valueAL1)+"|"+String(valueAL2)+"|"+String(openIrrigation)+"|"+String(irrigationSpeed)+"|"+String(modality);
}

void AppCommunicationTask::tick(){
  switch(state){
    case IDLE_STATE:
      if(messageSent and millis() - lastUpdateTime < UPDATE_TIME){
        break;
      }
      messageSent = false;
      if(tryReceiving){
        state = RECEIVING;
        tryReceiving = false;
      }else{
        state = SENDING;
        tryReceiving = true;
      }
      break;
    case RECEIVING:
      if (msgService.isMsgAvailable()) {
        Msg* msg = msgService.receiveMsg();
        String msgContent = msg->getContent();
        
        StringSplitter *splitter = new StringSplitter(msgContent, '|', 7);

        if(splitter->getItemCount() != 7){
          state = IDLE_STATE;
          delete msg;
          break;
        }
        
        String dL1 = splitter->getItemAtIndex(0);
        String dL2 = splitter->getItemAtIndex(1);
        String aL1 = splitter->getItemAtIndex(2);
        String aL2 = splitter->getItemAtIndex(3);
        String openIrrigationStr = splitter->getItemAtIndex(4);
        String irrigationSpeedStr = splitter->getItemAtIndex(5);
        String modalityStr = splitter->getItemAtIndex(6);
         
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
        if(modalityStr.equals("MAN") or modalityStr.equals("AUT") or modalityStr.equals("ARM")){
          modality = modalityStr;
        }
        delete msg;
        delete splitter;
     }
     state = IDLE_STATE;
     break;
   case SENDING:
      msgService.sendMsg(Msg(composeMsg()));
      messageSent = true;
      lastUpdateTime = millis();
      state = IDLE_STATE;
      break;
  }
}
