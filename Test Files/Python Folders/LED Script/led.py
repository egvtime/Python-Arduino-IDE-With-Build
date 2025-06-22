from arduino import *
button = 15
led = 16

def setup():
	pinmode(button, INPUT)
	pinmode(led, OUTPUT)

def loop():
	buttonstate = digitalread(button)
	if buttonstate == 1:
		digitalwrite(led, HIGH)
	else:
		digitalwrite(led, LOW)

run()
