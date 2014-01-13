#ifndef ROBOID_1_0
#define ROBOID_1_0

typedef enum {
    ENGINE_FRONT_LEFT,
    ENGINE_FRONT_RIGHT,
    ENGINE_REAR_LEFT,
    ENGINE_REAR_RIGHT
} Engine;

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

#endif // ROBOID_1_0_