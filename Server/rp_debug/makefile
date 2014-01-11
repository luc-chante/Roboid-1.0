################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include src/subdir.mk
-include subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: Roboid-1.0\ Server

# Tool invocations
Roboid-1.0\ Server: $(OBJS) $(USER_OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: Éditeur de liens C GCC'
	/home/luc/.local/share/ct-ng-tools/arm-roboid-linux-gnueabi/bin/arm-roboid-linux-gnueabi-gcc -L/home/luc/.local/share/ct-ng-tools/arm-roboid-linux-gnueabi/arm-roboid-linux-gnueabi/sysroot/usr/lib -L/home/luc/.local/share/ct-ng-tools/arm-roboid-linux-gnueabi/arm-roboid-linux-gnueabi/lib -o "Roboid-1.0 Server" $(OBJS) $(USER_OBJS) $(LIBS)
	@echo 'Finished building target: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(OBJS)$(C_DEPS)$(EXECUTABLES) "Roboid-1.0 Server"
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets
