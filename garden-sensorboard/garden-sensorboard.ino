#include <WiFi.h>
#include <HTTPClient.h>

#include "digital_led.h"
#include "digital_led_impl.h"
#include "temp_sensor.h"
#include "temp_sensor_impl.h"
#include "photoresistor.h"
#include "photoresistor_impl.h"

const char* ssid = "TIM-22562475";
const char* password = "o98UxyVtTv5Uc5z1ks5cIp8t";

const char *serviceURI = "http://192.168.1.96:8080";

DigitalLed* led;
TempSensor* tempSensor;
Photoresistor* photoresistor;

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

void loop() {
  if (WiFi.status()== WL_CONNECTED){      
    int lux = photoresistor->getLux();
    //float temp = tempSensor->getTemperature();
    float temp = 20.0;
    int mappedTemp = map(temp, 17.0, 25.0, 1, 5);
    int code = sendData(serviceURI, mappedTemp, lux);
    if (code == 200){
       Serial.println("ok");   
    } else {
       Serial.println(String("error: ") + code);
    }
    
    delay(1000);

  } else {
    Serial.println("WiFi Disconnected... Reconnect.");
    connectToWifi(ssid, password);
  }
}
