#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/time.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <pthread.h>

#include "roboid-1.0.h"

// Make sure this constants are the same as the Android application
#define CMD_SET_LEFT_SPEED   0x01
#define CMD_SET_RIGHT_SPEED  0x02
#define CMD_SET_ACCELERATION 0x04
#define CMD_CLOSE_CONNECTION 0x08

#define ACCELERATION_DEFAULT 0x01
#define ACCELERATION_SPORT   0x02
#define ACCELERATION_SMOOTH  0x04

static int server = -1;
static pid_t server_pid = -1;
//static pid_t speed_pid = -1;

// Remote control of the robot
static void start_server(void);

// Display current speed
static void* start_speed_listening(void*);

int main(int argc, char **argv) {
    pid_t pid;
    int status;

    switch (pid = fork()) {
        case -1:
            perror("fork");
            exit(EXIT_FAILURE);
        case 0:
            start_server();
            exit(EXIT_SUCCESS);
        default:
            server_pid = pid;
    }
    
    waitpid(server_pid, &status, 0);

    close(server);
	haltRoboid();
    
    return 0;
}

void start_server(void) {
	struct sockaddr_in config, client_addr;
	char buffer[2];
	int c, client;

	server = socket(AF_INET, SOCK_STREAM, 0);
	if (server == -1) {
		perror("socket");
		exit(EXIT_FAILURE);
	}
	
	config.sin_port = htons(8082);
	config.sin_addr.s_addr = 0;
	config.sin_addr.s_addr = INADDR_ANY;
	config.sin_family = AF_INET;
	
	if (bind(server, (struct sockaddr*) &config, sizeof(struct sockaddr_in)) == -1) {
		perror("bind");
		exit(EXIT_FAILURE);
	}
	
	if (listen(server, 1) == -1) {
		perror("listen");
		exit(EXIT_FAILURE);
	}
    
	startRoboid();
    
    while (1) {
    	c = sizeof(struct sockaddr_in);
        client = accept(server, (struct sockaddr*) &client_addr, (socklen_t*)&c);
#ifdef NDEBUG
        if (client != -1) {
        	unsigned long ip = client_addr.sin_addr.s_addr;
        	printf("Nouvelle connection: %lu.%lu.%lu.%lu\n", ip & 0xff, (ip >> 8) & 0xff, (ip >> 16) & 0xff, (ip >> 24) & 0xff);
        }
#endif
        while (client != -1 && read(client, &buffer, 2) != -1) {
            if (buffer[0] & CMD_SET_LEFT_SPEED) {
                setLeftSpeed(buffer[1]);
            }
            else if (buffer[0] & CMD_SET_RIGHT_SPEED) {
                setRightSpeed(buffer[1]);
            }
            else if (buffer[0] & CMD_SET_ACCELERATION) {

            }
            else if (buffer[0] & CMD_CLOSE_CONNECTION) {
#ifdef NDEBUG
            	unsigned long ip = client_addr.sin_addr.s_addr;
            	printf("Fin de transmission du client: %lu.%lu.%lu.%lu\n", ip & 0xff, (ip >> 8) & 0xff, (ip >> 16) & 0xff, (ip >> 24) & 0xff);
#endif
                close(client);
                client = -1;
            }
        }
    }
}

static void* start_speed_listening(void *arg) {
    while (1) {
        printf("\x0D%.2g | %.2g", get_left_speed(), get_right_speed());
        usleep(500000);
    }
    return NULL;
}
