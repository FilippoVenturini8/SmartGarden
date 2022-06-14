#include "digital_led.h"
#include "digital_led_impl.h"
#include "temp_sensor.h"
#include "temp_sensor_impl.h"
#include "photoresistor.h"
#include "photoresistor_impl.h"

DigitalLed* led;
TempSensor* tempSensor;
Photoresistor* photoresistor;

void setup() {
  led = new DigitalLedImpl(4);
  tempSensor = new TempSensorImpl(34);
  photoresistor = new PhotoresistorImpl(35);
  Serial.begin(9600);
}

void loop() {
  /*led->switchOn();
  delay(1000);
  led->switchOff();
  delay(1000);*/
  //Serial.println(tempSensor->getTemperature());
  Serial.println(photoresistor->getLux());
  delay(100);
}