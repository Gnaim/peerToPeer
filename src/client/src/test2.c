
#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>

void error_handling(char* s){
    perror(s);
    exit(1);
}

int main(){
    int retval;
    struct sockaddr_in servaddr, clientaddr;
    socklen_t size = sizeof(clientaddr);
    int servsock;
    char buff[512];
    
    servsock = socket(AF_INET,SOCK_DGRAM,0);
    if(servsock == -1)error_handling("socket");
    
    memset(&servaddr,0,sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(8080);
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    
    struct addrinfo* res = NULL;
    struct addrinfo hints;
    memset(&hints, 0, sizeof(struct addrinfo));
    //hints.ai_family = AF_INET;
    //hints.ai_socktype = SOCK_DGRAM;
    getaddrinfo("localhost", NULL, &hints, &res);
    for(; res != NULL; res = res->ai_next)
    {
        struct sockaddr_in* saddr = (struct sockaddr_in*)res->ai_addr;
        printf("hostname: %s\n", inet_ntoa(saddr->sin_addr));
    }
    freeaddrinfo(res);
    
    retval = bind(servsock , (const struct sockaddr*) &servaddr, sizeof(servaddr));
    if(retval == -1)error_handling("bind");
    
    while(1){
        retval = recvfrom(servsock,buff,512,0,(struct sockaddr*) &clientaddr, &size);
        if(retval== -1)error_handling("recvfrom");
        sendto(servsock,buff,retval,0,(struct sockaddr*) &clientaddr,size);
        buff[retval]=0;
        printf("new packet of size %i from %s:%d\n%s",retval,
               inet_ntoa(clientaddr.sin_addr),
               ntohs(clientaddr.sin_port) ,buff);
        if(retval ==-1)error_handling("write");
    }
    
    
    
    
    return 0;
}
