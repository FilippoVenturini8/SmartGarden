#include "digital_led.h"
#include "digital_led_impl.h"
#include "analog_led.h"
#include "analog_led_impl.h"
#include "servo_motor.h"
#include "servo_motor_impl.h"

DigitalLed* digitalLed1;
DigitalLed* digitalLed2;
AnalogLed* analogLed1;
AnalogLed* analogLed2;
ServoMotor* servoMotor;

void setup() {
  digitalLed1 = new DigitalLedImpl(8);
  digitalLed2 = new DigitalLedImpl(7);
  analogLed1 = new AnalogLedImpl(6);
  analogLed2 = new AnalogLedImpl(5);
  servoMotor = new ServoMotorImpl(3);
  Serial.begin(9600);
}

void loop() {
  for(int i=0; i<255; i++){
    analogLed1->setIntensity(i);
    analogLed2->setIntensity(i);
    delay(10);
  }
  servoMotor->on();
  servoMotor->setPosition(180);
  delay(500);
  servoMotor->setPosition(0);
  delay(500);
  servoMotor->off();
  digitalLed1->switchOn();
  delay(500);
  digitalLed1->switchOff();
  delay(500);
  digitalLed2->switchOn();
  delay(500);
  digitalLed2->switchOff();
  delay(500);
}
