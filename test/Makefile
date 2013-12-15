CC = gcc
CFLAGS = -Wall -g3
LIBS = -lwiringPi
SRCS = roboid-1.0.c pins-test.c
OBJS = $(SRCS:.c=.o)
TARGET = test

all: $(TARGET)

test: $(OBJS)
	$(CC) -o $@ $^ $(LIBS)

%.o: %.c %.h
	$(CC) $(CFLAGS) $(LIBS) -c -o $@ $<

clean:
	rm -f $(OBJS) $(TARGET)
