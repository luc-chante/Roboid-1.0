#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>
#include <time.h>
#include <wiringPi.h>

#include "roboid-1.0.h"

void terminate(int sig_num) {
    haltRoboid();
    exit(EXIT_SUCCESS);
}

int main(int argc, char **argv) {
    
    if (getuid() != 0) {
        printf("This program has to launch in super user mode\n");
        exit(EXIT_FAILURE);
    }
    
    startRoboid();
    
    signal(SIGINT, terminate);
    moveForward(75, 50);
    
    return 0;
}