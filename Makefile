.PHONY: all build clean 

default: 
	mvn clean compile assembly:single

clean:
	mvn clean

build:
	mvn compile assembly:single
