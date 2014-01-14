#include <stdio.h>
#include <stdlib.h>
#include <wiringPi.h>
#include <math.h>
#include <sys/time.h>

#include "roboid-1.0.h"

#define sign(X) (((X) > 0f) - ((X) < 0f))
#define min(X, Y) ((X) < (Y) ? X : Y)
#define max(X, Y) ((X) > (Y) ? X : Y)

/**
 * Simple usefull enum
 */
typedef enum {
    ENGINE_LEFT,
    ENGINE_RIGHT
} Engine;

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

// Number of ticks for a complete rotation of a wheel
#define NB_TICK_TOTAL 10
// Number of ticks used to calculate the actual speed
#define NB_TICK 5
// NB_TICK_TOTAL / NB_TICK (to avoid useless calculation)
#define NB_TICK_RATIO 2
// The circumference os a wheel in m
#define WHEEL_CIRCUMFERENCE 0.20420352248333656049 // (2 * MATH_PI * 3.25 / 100) m

/**
 * Structure containing data for the current speed calculation.
 */
struct {
	struct timeval left[NB_TICK];
	int left_pos;
	struct timeval right[NB_TICK];
	int right_pos;
} SpeedStats;

/**
 * Listeners for tick event on encoders (when the pin goes from 0 to 1).
 */
static void left_speed_listener(void);
static void right_speed_listener(void);
static void reset_speed_stats(struct timeval*);

/**
 * Max value to set the speed (not related to the actual speed).
 * The speed should be between -MAX and MAX.
 */
#define MAX_SPEED 100

/**
 * Required speed for each engines.
 */
static int speed[2] = { 0, 0 };

// dummy var needed to create a thread for each engine.
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
    int id = new_engine;
    new_engine = -1;
    struct engine_s *F = &(Roboid.engines[id]);
    struct engine_s *R = &(Roboid.engines[id + 2]);
    
    pinMode(F->pwm, OUTPUT);
    pinMode(R->pwm, OUTPUT);
    digitalWrite(F->pwm, LOW);
    digitalWrite(R->pwm, LOW);
    
    pinMode(F->in1, OUTPUT);
    pinMode(R->in1, OUTPUT);
    digitalWrite(F->in1, HIGH);
    digitalWrite(R->in1, HIGH);
    
    pinMode(F->in2, OUTPUT);
    pinMode(R->in2, OUTPUT);
    digitalWrite(F->in2, LOW);
    digitalWrite(R->in2, LOW);
    
    piHiPri(50);
    for (;;) {
        s = speed[id];
        d = MAX_SPEED - abs(s);
        
        if (s > 0 && F->signum < 1) {
            digitalWrite(F->in1, HIGH);
            digitalWrite(F->in2, LOW);
            digitalWrite(R->in1, HIGH);
            digitalWrite(R->in2, LOW);
            F->signum = 1;
        }
        else if (s < 0 && F->signum > -1) {
            digitalWrite(F->in1, LOW);
            digitalWrite(F->in2, HIGH);
            digitalWrite(R->in1, LOW);
            digitalWrite(R->in2, HIGH);
            F->signum = -1;
        }
        digitalWrite(F->pwm, HIGH - (s == 0));
        digitalWrite(R->pwm, HIGH - (s == 0));
        delayMicroseconds(abs(s) * 100);
        
        digitalWrite(F->pwm, LOW + (d == 0));
        digitalWrite(R->pwm, LOW + (d == 0));
        delayMicroseconds(d * 100);
    }
    
    return NULL;
}

/**
 * Create a new engine.
 * 
 * Define pin mode for the engine pins and start the engine controller thread.
 */
static inline int createEngine(Engine e) {
    int res;
    
    speed[e] = 0;
    new_engine = e;
    res = piThreadCreate(engineController);
    
    while (new_engine != -1) {
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
    
    createEngine(ENGINE_LEFT);
    createEngine(ENGINE_RIGHT);
    
    //reset_speed_stats((struct timeval*) &(Roboid.encoder.left));
    //reset_speed_stats((struct timeval*) &(Roboid.encoder.right));
    
    pinMode(Roboid.encoder.left, INPUT);
    //wiringPiISR(Roboid.encoder.left, INT_EDGE_RISING, left_speed_listener);
    pinMode(Roboid.encoder.right, INPUT);
    //wiringPiISR(Roboid.encoder.right, INT_EDGE_RISING, right_speed_listener);
}

/**
 * Finalization
 */
void haltRoboid(void) {
    haltEngine(ENGINE_LEFT);
    haltEngine(ENGINE_RIGHT);
}

/**
 * Set speed for the two left engines.
 */
void setLeftSpeed(signed char value) {
	value = max(-100, min(100, value));
	speed[ENGINE_LEFT] = value;
}

/**
 * Set speed for the two right engines.
 */
void setRightSpeed(signed char value) {
	value = max(-100, min(100, value));
	speed[ENGINE_RIGHT] = value;
}

/**
 * Listeners for tick event on encoders (when the pin goes from 0 to 1).
 */
static void left_speed_listener(void) {
    printf("tick\n");
	gettimeofday(&(SpeedStats.left[SpeedStats.left_pos]), NULL);
	SpeedStats.left_pos = (SpeedStats.left_pos + 1) % NB_TICK;
}

/**
 * Listeners for tick event on encoders (when the pin goes from 0 to 1).
 */
static void right_speed_listener(void) {
	gettimeofday(&(SpeedStats.right[SpeedStats.right_pos]), NULL);
	SpeedStats.right_pos = (SpeedStats.right_pos + 1) % NB_TICK;
}

/**
 * Return the actual speed for the specified wheel (encoder)
 */
static double get_speed(struct timeval *ticks, int start) {
	int i;
	struct timeval step;
	double speed = 0;

	for (i = 1; i < NB_TICK; i++) {
        timersub(&(ticks[(i + start) % NB_TICK]), &(ticks[(i - 1 + start) % NB_TICK]), &step);
		speed += (double) step.tv_sec + ((double) step.tv_usec / 1000000.);
	}

    if (speed < 0.01 && speed > -0.01) {
        return 0.;
    }
	return WHEEL_CIRCUMFERENCE / (speed * NB_TICK_RATIO);
}

/**
 * Return the left (right) speed in m/s.
 */
double get_left_speed(void) {
    return get_speed(SpeedStats.left, SpeedStats.left_pos);
}
double get_right_speed(void) {
    return get_speed(SpeedStats.right, SpeedStats.right_pos);
}

static void reset_speed_stats(struct timeval *ticks) {
	int i;

	for (i = 0; i < NB_TICK; i++) {
		ticks[i].tv_sec = 0;
		ticks[i].tv_usec = 0;
	}
}
