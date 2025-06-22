bool GameGoing = true;

int pirPin = 12;                  
int pirStat = 0; 
bool greenlight = true;

int GreenPin = 10;
int RedPin = 9;

int seconds = 0;

int ButtonPin = 7;
int buttonState = 0;

int Piezo = 13;

void setup() { 
  pinMode(ButtonPin, INPUT);
  pinMode(pirPin, INPUT);  
  pinMode(GreenPin, OUTPUT);  
  pinMode(RedPin, OUTPUT);  
  pinMode(Piezo, OUTPUT);
  Serial.begin(9600);
}

void loop(){
  if(GameGoing == true){
    digitalWrite(Piezo, LOW);
    Game();
  }else{
  	Button();
  }
} 

void Game(){
 if(greenlight == true){
   digitalWrite(GreenPin, HIGH);
   digitalWrite(RedPin, 0);
 	GreenLight();
   
  buttonState = digitalRead(ButtonPin);

  if (buttonState == HIGH) {
    GameGoing = false;
    Serial.println("Game Won");
  } 
 }
 if(greenlight == false){
   digitalWrite(GreenPin, 0);
   digitalWrite(RedPin, 1);
   delay(2000);
  	RedLight();
 }
}

void GreenLight(){  
  	delay(random(1000, 10000));
  	
  	greenlight = false;
}

void RedLight(){
  seconds++;
 pirStat = digitalRead(pirPin); 
 if (pirStat == HIGH) {            
   Serial.println("Motion Detected!"); 
   GameGoing = false;
 } 
 else {
   Serial.println(seconds);
 }
  if(seconds >= 10){
    seconds = 0;
    greenlight = true;
  }
}


void Button(){
buttonState = digitalRead(ButtonPin);
Serial.println("Game Lost");
  digitalWrite(Piezo, HIGH);
  if (buttonState == HIGH) {
    GameGoing = true;
  } 
}

