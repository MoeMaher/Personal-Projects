package System;

import groovy.ui.SystemOutputInterceptor;

import java.io.*;
import java.net.*;
import java.util.ArrayList; 

/*
TODO: make a method to retrun all online clients
*/


//class Sender extends Thread{
//	MainServer server;
//	int count;
//	public Sender(MainServer s){
//		server = s;
//                count = 0 ;
//	}
//        public void inc(){
//            count++;
//        }
//	public void run() {
//                System.out.println("Sender started");
//		
//	}
//	
//}
//	

class TCPConnection extends Thread {
	String clientSentence; 
	Socket connectionSocket;
	int Port;
	ConnectionMaker server;
	
	public  TCPConnection(Socket Socket,ConnectionMaker s, int Port){
		connectionSocket = Socket;
		server = s;
		this.Port = Port;
		
	}
	
	public void run() {
		try {
			
			threadsJob();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	private void threadsJob() throws Exception  {


		while(true)
		{
			
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
                        
			clientSentence = inFromClient.readLine(); 
			if(clientSentence != null)
			{
				//capitalizedSentence = clientSentence.toUpperCase() + '\n'; 
				server.addMsg(clientSentence, connectionSocket);
			}

		}
	}
}


public class MainServer {

    ConnectionMaker maker;
    ArrayList<String> msg = new ArrayList<String>();
    ArrayList<Socket> msgSocket = new ArrayList<Socket>();
    ArrayList<Socket> sockets = new ArrayList<Socket>();
    ArrayList<String> Clients = new ArrayList<String>();
    ArrayList<String> VirtualClients = new ArrayList<String>();
    ArrayList<Socket> Servers = new ArrayList<Socket>();
    int port;
    String msgID ;
    
    public MainServer(int portNumber) throws Exception
    {
        port = portNumber;
        maker = new ConnectionMaker(this, port);
        maker.start();
        msgID = port + "";
        
    }

    public String toString(){
        return "Server with Port : "+port;
    }

    public int getPort(){
        return port;
    }
    
    public void connectToServer(int Port) throws Exception{
        
        Socket clientSocket = new Socket("MahersPC", Port); 
        
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());       	

        outToServer.writeBytes("server" + '\n');
        
        addServer(clientSocket);
        
        TCPConnection s = new TCPConnection(clientSocket, maker , port);
        s.start();
        
    }
    
    public void sendMembersList(Socket s){
        String msg0 ="serverMembersList@";
        for(int i = 0 ; i<Clients.size(); i++){
            msg0 = msg0 + Clients.get(i) + ",";
        }
        
        for(int i =0; i<VirtualClients.size() ; i++){
            msg0 = msg0 + VirtualClients.get(i) + ",";
        }
        
        StringBuilder f = new StringBuilder(msg0);
        f.deleteCharAt(f.length()-1);
        msg0 = f.toString();
        System.out.println(msg0);
        msg.add(msg0);
        msgSocket.add(s);
        send(100);
        
    }
    
    public void addClient(String name, Socket s){
        Clients.add(name);
        sockets.add(s);
    }
    public void rmClient(String name, Socket s){
        Clients.remove(name);
        sockets.remove(s);
    }
    public void addmsg(String x , Socket s){
        
        if(x.equals("server"))                              // server request connection
        {
            addServer(s);
            System.out.println("Server Added");
            return;
        }
        else
        {
            if(x.startsWith("ok"))                          // add client to server
            {   
                String [] data = x.split("#");
                addClient(data[1],s);
                System.out.println(data[1] +" added Succesfully.");
                broadcastMsg("addThisUsername#"+data[1]);
                return;
            }
            if(x.startsWith("close")){                      //close connection with disconnecting client
                String [] data = x.split("#");
                rmClient(data[1],s);
                System.out.println(data[1] +" Disconnected");
                broadcastMsg("removeThisUsername#"+data[1]);
                return;
            }
            if(x.startsWith("addThisUsername"))             //  to add new clients joined to other serves and
            {                                               //  add them to an array called VirtualClients to 
                String [] data = x.split("#");              //  keep track of the names currently joined

                if((!VirtualClients.contains(data[1])) && (!Clients.contains(data[1])))
                {
                    VirtualClients.add(data[1]);
                    broadcastMsg(x);
                }
                return;
            }
            if(x.startsWith("removeThisUsername"))          //  to remove disconnected clients
            {                                               
                String [] data = x.split("#");              
                if(VirtualClients.contains(data[1]))
                {
                    VirtualClients.remove(data[1]);
                    broadcastMsg(x);
                }
                return;
            }
            if(x.startsWith("getMembers"))                  // client requested current connected client to the network
            {
                sendMembersList(s);
                return;
            }
            if(x.startsWith("connectionNum")){
               return;
            }
                                                            // its a message from client.
            System.out.println(x);
            String [] Data = x.split("#");
            System.out.println(Data.length);
            System.out.println(Data[0]);
            System.out.println(Data[1]);
            String dest = Data[0];
            Data = Data[1].split("%");
            String TTL = Data[0];
            int ttl = Integer.parseInt(TTL);
            Data = Data[1].split("@");
            String source = Data[0];
            String sentence;
            String ID;
            Data = Data[1].split("!");
            sentence = Data[1];
            ID = Data[0];

            
            if(ttl >= 1){
                if(Clients.contains(dest))
                {
                    msg.add(dest +"#" + (ttl-1) + "%" + source + "@" + ID + "!" + sentence);
                    msgSocket.add((sockets.get(Clients.indexOf(dest))));
                    send(ttl-1);

                }
                else
                {
                    broadcastMsg(dest +"#" + (ttl-1) + "%" + source + "@" + ID + "!" + sentence);
                }
            }

        }
    }
    
    public void addServer(Socket s){
        Servers.add(s);
    }
    
    public void broadcastMsg(String msg){
        
        for(int i = 0; i<Servers.size() ; i++)
        {
           Socket out = Servers.get(i);
           DataOutputStream  outToClient = null;
            try {
                outToClient = new DataOutputStream(out.getOutputStream());
                outToClient.writeBytes(msg+"\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public void send(int ttl) {
        
        if(!msg.isEmpty())
        {
            int size = msgSocket.size()-1;
            Socket s = msgSocket.get(size);
            String ms = msg.get(size);
            msgSocket.remove(size);
            msg.remove(size);
            DataOutputStream  outToClient = null;
            try {
                outToClient = new DataOutputStream(s.getOutputStream());
                outToClient.writeBytes(ms+"\n");
                System.out.println("message " + ms + " is Being Sent to " + s);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("6 "+ e.getMessage());
            }
        }
       

    }
    
    public static void main(String argv[]) throws Exception 
    { 
                    MainServer s1 = new MainServer(1111);
                  //  MainServer s2 = new MainServer(1234);
                    //MainServer s3 = new MainServer(2222);
                    //MainServer s4 = new MainServer(3333);
                    
                    //s1.connectToServer(s2.getPort());
                    //s2.connectToServer(s3.getPort());
                    //s3.connectToServer(s4.getPort());
                   
                                      
    } 	

}

   




       
class ConnectionMaker extends Thread{
    
	String clientSentence; 
	String capitalizedSentence; 
	MainServer main;
	int numOfConnections=0;
	ArrayList<String> msg = new ArrayList<String>();
	ArrayList<Socket> msgSocket = new ArrayList<Socket>();
	ArrayList<Socket> sockets = new ArrayList<Socket>();
	ArrayList<String> Clients = new ArrayList<String>();
	int portnum;
	
	public ConnectionMaker(MainServer m, int port) {
        main = m;
        portnum = port;
    }
        
        public void run(){
            try{
                
                ServerSocket welcomeSocket = new ServerSocket(portnum);
                System.out.println("ConnectionMaker Started Succefully");
                    while(true) { 

                            Socket connectionSocket = welcomeSocket.accept(); 
                            numOfConnections++;

                            TCPConnection x = new TCPConnection(connectionSocket,this ,portnum);
                            x.start();

		}
            }
            catch(Exception e){
                System.out.println("7 "+ e.getMessage());
            }
        }
	public void addMsg(String x, Socket connectionSocket){
        main.addmsg(x, connectionSocket);

        }
                //msg.add(in[1]);
                //System.out.println("a msg created and added to queue msg is : "+in[1] + " to " + sockets.get(Clients.indexOf(in[0])));
                //System.out.println(msg.size());
                //k.inc();
                //server.msgSocket.remove(size);
                //server.msg.remove(size);
                //count--;
               
//                DataOutputStream  outToClient = null;
//                System.out.println("Before DataOutputStream");
//                try {
//                        outToClient = new DataOutputStream(sockets.get(Clients.indexOf(in[0])).getOutputStream());
//                        System.out.println("writeBytesss is sendinngg"); 
//                        outToClient.writeBytes(in[1]+"\n");
//                } catch (IOException e) {
//                        //
//                        e.printStackTrace();
//				}
                //k.inc();
       

}
