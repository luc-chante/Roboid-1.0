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
 * Make the robot moving forward
 */
extern void moveForward(unsigned int Th, unsigned int T);

/*
// Engines pins definition
#define FRONT_LEFT_PWM 0
#define FRONT_LEFT_IN1 3
#define FRONT_LEFT_IN2 2

#define FRONT_RIGHT_PWM 15
#define FRONT_RIGHT_IN1 16
#define FRONT_RIGHT_IN2 1

#define REAR_LEFT_PWM 6
#define REAR_LEFT_IN1 11
#define REAR_LEFT_IN2 10

#define REAR_RIGHT_PWM 12
#define REAR_RIGHT_IN1 13
#define REAR_RIGHT_IN2 14

// Encoders pins definition
#define ENCODER_RIGHT 4
#define ENCODER_LEFT 5

#define _MAX_PINS_ 14

static const int ROBOID_TO_WPI[] = {
    FRONT_LEFT_PWM,  FRONT_LEFT_IN1,  FRONT_LEFT_IN2,
    FRONT_RIGHT_PWM, FRONT_RIGHT_IN1, FRONT_RIGHT_IN2,
    REAR_LEFT_PWM,   REAR_LEFT_IN1,   REAR_LEFT_IN2,
    REAR_RIGHT_PWM,  REAR_RIGHT_IN1,  REAR_RIGHT_IN2,
    ENCODER_RIGHT, ENCODER_LEFT
};

static const int ROBOID_MODE[] = {
    OUTPUT, OUTPUT, OUTPUT, // Front Left Engine
    OUTPUT, OUTPUT, OUTPUT, // Front Right Engine
    OUTPUT, OUTPUT, OUTPUT, // Rear Left Engine
    OUTPUT, OUTPUT, OUTPUT, // Rear Right Engine
    INPUT, INPUT            // Encoder
};

static const int ROBOID_DEFAULT[] = {
    LOW, HIGH, LOW,
    LOW, HIGH, LOW,
    LOW, HIGH, LOW,
    LOW, HIGH, LOW
};

extern void roboid_init(void);

extern void roboid_reset(void);
*/

#endif // ROBOID_1_0_