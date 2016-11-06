#include "Server.h"

int main()
{
	// Read data from Country.txt
	readData();

	// Connect to socket when codes
	// are running
	socketConnection();
}

int socketConnection()
{
	// Capture Time to show as output
	// when and what time did the
	// server start
	time(&rawtime);
	captureTimeDate = localtime(&rawtime);

	// Set up all required host
	// variables that will be used
	// when setting up the ports for
	// listening and binding
	int host_port = 1101;
	int host_socket;		// host socket
	int * client_socket;	// client socket
	int * p_int;
	// int err;
	struct sockaddr_in my_addr;		// store address local
	struct sockaddr_in sock_addr;	// store address socket
	socklen_t addr_size = 0; // size of address

	int child_process_id;	// to store child id
	signal(SIGCHLD, SIG_IGN);	// terminate child and and ignore signal

	host_socket = socket(AF_INET, SOCK_STREAM, 0);
	
	// if host socket fails
	if (host_socket  == -1)
	{
		printf("Captured Activity on: (%s)\n", asctime(captureTimeDate));
		printf("Server: Error Initializing. Ref No: %d\n\n", errno);
		return 0;
	}

	p_int = (int *) malloc(sizeof(int));
	* p_int = 1;

	// if setting protocol options fails
	if ((setsockopt(host_socket, SOL_SOCKET, SO_KEEPALIVE, (char*)p_int, sizeof(int)) == -1 ) ||
		(setsockopt(host_socket, SOL_SOCKET, SO_REUSEADDR, (char*)p_int, sizeof(int)) == -1 ))
	{
		printf("Captured Activity on: (%s)\n", asctime(captureTimeDate));
		printf("Server: Error Setting Protocol Options. Ref No: %d\n\n", errno);
		return 0;
	}

	free(p_int);

	my_addr.sin_family = AF_INET;		 // Initialize to family port
	my_addr.sin_port = htons(host_port); // Choose port

	memset(&(my_addr.sin_zero), 0, 8);
	my_addr.sin_addr.s_addr = INADDR_ANY;

	// if binding to port fails
	if (bind(host_socket, (struct sockaddr *) &my_addr, sizeof(my_addr)) == -1)
	{
		printf("Captured Activity on: (%s)\n", asctime(captureTimeDate));
		printf("Server: Error Binding, Port is in Use. Ref No: %d\n\n", errno);
		return 0;
	}

	// if listening to port fails
	if (listen(host_socket, 10) == -1)
	{
		printf("Captured Activity on: (%s)\n", asctime(captureTimeDate));
		printf("Server: Error Listening to Port. Ref No: %d\n\n", errno);
		return 0;
	}

	printf("Captured Activity on: (%s)\n", asctime(captureTimeDate));
	printf("Server: Started with PID: %d\n", getpid());

	addr_size = sizeof(struct sockaddr_in);

	// loop
	while (1)
	{
		time(&rawtime);
		captureTimeDate = localtime(&rawtime);

		printf("Server: Waiting for a connection\n");
		client_socket = (int *) malloc(sizeof(int));

		// if able to server is able to accept connection
		if ((* client_socket = accept(host_socket, (struct sockaddr *) &sock_addr, &addr_size)) != -1)
		{
			printf("------------------------------------------------------------\n");
			printf("Connection Received on: %s\n", asctime(captureTimeDate));
			printf("Received From:          %s\n\n", inet_ntoa(sock_addr.sin_addr));

			switch (child_process_id = fork())
			{
				// Fail to fork
				case -1:
					printf("Server: Fail to Fork a Child Process ID. Ref No: %d\n", errno);
					printf("------------------------------------------------------------\n");
					exit(0);
					break;

				// Successful fork
				case 0:
					printf("Server: Forked Child Process ID '%d' on: %s\n", getpid(), asctime(captureTimeDate));
					printf("------------------------------------------------------------\n");
					socketHandler(client_socket, getpid());
					exit(0);
					break;

				// Error
				default:
					close(*client_socket);
					free(client_socket);
					break;
			}
		}

		else
		{
			printf("Server: Unable to Accept any Incoming Connections. Ref No: %d\n\n", errno);
		}
	}
	return 0;
}

void * socketHandler(void * get_client_socket, int get_child_id)
{
	// loop
	while (1)
	{
		int * client_socket = (int *) get_client_socket;

		char buffer[Max_Buffer_Size];
		int buffer_len = Max_Buffer_Size;
		int bytecount;

		printf("------------------------------------------------------------\n");

		// clear memory to 0
		memset(buffer, 0, buffer_len);

		// if the server doesn't receive any data from client
		if ((bytecount = recv(* client_socket, buffer, buffer_len, 0)) == -1)
		{
			printf("Captured Activity on: (%s)\n", asctime(captureTimeDate));
			printf("Server: Error Receiving Data from Client. Ref No: %d\n\n", errno);
			free(client_socket);
			return 0;
		}

		// if the client stops the program
		if (bytecount == 0)
		{
			printf("Captured Activity on: (%s)\n", asctime(captureTimeDate));
			printf("Server: Client ID '%d' has Terminated\n\n", get_child_id);
			printf("Server: Bytes Sent out: %d\n", bytecount);
			return 0;
		}

		// if the last message given by client
		// on client side is "end"
		if (strcmp(buffer, "end") == 0)
		{
			return 0;
		}

		if(strcmp(buffer, "export") != 0)
		{
			processDataFromClient(buffer);
			// if server is unable to send out data to client
			if ((bytecount = send(* client_socket, buffer, strlen(buffer), 0)) == -1)
			{
				printf("Captured Activity on: (%s)\n", asctime(captureTimeDate));
				printf("Server: Unable to send out Data to Client. Ref No: %d\n\n", errno);
				free(client_socket);
				return 0;
			}
		}
	}
	return 0;
}

void processDataFromClient(char get_client_data[])
{
	char tempStorage[Max_Buffer_Size];

	convertDataString(tempStorage, get_client_data);

	strcpy(get_client_data, tempStorage);

	get_client_data[strlen(get_client_data)] = '\0';
}



