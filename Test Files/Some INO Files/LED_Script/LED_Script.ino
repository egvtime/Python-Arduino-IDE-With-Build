int button = 15;
int led = 16;
void setup(){
		pinMode(button,INPUT);
		pinMode(led,OUTPUT);
}
void loop(){
	int buttonstate = digitalRead(button);
	if(buttonstate == 1) 
	{
		digitalWrite(led,HIGH);
	}
	else 
	{
		digitalWrite(led,LOW);
	}
}
