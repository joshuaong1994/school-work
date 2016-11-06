#ifndef SERVER_H
#define SERVER_H

#include "CountryData.h"

time_t rawtime;
struct tm * captureTimeDate;

int socketConnection();
void * socketHandler(void *, int);
void processDataFromClient(char []);

#endif