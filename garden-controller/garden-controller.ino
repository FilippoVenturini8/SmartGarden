#include "digital_led.h"
#include "digital_led_impl.h"
#include "analog_led.h"
#include "analog_led_impl.h"
#include "servo_motor.h"
#include "servo_motor_impl.h"

DigitalLed* digitalLed;
AnalogLed* analogLed;
ServoMotor* servoMotor;

void setup() {
  digitalLed = new DigitalLedImpl(7);
  analogLed = new AnalogLedImpl(6);
  servoMotor = new ServoMotorImpl(3);
  Serial.begin(9600);
}

void loop() {
  digitalLed->switchOn();
  for(int i=0; i<255; i++){
    analogLed->setIntensity(i);
    delay(10);
  }
  /*servoMotor->on();
  servoMotor->setPosition(180);
  delay(500);
  servoMotor->setPosition(0);
  delay(500);
  servoMotor->off();*/
  delay(500);
  digitalLed->switchOff();
  delay(500);
}
