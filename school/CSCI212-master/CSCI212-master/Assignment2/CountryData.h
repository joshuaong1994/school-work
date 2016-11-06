
#ifndef COUNTRY_DATA_H
#define COUNTRY_DATA_H

// ====================================================================

#include <fcntl.h>
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <resolv.h>
#include <unistd.h>
#include <signal.h>
//#include <pthread.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <time.h>
// ====================================================================

#define TLD_LEN				2
#define COUNTRY_LEN 		100
#define FIPS104_LEN 		2
#define ISO2_LEN			2
#define ISO3_LEN			3
#define CAPITAL_LEN			100
#define REGION_LEN			100
#define CURRENCY_LEN		50
#define CURRENCY_CODE_LEN	3

#define No_Of_Rec_Fields		11
#define Max_Record_Size			250
#define Line_Char_Buffer_Size	400
#define Max_Buffer_Size			1024

#define LINE_DATA_DELIMITER 	","
#define INPUT_FILE_NAME 		"Countries.txt"

// ====================================================================

//const char*	LINE_DATA_DELIMITER		= ",";
//const char*	INPUT_FILE_NAME 		= "Countries.txt";


typedef struct CountryRecord
{
	char TLD			[TLD_LEN+1];			// Top Level Domain code
	char Country		[COUNTRY_LEN+1];	
	char FIPS104		[FIPS104_LEN+1];		// Ctry code according to FIPS104 standard
	char ISO2			[ISO2_LEN+1];			// Ctry code according to ISO2    standard
	char ISO3			[ISO3_LEN+1];			// Ctry code according to ISO3    standard
	double ISONo;

	char Capital		[CAPITAL_LEN+1];	
	char Region			[REGION_LEN+1];			// E.g. Asia, Europe, etc.
	char Currency		[CURRENCY_LEN+1];		// Full name of currency
	char CurrencyCode	[CURRENCY_CODE_LEN+1];	// Currency abbreviation
	double Population;

}	CountryRecordType;

int NoOfRecordsRead;
CountryRecordType globalCountryDataArray [Max_Record_Size];

// ====================================================================

void readData ();
char* get_line (char *s, size_t n, FILE *f);
CountryRecordType createCountryRecord (char* aLine);
void displayRecordContent (CountryRecordType ctryRec);
void showAllRecords ();

int findCountryRecord (const char* countryName);
char* getCapital (const char* countryName);
char* getCurrencyCode (const char* countryName);

CountryRecordType getCountryData(const char *);
void convertDataString(char [], const char *);

// ====================================================================

#endif // COUNTRY_DATA_H


