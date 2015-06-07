/**
 * Combination of dhtreader.c and DHT11.c
 * 
 * JNI based on dhtreader.c from greenhouse project
 * https://bitbucket.org/Temdegon/greenhouse/
 * at src/main/java/com/epam/llpd/greenhouse/sensor/dhtreader.c
 *
 * Algorithm to read the values from the DHT11.c from the sundounder kit.
 * https://github.com/sunfounder/Sunfounder_SensorKit_C_code_for_RaspberryPi
 * at exercise 20_DHT11
 *
 * @author marcandreuf
 */

#include <wiringPi.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include "DHT11SensorReader.h"

#define MAXTIMINGS 85

int dht11_dat[5] = {0, 0, 0, 0, 0};

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    if (wiringPiSetup() == -1) {
        exit(1);
    }
    return JNI_VERSION_1_6;
}

int readDHT(int dhtPin, float *temp_p, float *hum_p) {
    uint8_t laststate = HIGH;
    uint8_t counter = 0;
    uint8_t j = 0, i;
    dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] = 0;

    // pull pin down for 18 milliseconds
    pinMode(dhtPin, OUTPUT);
    digitalWrite(dhtPin, LOW);
    delay(18);
    // then pull it up for 40 microseconds
    digitalWrite(dhtPin, HIGH);
    delayMicroseconds(40);
    // prepare to read the pin
    pinMode(dhtPin, INPUT);

    // detect change and read data
    for (i = 0; i < MAXTIMINGS; i++) {
        counter = 0;
        while (digitalRead(dhtPin) == laststate) {
            counter++;
            delayMicroseconds(1);
            if (counter == 255) {
                break;
            }
        }
        laststate = digitalRead(dhtPin);

        if (counter == 255) break;

        // ignore first 3 transitions
        if ((i >= 4) && (i % 2 == 0)) {
            // shove each bit into the storage bytes
            dht11_dat[j / 8] <<= 1;
            if (counter > 16)
                dht11_dat[j / 8] |= 1;
            j++;
        }
    }

    // check we read 40 bits (8bit x 5 ) + verify checksum in the last byte
    // print it out if data is good
    if ((j >= 40) && 
            (dht11_dat[4] == 
            ((dht11_dat[0]+dht11_dat[1]+dht11_dat[2]+dht11_dat[3])&0xFF))
       ) {
        *temp_p = (float) dht11_dat[2];
        *hum_p = (float) dht11_dat[0];
        return 1;
    } else {
        return 0;
    }
}

JNIEXPORT jfloatArray JNICALL Java_org_mandfer_dht11_DHT11SensorReader_readData
(JNIEnv *env, jobject obj, jint gpio_pin) {
    jfloatArray j_result = (*env)->NewFloatArray(env, 2);
    jfloat result[2];
    float t, h;
    int pin = (int) gpio_pin;
    int rs = 0;
    int errorCounter = 0;
    do {
        rs = readDHT(pin, &t, &h);        
        if (errorCounter > 10) {
            t = -999;
            h = -999;
            break;
        }else{
            errorCounter++;
        }        
    }while (rs == 0);    
    result[0] = (jfloat) t;
    result[1] = (jfloat) h;
    (*env)->SetFloatArrayRegion(env, j_result, 0, 2, result);
    return j_result;
}

