# Chating Application Project

This Project is made by Mohamed Maher

Email : 

```
momaherg@gmail.com
```

## Getting Started 

to start the application you should run LOGIN.java class 

## Project Structure :

### LogIN :

* Button to Create a server listens to  specific portNumber.
* Button to connect 2 Servers together to form a network, with these 2 Server as a start.
* Ability to connect extra servers to this network by connecting these servers to atleast 1 server in the network.
* Choose a Username to join the network with

### MainServer :

which represents the MultiThreaded Server

* Listens for new connections.
* Incase the connection was from another server wants to connect, the server adds to the connected server and notify other servers.
* Incase the connection was from Client, the server creates a Thread specialy for that client to listen to its Socket.
* Notify all servers that this Client is connected to the network.
* Incase a message came to the server with Destination to a Client not connected to that server, The Server forwards the incoming message to some neighboring Servers.


### Client :

* Represent a Client that can connect to this server and its GUI is already implemented in it.
* Send Messages to the connected server.


