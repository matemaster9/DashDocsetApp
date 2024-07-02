#!/bin/zsh

rm -f ./app/DashDocsetApp-1.0.2.jar

cp -f ./target/DashDocsetApp-1.0.2.jar ./app/

/Library/Java/JavaVirtualMachines/jdk-17.0.9.jdk/Contents/Home/bin/java -jar \
./app/DashDocsetApp-1.0.2.jar \
--spring.config.location=./app/application.yml