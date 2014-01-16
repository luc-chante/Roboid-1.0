#ifndef ENGINE_H
#define ENGINE_H

#include <sys/time.h>

#define MAX_SPEED 100
// Number of ticks for a complete rotation of a wheel
#define NB_TICK_TOTAL 10
// Number of ticks used to calculate the actual speed
#define NB_TICK 5
// NB_TICK_TOTAL / NB_TICK (to avoid useless calculation)
#define NB_TICK_RATIO 2
// The circumference os a wheel in m
#define WHEEL_CIRCUMFERENCE 0.20420352248333656049 // (2 * MATH_PI * 3.25 / 100) m

typedef struct {
    struct {
        int pwm;
        int in1;
        int in2;
    } front;
    
    struct {
        int pwm;
        int in1;
        int in2;
    } rear;
    
    struct {
        struct timeval ticks[NB_TICK];
        int pos;
    } stats;
    
    int speed;
    
} Engine;

extern void Engine_initialise(Engine *E,
        int front_pwm, int front_in1, int front_in2,
        int rear_pwm, int rear_in1, int rear_in2);

extern void Engine_set_speed(Engine *E, int speed);

extern void Engine_update_instantaneous_speed(Engine *E);

extern double Engine_get_instantaneous_speed(Engine *E);

#endif
