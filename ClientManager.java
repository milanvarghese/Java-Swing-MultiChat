package system;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

import system.ClientManager;

public class ClientManager implements Runnable {
	
	Scanner scn = new Scanner(System.in);
	Socket socket;
	private final AtomicBoolean running = new AtomicBoolean(false);
	final DataInputStream dis;
	final DataOutputStream dos;
	boolean isClientLoggedIn;
	private String clientName;
	
	@SuppressWarnings("null")
	ClientManager(String name, Socket s, DataInputStream dis, DataOutputStream dos) {
		
		this.clientName = name;
		this.socket = s;
		this.dis = dis;
		this.dos = dos;
		this.isClientLoggedIn=true;
	}
	
	
	@Override
	public void run() {
		
		String receivedData;
		running.set(true);
		
		while (running.get()) {
			
			try {
				
				//Receiving the received data from client
				receivedData=dis.readUTF();
				system.Server.displayServerLog("Received Message From Client " + this.clientName);
				System.out.println(receivedData);
				
				if ( receivedData.indexOf("#") != -1 ) {
					
					//Checking for request to close connection
					if(receivedData.equals("#CloseConn")) {
						this.isClientLoggedIn=false;
						this.socket.close();
						this.stop();
						break;
					}
					
					//Splitting the recipient name and message.
					StringTokenizer st = new StringTokenizer(receivedData,"#");
					String recipient = st.nextToken();
					String messageToSend = st.nextToken();
					
					System.out.println("\nRecipient: " + recipient);
					System.out.println("Message: " + messageToSend);
					
					if (recipient.equals("AddClient")) {
						
						for(ClientManager CM : system.Server.clientVector) {

							if(CM.clientName != this.clientName && CM.isClientLoggedIn==true) {
								
								CM.dos.writeUTF("AddUser#" + messageToSend);
								this.dos.writeUTF("AddUser#" + CM.clientName);
								
							}
						}
						continue;
						
					}
					
					if (recipient.equals("RemoveClient")) {
						
						for(ClientManager CM : system.Server.clientVector) {

							if(CM.clientName != this.clientName && CM.isClientLoggedIn==true) {
								
								CM.dos.writeUTF("RemoveUser#" + messageToSend);
								CM.dis.close();
								CM.dos.close();
								CM.socket.close();
								dis.close();
								dos.close();
								
							}
						}
						continue;
					}
					
					if(receivedData.contains("Global")) {
						for(ClientManager CM : system.Server.clientVector) {
							System.out.print("Message Is send globally loop");
							if(CM.isClientLoggedIn==true) {
								CM.dos.writeUTF("Global#" + this.clientName + ": " + messageToSend);
							}
						}
						system.Server.displayServerLog("Message from Client " + this.clientName + " forwarded to Global Chat.");
						continue;
					}
				
					//Searching for the client and adding them to active list
					for(ClientManager CM : system.Server.clientVector) {

						if(CM.clientName.equals(recipient) &&  CM.isClientLoggedIn==true) {
						
							CM.dos.writeUTF(this.clientName + ": " + messageToSend);
							system.Server.displayServerLog("Message from Client " + this.clientName + " forwarded to Client "+recipient);
							break;
						}
					}
				}
				
				
			} catch (SocketException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		//The DIS and DOS will be closed once we exit the while loop
		try {
			this.dis.close();
			this.dos.close();
		} catch(Exception e1) {
			e1.printStackTrace();
		}
		
	}
	
	public void stop() {
		running.set(false);
    }
	
}
