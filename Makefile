.PHONY: clean build

default: build

clean:
	@printf "\nCleaning letlog...\n\n"
	mvn clean

build:
	@printf "\nBuilding letlog...\n\n"
	mvn clean compile assembly:single

version = 0.0.9
releasePath = letlog-$(version)_release

release-win:
	@printf "\nGenerating Windows release...\n\n"
	rm -rf ./$(releasePath)-win

	mkdir ./$(releasePath)-win
	mkdir ./$(releasePath)-win/bin

	cp -r ./images ./$(releasePath)-win/

	cp ./target/letlog-1-jar-with-dependencies.jar ./$(releasePath)-win/bin/letlog-$(version).jar
	cp ./letlog.conf ./$(releasePath)-win/letlog.conf

#generates the run.bat script
	@echo "start javaw.exe -jar .\bin\letlog-$(version).jar" > ./$(releasePath)-win/run.bat