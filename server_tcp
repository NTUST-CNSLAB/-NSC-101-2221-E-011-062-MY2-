#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <signal.h>

/**********************************************************************
        Author: Tsung Han, Chen
        email: tom.jason2000@gmail.com
        File Abstract:
                open TCP port and listen TCP package.
**********************************************************************/

int main()
{	int count =0;
	int sockfd;
	int val = 1, nTime = 5000;
	struct sockaddr_in dest;
	char buffer[] = "Hello World!";
	pid_t processid;
	char cfdnumber[5];

	/* create socket , same as client */
	if( (sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0 ){
		printf("socket error!");
		exit(0);
	}

	signal(SIGCHLD, SIG_IGN);
	/* initialize structure dest */
	bzero(&dest, sizeof(dest));
	dest.sin_family = AF_INET;
	dest.sin_port = htons(7700);//7700 (test7720)
	/* this line is different from client */
	dest.sin_addr.s_addr = INADDR_ANY;

	/* Can soon restart */
	if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &val, sizeof(val)) < 0 ){
		fprintf(stderr,"setsockopt error");
		exit(0);
	}

	//setsockopt(SOCKET, SOL_SOCKET, SO_SNDTIMEO, (char*)&nTime, sizeof(nTime));

	/* Assign a port number to TCP socket */
	if( (bind(sockfd, (struct sockaddr*)&dest, sizeof(dest))) < 0 ){
		printf("bind error!\n");
                exit(0);
	}

	/* make it listen to socket with max 20 connections */
	if( (listen(sockfd, 20)) < 0 ){
		printf("listen error!");
                exit(0);
	}

	/* infinity loop -- accepting connection from client forever */
	while(1)
	{
		int clientfd;
		struct sockaddr_in client_addr;
		int addrlen = sizeof(client_addr);

		/* Wait and Accept connection */
				
		if( (clientfd = accept(sockfd, (struct sockaddr*)&client_addr, &addrlen)) < 0 ){
			printf("receive error!");
	                exit(0);
		}	

		/* To creation pipe */
		if ((processid = fork()) < 0) {
			printf("fork error\n");
		}
		else if (processid > 0) {
			/* parent */
			close(clientfd);
			
			//test only
			//count++;
			//if(count >5){
			//close(sockfd);
			//exit(0);
			//}
		}
		else {	/* child */
			//execute other process
			//close(sockfd);
			sprintf(cfdnumber,"%d",clientfd);
			printf("enter\n");
			if (execl("./handlefunction","handlefunction",cfdnumber,"TCP",(char*)0) < 0)
				printf("execl error\n");
		}
		/////////////////////////////////////////////////////////////////////////////////////
		/* Send message */
		//send(clientfd, buffer, sizeof(buffer), 0);
		/* close(client) */
		//close(clientfd);
		
	}

	/* close(server) , but never get here because of the loop */
	close(sockfd);
	return 0;
}
