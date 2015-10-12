#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <signal.h>
#include <arpa/inet.h>

#include <sys/time.h>
#include <fcntl.h>
#include <time.h>
#include <utime.h>

#include "/usr/include/mysql/mysql.h"

#define SQL_SERVER_IP "localhost"
#define USER "root" 
#define PASSWORD "root" 
#define DBNAME "ClockSkew"

#define TRUE 1
#define FALSE 0

/**********************************************************************
        Author: Tsung Han, Chen
        email: tom.jason2000@gmail.com
        File Abstract:
                open UDP port and listen UDP package.
**********************************************************************/


#pragma pack(1)
typedef struct{
        char request;
        long clock;//8
        long connectionID;//8
        char ID[16]; //16+1
        char password[16]; //16+1
        char email[50];
}REQUEST;
#pragma pack()

typedef union{
        REQUEST ori;
        char cha[99];
}RQ_BUFFER;


typedef union{
        struct sockaddr_in ori;
        char cha[16];
}SOCK_IN;

unsigned long myservertime(){
        struct timeval tv;
        unsigned long start_utime, sec, usec, end_time ;
        gettimeofday(&tv,NULL);
        sec = tv.tv_sec;
        usec = tv.tv_usec;
        end_time = sec * 1000000 + usec;
        return end_time;
}

long servertime;

MYSQL mysql;
MYSQL_RES *res;
MYSQL_ROW row;

