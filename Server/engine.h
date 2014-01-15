#ifndef ENGINE_H
#define ENGINE_H

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
        int const pwm;
        int const in1;
        int const in2;
    } front;
    
    struct {
        int const pwm;
        int const in1;
        int const in2;
    } rear;
    
    struct {
        struct timeval ticks[NB_TICK];
        int pos;
    } stats;
    
    int speed;
    
} Engine;

extern void Engine_initialise(Engine *E,
        int const front_pwm,
        int const front_in1,
        int const front_in2,
        int const rear_pwm,
        int const rear_in1,
        int const rear_in2);

extern void Engine_set_speed(int speed);

extern void Engine_update_instantaneous_speed(Engine *E);

extern double Engine_get_instantaneous_speed(Engine *E);

#endif