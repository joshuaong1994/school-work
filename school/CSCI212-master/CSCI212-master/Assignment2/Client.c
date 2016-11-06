#include "Client.h"

void welcomeInstructions()
{
	printf("\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
	printf("        Welcome to the Country Info Directory Service!         \n");
	printf("       ----------------------------------------------          \n");
	printf("Usage :\n\n");
	printf("1) At the '>' prompt, type in the name of the country\n");
	printf("   you wish to search\n\n");
	printf("2) To end program, type in 'end'\n");
	printf("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");
}

int connectToServer()
{
	host_socket = socket(AF_INET, SOCK_STREAM, 0);

	// Client can't initialize
	if (host_socket == -1)
	{
		printf("Client: Error Initializing. Ref No: %d\n", errno);
		return 1;
	}

	p_int = (int *) malloc(sizeof(int));
	* p_int = 1;

	// if setting protocol options fails
	if ((setsockopt(host_socket, SOL_SOCKET, SO_KEEPALIVE, (char *)p_int, sizeof(int)) == -1) ||
		(setsockopt(host_socket, SOL_SOCKET, SO_REUSEADDR, (char *)p_int, sizeof(int)) == -1))
	{
		printf("Client: Error Setting Protocol Options. Ref No: %d\n\n", errno);
		free(p_int);
		return 1;
	}

	free(p_int);

	my_addr.sin_family = AF_INET;
	my_addr.sin_port = htons(host_port);

	memset(&(my_addr.sin_zero), 0, 8);
	my_addr.sin_addr.s_addr = inet_addr(host_name);

	// if unable to connect to socket
	if (connect(host_socket, (struct sockaddr *) &my_addr, sizeof(my_addr)) == -1)
	{
		printf("Client: Error Connecting to Socket. Ref No: %d\n\n", errno);
		return 1;
	}

	clientInteraction();
	close(host_socket);
	return 0;
}

int clientInteraction()
{
	while (1)
	{
		// searchCountry();
		memset(userinput, '\0', Max_Buffer_Size);
		printf("Enter Country > ");
		fgets(userinput, Max_Buffer_Size, stdin);
		userinput[strlen(userinput) - 1] = '\0';

		if (strcmp(userinput, "end") == 0)
		{
			return 0;
		}

		else
		{
			sendPacket();
			recievePacket();

			if (storePacket(recieved))
			{
				countryDetails(record);
				welcomeInstructions();
			}

			else
			{
				printf("\n-------------------------------------------------------------\n");
				printf("Error - Country Not Found! ('%s')\n\n", userinput);
			}
			
		}
	}
	return 0;
}

int storePacket(char recieved[])
{
	char tempStorage[Max_Buffer_Size];
	strcpy(tempStorage, recieved);

	if (strcmp(tempStorage, "Invalid") != 0)
	{
		char * capture = strtok (tempStorage, LINE_DATA_DELIMITER);

		strcpy (record.TLD, capture);
		capture = strtok (NULL, LINE_DATA_DELIMITER);

		strcpy (record.Country, capture);
		capture = strtok (NULL, LINE_DATA_DELIMITER);

		strcpy (record.FIPS104, capture);
		capture = strtok (NULL, LINE_DATA_DELIMITER);

		strcpy (record.ISO2, capture);
		capture = strtok (NULL, LINE_DATA_DELIMITER);

		strcpy (record.ISO3, capture);
		capture = strtok (NULL, LINE_DATA_DELIMITER);

		record.ISONo = atof (capture);
		capture = strtok (NULL, LINE_DATA_DELIMITER);

		strcpy (record.Capital, capture);
		capture = strtok (NULL, LINE_DATA_DELIMITER);

		strcpy (record.Region, capture);
		capture = strtok (NULL, LINE_DATA_DELIMITER);

		strcpy (record.Currency, capture);
		capture = strtok (NULL, LINE_DATA_DELIMITER);

		strcpy (record.CurrencyCode, capture);
		capture = strtok (NULL, LINE_DATA_DELIMITER);

		record.Population = atof (capture);

		return 1;
	}

	return 0;
}

// Client sends data packet to Server
int sendPacket()
{
	// if send packet data fails
	if ((bytecount = send(host_socket, userinput, strlen(userinput), 0)) == -1)
	{
		printf("Client : Error Sending Data. Ref No: %d\n", errno);
		return 1;
	}
	return 0;
}

// Client receives data packet from Server
int recievePacket()
{
	memset(recieved, '\0', Max_Buffer_Size);

	// if unable to recieve data from server
	if ((bytecount = recv(host_socket, recieved, Max_Buffer_Size, 0)) == -1)
	{
		printf("Client : Error Recieving Data. Ref No: %d\n", errno);
		return 1;
	}

	return 0;
}

void countryDetails(CountryRecordType country_data)
{
	printf("\n-------------------------------------------------------------\n");
	printf("                        Country Details                        \n");
	printf("                   -------------------------                   \n\n");

	printf("TLD\t\t: %s\n", record.TLD);
	printf("Country\t\t: %s\n", record.Country);
	printf("FIPS 104\t: %s\n", record.FIPS104);
	printf("ISO 2\t\t: %s\n", record.ISO2);
	printf("ISO 3\t\t: %s\n", record.ISO3);
	printf("ISO No\t\t: %0.2f\n", record.ISONo);
	printf("Capital\t\t: %s\n", record.Capital);
	printf("Region\t\t: %s\n", record.Region);
	printf("Currency\t: %s\n", record.Currency);
	printf("Currency Code\t: %s\n", record.CurrencyCode);
	printf("Population\t: %0.2f\n", record.Population);
}

int main()
{
	// display the welcome message
	// and instructions for client
	welcomeInstructions();

	// check connection to server
	// from client
	if (connectToServer() == 0)
	{
		printf("\t-- Quitting Conuntry Info Directory Service --\n\n\n\n\n\n");
	}
}
