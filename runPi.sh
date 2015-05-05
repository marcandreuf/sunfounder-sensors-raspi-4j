#!/bin/bash

mainClass=$1

echo
echo " ----------------- Lets run this awesome pi ------------------"
echo "Running: $mainClass"

sudo java -cp "target/classes:/opt/pi4j/lib/*:target/*" $mainClass
