package System;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

class ClientSender extends Thread  {
    Socket clientSocket;
    String Username;
    ClientReciever rec;
    int ttl = 2;

    public ClientSender(Socket sock, String name, ClientReciever rec){
        clientSocket = sock;
        Username = name;
        this.rec = rec;
    }
    
    public void run(){
        String sentence;
        while(true){
            
          
            try{
                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());       	

                sentence = inFromUser.readLine();
                if (sentence.equals("MembersList"))
                {
                    outToServer.writeBytes("getMembers" + '\n');
                }
                else{
                    String[] s = sentence.split("#");
                    String outstr = s[0] +"#"+ttl+"%"+ Username +"@"+s[1];
                    if(sentence == "bye"){
                        outToServer.writeBytes("close" + '\n');
                        return;
                    }

                    outToServer.writeBytes(outstr + '\n');
                }
            }
            catch(Exception e ){
                System.out.println("10 "+ e.getMessage());            }
        }
    }
}

class ClientReciever extends Thread {
    Socket clientSocket;
    Client client;
    ArrayList<String> sentIDS = new ArrayList<String>();
    String clientName;
    String Cname;

    public ClientReciever(Socket sock, Client client, String clientName){
        clientSocket = sock;
        this.client  = client;
        this.Cname = clientName;
        System.out.println(clientSocket);

    }
    
    public void run(){
        while(true){
            try {
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputString = "xxxxxxxxxx";

                if(inFromServer.ready()) {
                    inputString = inFromServer.readLine();
                    System.out.println("in inFromServer " + inputString);
                }
                //System.out.println("after inputstring" + inputString);


                if (inputString.startsWith("serverMembersList")) {
                    String[] D = inputString.split("@");
                    String[] names = D[1].split(",");
                    client.model.clear();
                    System.out.println("this is a member's list request and here is an example of the members" + names[1]);
                    for (String name : names) {
                        System.out.println(name);
                        client.model.addElement(name);
                    }
                } else {
                    if(! inputString.equals("xxxxxxxxxx")) {
                        String[] Data = inputString.split("#");
                        String dest = Data[0];
                        Data = Data[1].split("%");
                        String TTL = Data[0];
                        Data = Data[1].split("@");
                        String source = Data[0];
                        Data = Data[1].split("!");
                        String msgIN = Data[1];
                        String ID = Data[0];

                        if (!sentIDS.contains(ID)) {
                            client.msgModel.addElement(source + " : " + msgIN);
                            sentIDS.add(ID);
                        }
                    }

                }
            }
                catch(Exception e){
                    System.out.println("11 "+ e.getMessage());
                    return;
            }

        }
    }
    
    public static void main(String[] args) throws Exception{
        
        Client c1 = new Client(1111, "maher");
//        Client c2 = new Client(3333, "salma",3);
//        Client c3 = new Client(1234, "mariam",3);
    }
    
}
