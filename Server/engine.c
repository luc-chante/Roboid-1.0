#include "engine.h"

#define min(X, Y) ((X) < (Y) ? X : Y)
#define max(X, Y) ((X) > (Y) ? X : Y)

static void Engine_clear_speed_stats(Engine *E) {
    int i;
    
    for (i = 0; i < NB_TICK; i++) {
        E->stats.ticks[i].tv_sec = 0;
        E->stats.ticks[i].tv_usec = 0;
    }
    E->stats.pos = 0;
}

Engine *dummy = NULL;

static PI_THREAD(Engine_start) {
    Engine *E;
    int sign, speed, delay;
    
    E = dummy;
    dummy = NULL;
    
    pinMode(E->front.pwm, OUTPUT);
    pinMode(E->front.in1, OUTPUT);
    pinMode(E->front.in2, OUTPUT);
    pinMode(E->rear.pwm, OUTPUT);
    pinMode(E->rear.in1, OUTPUT);
    pinMode(E->rear.in2, OUTPUT);
    
    digitalWrite(E->front.in1, LOW);
    digitalWrite(E->front.in2, HIGH);
    digitalWrite(E->rear.in1, LOW);
    digitalWrite(E->rear.in2, HIGH);
    
    sign = 1;
    
    piHiPri(50);
    while (1) {
        speed = E->speed;
        delay = MAX_SPEED - abs(s);
        
        if (speed > 0 && sign == -1) {
            digitalWrite(E->front.in1, HIGH);
            digitalWrite(E->front.in2, LOW);
            digitalWrite(E->rear.in1, HIGH);
            digitalWrite(E->rear.in2, LOW);
            sign = 1;
        }
        else if (speed < 0 && sign == 1) {
            digitalWrite(E->front.in1, LOW);
            digitalWrite(E->front.in2, HIGH);
            digitalWrite(E->rear.in1, LOW);
            digitalWrite(E->rear.in2, HIGH);
            sign = -1;
        }
        digitalWrite(E->front.pwm, HIGH - (speed == 0));
        digitalWrite(E->rear.pwm, HIGH - (speed == 0));
        delayMicroseconds(abs(s) * 100);
        
        digitalWrite(E->front.pwm, LOW + (delay == 0));
        digitalWrite(E->rear.pwm, LOW + (delay == 0));
        delayMicroseconds(delay * 100);
    }
    
    return NULL;
}

void Engine_initialise(Engine *E,
        int const front_pwm,
        int const front_in1,
        int const front_in2,
        int const rear_pwm,
        int const rear_in1,
        int const rear_in2) {
    E->front.pwm = front_pwm;
    E->front.in1 = front_in1;
    E->front.in2 = front_in2;
    E->rear.pwm = rear_pwm;
    E->rear.in1 = rear_in1;
    E->rear.in2 = rear_in2;
    
    Engine_clear_speed_stats(E);
    
    while (dummy != NULL) {
        delay(1);
    }
    
    dummy = E;
    piThreadCreate(Engine_start);
}

void Engine_set_speed(int speed) {
    if (speed == 0) {
        Engine_clear_speed_stats(E);
    }
    E->speed = max(-100, min(100, speed));
}

void Engine_update_instantaneous_speed(Engine *E) {
    gettimeofday(&(E->stats.ticks[E->stats.pos]), NULL);
	E->stats.pos = (E->stats.pos + 1) % NB_TICK;
}

double Engine_get_instantaneous_speed(Engine *E) {
    int i;
	struct timeval step;
	double speed = 0;

	for (i = 1; i < NB_TICK; i++) {
        timersub(
                &(E->stats.ticks[(i + E->stats.pos) % NB_TICK]),
                &(E->stats.ticks[(i - 1 + E->stats.pos) % NB_TICK]),
                &step);
		speed += (double) step.tv_sec + ((double) step.tv_usec / 1000000.);
	}

    if (speed < 0.01 && speed > -0.01) {
        return 0.;
    }
	return WHEEL_CIRCUMFERENCE / (speed * NB_TICK_RATIO);
}