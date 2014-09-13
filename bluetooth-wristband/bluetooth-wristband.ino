#include <RFduinoBLE.h>
#define ACTION 2
#define CONNECT 3
#define DISCONNECT 4

void setup() {
  pinMode(ACTION , OUTPUT);   
  pinMode(DISCONNECT, OUTPUT);
  pinMode(CONNECT, OUTPUT);
  
  RFduinoBLE.deviceName = "awearable";
  RFduinoBLE.begin();  
}

void loop() {
  RFduino_ULPDelay(INFINITE);
}


void RFduinoBLE_onReceive(char *data, int len){
  uint8_t command = data[0];
  // echo command for debugging
  RFduinoBLE.sendByte(command);

  if(command == 0x01) {
    digitalWrite(ACTION, HIGH); delay(500); digitalWrite(ACTION, LOW);
  } else if(command == 0xFF) {
    digitalWrite(ACTION, HIGH);
  } else if(command == 0x00) {
    digitalWrite(ACTION, LOW);
  }
}

/***********************
* Connect/Disconnect lights
* for debugging
************************/
void RFduinoBLE_onConnect() {
  digitalWrite(CONNECT, HIGH);
  delay(1000);
  digitalWrite(CONNECT, LOW);
}
void RFduinoBLE_onDisconnect(){
  digitalWrite(DISCONNECT, HIGH);
  delay(1000);
  digitalWrite(DISCONNECT, LOW);
}
