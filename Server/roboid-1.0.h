#ifndef ROBOID_1_0
#define ROBOID_1_0

/**
 * Initialize GPIO mode and defaults values.
 * Should be called at the beginning of the program.
 */
extern void startRoboid(void);

/**
 * Reset GPIOs values.
 * During test <CTRL-C> should be handled and call this method.
 */
extern void haltRoboid(void);

/**
 * Set speed for the two left engines.
 */
extern void setLeftSpeed(signed char speed);

/**
 * Set speed for the two right engines.
 */
extern void setRightSpeed(signed char speed);

/**
 * Return the left (right) speed in m/s.
 */
extern double get_left_speed(void);
extern double get_right_speed(void);
#endif // ROBOID_1_0_
