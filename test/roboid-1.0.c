#include <stdlib.h>
#include <wiringPi.h>

#include "roboid-1.0.h"

typedef struct {
    const int pwm;
    const int in1;
    const int in2;
} Engine;

typedef struct {
    const int left;
    const int right;
} Encoder;

struct {
    Encoder encoder;
    Engine engines[];
} Roboid = {
    { 4, 5 }, // Encoder { LEFT, RIGHT }
    {
        {  0,  3,  2 }, // Front Left  { PWM, IN1, IN2 }
        { 15, 16,  1 }, // Front Right { PWM, IN1, IN2 }
        {  6, 11, 10 }, // Rear Left   { PWM, IN1, IN2 }
        { 12, 13, 14 }  // Rear Right  { PWM, IN1, IN2 }
    }
};

void startRoboid(void) {
    Engine *E;
    int i;
    
    wiringPiSetup();

    for (i = 0; i < 4; i++) {
        E = &(Roboid.engines[i]);
        pinMode(E->pwm, OUTPUT);
        pinMode(E->in1, OUTPUT);
        pinMode(E->in2, OUTPUT);
    }
    
    pinMode(Roboid.encoder.left, INPUT);
    pinMode(Roboid.encoder.right, INPUT);
}

static void haltEngine(Engine *E) {
    digitalWrite(E->pwm, LOW);
    digitalWrite(E->in1, LOW);
    digitalWrite(E->in2, LOW);
}

void haltRoboid(void) {
    int i;
    for (i = 0; i < 4; i++) {
        haltEngine(&(Roboid.engines[i]));
    }
}

static void moveForwardEngine(Engine *E, unsigned int Th, unsigned int T) {
    digitalWrite(E->in1, HIGH);
    digitalWrite(E->in2, LOW);
    
    for(;;) {
        digitalWrite(E->pwm, HIGH);
        delay(Th);
        digitalWrite(E->pwm, LOW);
        delay(T);
    }
}

void moveForward(unsigned int Th, unsigned int T) {
    moveForwardEngine(&(Roboid.engines[0]), Th, T);
}