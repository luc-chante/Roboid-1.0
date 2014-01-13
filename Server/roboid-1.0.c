#include <stdlib.h>
#include <wiringPi.h>
#include <math.h>

#include "roboid-1.0.h"

#define sign(X) (((X) > 0f) - ((X) < 0f))

/**
 * Structure of an engine.
 */
struct engine_s {
    const int pwm; /** PWM pin number */
    const int in1; /** IN1 pin number */
    const int in2; /** IN2 pin number */
    int signum;    /** The signum of the last specified speed; usefull for direction swap */
};

/**
 * Structure of encoders.
 */
struct encoder_s {
    const int left;  /** Left encoder pin number */
    const int right; /** Right encoder pin number */
};

/**
 * The structure of the robot.
 */
struct {
    struct encoder_s encoder;
    struct engine_s engines[];
} Roboid = {
    { 4, 5 }, // Encoder { LEFT, RIGHT }
    {
        {  0,  3,  2, 0 }, // Front Left  { PWM, IN1, IN2 }
        { 15, 16,  1, 0 }, // Front Right { PWM, IN1, IN2 }
        {  6, 11, 10, 0 }, // Rear Left   { PWM, IN1, IN2 }
        { 12, 13, 14, 0 }  // Rear Right  { PWM, IN1, IN2 }
    }
};

#define MAX_SPEED 100
static int *speed = { 0, 0, 0, 0 };
int new_engine = -1;

/**
 * Thread execution for each engine.
 * 
 * Do the engine running (forward and backward) or not.
 * It uses the global array speed indexed by the engine id {@see Engine}. The
 * specified speed should be between -MAX_SPEED and MAX_SPEED. -MAX_SPEED for
 * full backward moving, 0 for halted engine and MAX_SPEED for full forward
 * moving.
 */
static inline PI_THREAD(engineController) {
    int s, d;
    int const id = new_engine;
    new_engine = -1;
    struct engine_s const *E = &(Roboid[id]);
    
    piHiPri(50);
    for (;;) {
        s = speed[id];
        d = MAX_SPEED - abs(s);
        
        if (s > 0 && E->signum < 1) {
            digitalWrite(E->in1, HIGH);
            digitalWrite(E->in2, LOW);
        }
        else if (s < 0 && E->signum > -1) {
            digitalWrite(E->in1, LOW);
            digitalWrite(E->in2, HIGH);
        }
        digitalWrite(E->pwm, HIGH - (s == 0));
        delayMicroseconds(abs(s) * 100);
        
        digitalWrite(E->pwm, LOW + (d == 0));
        delayMicroseconds(d * 100);
    }
}

/**
 * Create a new engine.
 * 
 * Define pin mode for the engine pins and start the engine controller thread.
 */
static inline int createEngine(Engine e) {
    int res;
    struct engine_s *E = &(Roboid[e]);
    
    pinMode(E->pwm, OUPUT);
    digitalWrite(E->pwm, LOW);
    
    pinMode(E->in1, OUPUT);
    digitalWrite(E->in1, HIGH);
    
    pinMode(E->in2, OUPUT);
    digitalWrite(E->in2, LOW);
    
    speed[e] = 0;
    new_engine = e;
    res = piThreadCreate(engineController);
    
    while (new_engine == -1) {
        delay(1);
    }
    
    return res;
}

/**
 * Stop the specified engine.
 * 
 * Simply set the speed to 0.
 */
static inline void haltEngine(Engine e) {
    speed[e] = 0;
}

/**
 * Initialization
 */
void startRoboid(void) {
    
    wiringPiSetup();
    
    createEngine(ENGINE_FRONT_LEFT);
    createEngine(ENGINE_FRONT_RIGHT);
    createEngine(ENGINE_REAR_LEFT);
    createEngine(ENGINE_REAR_RIGHT);
    
    pinMode(Roboid.encoder.left, INPUT);
    pinMode(Roboid.encoder.right, INPUT);
}

/**
 * Finalization
 */
void haltRoboid(void) {
    haltEngine(ENGINE_FRONT_LEFT);
    haltEngine(ENGINE_FRONT_RIGHT);
    haltEngine(ENGINE_REAR_LEFT);
    haltEngine(ENGINE_REAR_RIGHT);
}
