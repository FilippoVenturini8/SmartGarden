#include <WiFi.h>
#include <HTTPClient.h>

#include "digital_led.h"
#include "digital_led_impl.h"
#include "temp_sensor.h"
#include "temp_sensor_impl.h"
#include "photoresistor.h"
#include "photoresistor_impl.h"

#include "ArduinoJson.h"

const char* ssid = "TIM-22562475";
const char* password = "o98UxyVtTv5Uc5z1ks5cIp8t";

const char *serviceURI = "http://192.168.1.96:8080";

DigitalLed* led;
TempSensor* tempSensor;
Photoresistor* photoresistor;

bool requestData = false;
bool isInAlarm = false;

void connectToWifi(const char* ssid, const char* password){
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
}

void setup() {
  led = new DigitalLedImpl(4);
  tempSensor = new TempSensorImpl(32);
  photoresistor = new PhotoresistorImpl(35);
  led->switchOn();
  Serial.begin(115200); 
  connectToWifi(ssid, password);
}

int sendData(String address, float temperature, int lux){  
  
   HTTPClient http;    
   http.begin(address + "/api/data");      
   http.addHeader("Content-Type", "application/json");    
    
   String msg = 
    String("{ \"temperature\": ") + String(temperature) + 
    ", \"lux\": " + String(lux) +" }";
   
   int retCode = http.POST(msg);   
   http.end();  
      
   return retCode;
}

void receiveData(String address){
  HTTPClient http;
  
  http.begin(address + "/api/data");
      
  // Send HTTP GET request
  int httpResponseCode = http.GET();
    
  if (httpResponseCode>0) {
    //Serial.print("HTTP Response code: ");
    //Serial.println(httpResponseCode);
    String payload = http.getString();
    DynamicJsonDocument doc(1024);
    deserializeJson(doc, payload);
    const String modality = doc[0]["modality"];
    //Serial.println(modality);
    if(modality.equals("ARM") and !isInAlarm){
      led->switchOff();
      isInAlarm = true;
    }

    if(!modality.equals("ARM") and isInAlarm){
      led->switchOn();
      isInAlarm = false;
      requestData = false;
    }
  } else {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
  
  // Free resources
  http.end();
}

void loop() {
  if (WiFi.status()== WL_CONNECTED){      
    int lux = photoresistor->getLux();
    float temp = tempSensor->getTemperature();
    int mappedTemp = map(temp, 27.0, 35.0, 1, 5);
    if(mappedTemp == 5){
      requestData = true;
    }
    requestData = true;
    int code = sendData(serviceURI, mappedTemp, lux);
    if (code == 200){
       //Serial.println("ok");   
    } else {
       //Serial.println(String("error: ") + code);
    }
    
    delay(1000);

    if(requestData){
      receiveData(serviceURI);
    }

  } else {
    Serial.println("WiFi Disconnected... Reconnect.");
    connectToWifi(ssid, password);
  }
}
