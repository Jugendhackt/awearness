#include <RFduinoBLE.h>
#define DATA 2
#define CONNECT 3
#define DISCONNECT 4

void setup() {
  pinMode(DATA , OUTPUT);   
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
  
  if(command = 0x01) {
    digitalWrite(DATA, HIGH); delay(500); digitalWrite(DATA, LOW);
  }
}

/***********************
* Connect/Disconnect lights
* for debugging
************************/
void RFduinoBLE_onConnect() {
  digitalWrite(DATA, HIGH);
  delay(1000);
  digitalWrite(DATA, LOW);
}
void RFduinoBLE_onDisconnect(){
  digitalWrite(DISCONNECT, HIGH);
  delay(1000);
  digitalWrite(DISCONNECT, LOW);
}
