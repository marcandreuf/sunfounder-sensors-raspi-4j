#!/bin/bash
# 
# Script to compile and install the library in the raspberry pi java.library.path
# 
# Default location of java armhf for raspberry pi 
# !! Needs to be an openjdk, looks like the oragle does not have jni capabilities. !!
#
# Run this script from src/main/java location. Top root of packages names.
# sudo sh jniDHT11SensorReaderBuilder.sh 
#
# @author: marcandreuf
#

echo "Compile java class org/mandfer/dht11/DHT11SensorReader.java"
javac org/mandfer/dht11/DHT11SensorReader.java

echo "Create JNI header file DHT11SensorReader.h"
javah -d org/mandfer/dht11 org.mandfer.dht11.DHT11SensorReader

echo "Simplify header class name to org/mandfer/dht11/DHT11SensorReader.h"
mv org/mandfer/dht11/org_mandfer_dht11_DHT11SensorReader.h org/mandfer/dht11/DHT11SensorReader.h

echo "Compile org/mandfer/dht11/dht11sensor.c with wiringPi library"
gcc -I /usr/lib/jvm/java-1.7.0-openjdk-armhf/include/ -o org/mandfer/dht11/dht11sensor.so -shared org/mandfer/dht11/dht11sensor.c -l wiringPi

echo "Copy new org/mandfer/dht11/dht11sensor.so in /usr/lib/"
sudo cp org/mandfer/dht11/dht11sensor.so /usr/lib/

echo "Create symbolic link for the library"2
sudo ln -s /usr/lib/dht11sensor.so /usr/lib/libdht11sensor.so

echo "Updated library:"
ls -lart /usr/lib/libdht*

# Run the example class with 
#sudo java org.mandfer.dht11.DHT11SensorReader
# 
# The output should be similar to
# INFO: temp: 22.0, hum 35.0


