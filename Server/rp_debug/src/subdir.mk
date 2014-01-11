################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/roboid-1.0.c 

OBJS += \
./src/roboid-1.0.o 

C_DEPS += \
./src/roboid-1.0.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Compilateur C GCC'
	/home/luc/.local/share/ct-ng-tools/arm-roboid-linux-gnueabi/bin/arm-roboid-linux-gnueabi-gcc -I/home/luc/.local/share/ct-ng-tools/arm-roboid-linux-gnueabi/lib/gcc/arm-roboid-linux-gnueabi/4.8.2/include -I/home/luc/.local/share/ct-ng-tools/arm-roboid-linux-gnueabi/arm-roboid-linux-gnueabi/sysroot/usr/include -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


