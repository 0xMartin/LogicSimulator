#define BTN1 3
#define BTN2 4

#define OUT1 5

void setup() {
  Serial.begin(9600, SERIAL_8N1);
  
  pinMode(BTN1, INPUT_PULLUP);
  pinMode(BTN2, INPUT_PULLUP);

  pinMode(OUT1, OUTPUT);
}

void loop() {

  //btn 1
  if( digitalRead(BTN1) ){
    //off   
    Serial.write(B00000000);
  } else {
    //on
    Serial.write(B00000001);
  }

  //btn 1
  if( digitalRead(BTN2) ){
    //off   
    Serial.write(B00000010);
  } else {
    //on
    Serial.write(B00000011);
  }



  //out 1
  if (Serial.available() > 0) {
    byte incomingByte = Serial.read();

    if(incomingByte == B00000000) {
      digitalWrite(OUT1, LOW);  
    } else if(incomingByte == B00000001) {
      digitalWrite(OUT1, HIGH);   
    }
    
  }
  
}
