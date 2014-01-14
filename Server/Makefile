CC = gcc
CFLAGS = -Wall -g3 -DNDEBUG
LIBS = -lwiringPi -lm
SRCS = roboid-1.0.c server.c
OBJS = $(SRCS:.c=.o)
TARGET = server

all: $(TARGET)

server: $(OBJS)
	$(CC) -o $@ $^ $(LIBS)

%.o: %.c %.h
	$(CC) $(CFLAGS) $(LIBS) -c -o $@ $<

clean:
	rm -f $(OBJS) $(TARGET)
