#include <stdio.h>
#include <wiringPi.h>

#include "roboid-1.0.h"
#include "engine.h"

struct {
	Engine left;
	Engine right;
	struct {
		volatile int left;
		volatile int right;
	} encoders;
} Roboid;

/**
 * Initialize GPIO mode and defaults values.
 * Should be called at the beginning of the program.
 */
void startRoboid(void) {
    wiringPiSetup();
	
	Roboid.encoders.left = 4;
	Roboid.encoders.right = 5;
	
	Engine_initialise(&Roboid.left, 0,  3,  2, 6, 11, 10);
	Engine_initialise(&Roboid.right, 15, 16,  1, 12, 13, 14);
}

/**
 * Reset GPIOs values.
 * During test <CTRL-C> should be handled and call this method.
 */
void haltRoboid(void) {
	Engine_set_speed(&(Roboid.left), 0);
	Engine_set_speed(&(Roboid.right), 0);
}

/**
 * Set speed for the two left engines.
 */
void setLeftSpeed(signed char speed) {
	Engine_set_speed(&(Roboid.left), speed);
}

/**
 * Set speed for the two right engines.
 */
void setRightSpeed(signed char speed) {
	Engine_set_speed(&(Roboid.right), speed);
}

/**
 * Return the left (right) speed in m/s.
 */
double get_left_speed(void) {
	return Engine_get_instantaneous_speed(&(Roboid.left));
}
double get_right_speed(void) {
	return Engine_get_instantaneous_speed(&(Roboid.right));
}