int sql_insert(char* query){
        /*if insert data that information is same in DB will be re-insert in DB*/
        mysql_init(&mysql);
        if(!mysql_real_connect(&mysql,SQL_SERVER_IP,USER,PASSWORD,DBNAME,0,NULL,0)){
                //if connect DB failured
                printf( "Failed to connect to database: Error: %s\n",mysql_error(&mysql));
                printf( "Failed to connect to MySQL!\n");
                return FALSE;
        }
        else{
                printf("insert/update:\"%s\"\n",query);
                if(mysql_real_query(&mysql,query,(unsigned int) strlen(query))){
                        printf("Insert Error: %s\n",mysql_error(&mysql));
                        mysql_close(&mysql);
                        return FALSE;
                }else
                        printf("insert/update done!! \n");
        }

        mysql_close(&mysql);

        return TRUE;

}
int save_timestamp(long clock,long connection_ID){
        //only store
        char query[128];
        sprintf(query,"insert into ClockSkew.Timestamp value(NULL, %ld, %ld, %ld);",
        connection_ID, clock, servertime);
        //printf("insert:%s\n",query);
        if( sql_insert(query) == TRUE)
                return TRUE;

        return FALSE;
}
/*
void start_delay_detection(long connectionID,char fd[],struct sockaddr_in client_addr,int addrlen,pid_t processid){
	MYSQL mysql;
	MYSQL_RES *res;
	MYSQL_ROW row;
        char query[128], handle_list[500],sin_addr[16];
	int t_num=0;
	//SOCK_IN addr;

	bzero(query,sizeof(query));
        sprintf(query,"select COUNT( t_series_number ) from ClockSkew.Timestamp where CID =%ld;",connectionID);
	
	mysql_init(&mysql);
        if(!mysql_real_connect(&mysql,"140.118.70.148","csbwa","csbwa","csbwa",0,NULL,0)){
                //if connect DB failured
                printf( "Failed to connect to MySQL!\n");
                return ;
        }

        if(mysql_real_query(&mysql,query,(unsigned int) strlen(query))){
                printf("Search Error: %s\n",mysql_error(&mysql));
                mysql_close(&mysql);
                return ;
        }else{
                printf("Search done!! \n");

                res = mysql_store_result(&mysql);
		row = mysql_fetch_row(res);
		t_num =atoi(row[0]);
	        printf("CID #%ld have %d item(s)\n",connectionID,t_num);
		mysql_free_result(res);
		mysql_close(&mysql);

		if( t_num == 0){
			bzero(handle_list,sizeof(handle_list));
			bzero(sin_addr,sizeof(sin_addr));
			printf("%s : %d\n",inet_ntoa(client_addr.sin_addr), htons(client_addr.sin_port));
			//addr.ori = client_addr;
			sprintf(sin_addr,"%s",inet_ntoa(client_addr.sin_addr));
			sprintf(handle_list,"%s,%ld,%ld,%d,%d,%d,%s",fd,myservertime(),connectionID,addrlen,client_addr.sin_family,client_addr.sin_port,sin_addr);
			printf("%s\n",handle_list);

			
			if ((processid = fork()) < 0) {
                                printf("fork error\n");
                        }
                        else if (processid > 0) {
                                // parent 
                                //close(clientfd);
                        }
                        else {  // child
                                //execute other process
                                if (execl("./detection_timer","detection_timer",handle_list,(char*)0) < 0)
                                        printf("execl error\n");
                        }
			return;
		}
		
		printf("don't to do everything!!\n");
        }
	return ;
}
*/
int main()
{	
	int utp_sockfd;
	int val = 1;
	struct sockaddr_in dest;
	char buffer[] = "Hello World!";
	pid_t processid;
	char cfdnumber[5];

	/* create socket , same as client */
	if( (utp_sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ){
                printf("socket error!");
                exit(0);
        }

	signal(SIGCHLD, SIG_IGN);
	/* initialize structure dest */
	bzero(&dest, sizeof(dest));
	dest.sin_family = AF_INET;
	dest.sin_port = htons(7701);//for UDP 7701
	/* this line is different from client */
	dest.sin_addr.s_addr = INADDR_ANY;

	/* Can soon restart */
	if (setsockopt(utp_sockfd, SOL_SOCKET, SO_REUSEADDR, &val, sizeof(val)) < 0 ){
		fprintf(stderr,"setsockopt error");
		exit(0);
	}

	/* Assign a port number to UDP socket */
	if( (bind(utp_sockfd, (struct sockaddr*)&dest, sizeof(dest))) < 0 ){
                printf("bind error!");
                exit(0);
        }

	/* infinity loop -- accepting connection from client forever */
	while(1)
	{
		int clientfd;
		struct sockaddr_in client_addr;
		int addrlen = sizeof(client_addr);
		
		int nbytes = 0;
		int i;
		char buf[1025];
		char control_list[100],handle_list[100];
		char cfdnumber[5];
		RQ_BUFFER temp;

		char temp_list[256];
				
		/* Wait and Accept connection */
		bzero(&buf, sizeof(buf));
		bzero(&control_list, sizeof(control_list));				
		// UDP I/O
		nbytes = 0;
		if ((nbytes = recvfrom(utp_sockfd, &buf, 1024, 0, (struct sockaddr*)&client_addr,(socklen_t*)&addrlen)) < 0 ) {
			printf("Could not read datagram!!\n");
			continue;
		}
		printf("\nGET %d Bytes.\n",nbytes);

		for( i=0; i<99;i++){
			temp.cha[i] = buf[i];
			printf("%d,",buf[i]);
		}

		//sprintf(handle_list,"%c,%ld,%ld,%s",temp.ori.request,temp.ori.clock,temp.ori.connectionID,temp.ori.ID);

		//if (sendto(utp_sockfd, handle_list, sizeof(handle_list), 0,(struct sockaddr*) &client_addr, addrlen) < 0)
			//printf("not SEND out\n");


		if( nbytes == 99){
			//printf("\n%s\n",handle_list);		
			//sscanf(handle_list,"%c,%ld,%ld,%s",&temp.ori.request,&temp.ori.clock,&temp.ori.connectionID,temp.cha+17);
			//printf(handle_list,"%d,%ld,%ld,%s",temp.ori.request,temp.ori.clock,temp.ori.connectionID,temp.ori.ID);

			servertime = myservertime();

			if( save_timestamp(temp.ori.clock,temp.ori.connectionID) == TRUE ){
                        	printf("new timestamp data success!!\n");
                                        //not send success signal
                        }else
                                printf("new timestamp data failure\n");
 	                                //not send failure signal

		}else{
			printf("\nOut of resquest size(get %d bytes)\n",nbytes);
		}
		
	}

	/* close(server) , but never get here because of the loop */
	close(utp_sockfd);
	return 0;
}
