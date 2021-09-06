package system;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerBackend implements Runnable {
	
	int Port, ClientLimit;
	ServerSocket serverSocket;
	//Function to insert the Port and ClientLimit
	void insertPort(int x, int lim) {
		this.Port = x;
		this.ClientLimit = lim;
	}
	
	
	/*public void StopAll() throws Exception{
		for(ClientManager X : Server.clientVector) {
			if(X.isClientLoggedIn == true) {
				
				try {
					X.dis.close();
					X.dos.close();
					X.socket.close();
					X.isClientLoggedIn = false;
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		serverSocket.close();
	}*/


	@Override
	public void run(){
		
		try {
			
			serverSocket = new ServerSocket( Port );
			Socket socket;
			
			while(true) {
				
				//limiting the number of active clients
				if(Server.activeClientCount >= ClientLimit){
					system.Server.displayServerLog("Maximum Connections Reached!");
					serverSocket.close();
					break;
				}
				
				//Objects of the new Client - socket, dis and dos
				socket= serverSocket.accept();
				
				system.Server.displayServerLog("New Client Joined: "+socket);
				
				//Input and Output Streams
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				
				//Waiting to receive client name
				String CName = dis.readUTF();
				System.out.println("Client Name: "+CName);
				system.Server.displayServerLog("New Client Name: " + CName);
				
				system.Server.displayServerLog("Creating a handler for client " + CName + ".");
				
				ClientManager newClient = new ClientManager(CName, socket, dis, dos);
				
				//Creating a thread to manage the new client
				Thread t = new Thread(newClient);
				
				//Adding the new client object to the vector.
				system.Server.clientVector.add(newClient);
				
				//starting the thread of the new client to manage it in background
				t.start();
				
				//Once a new client has been added the count is incremented
				system.Server.activeClientCount++;
			}
			
			
		}catch (SocketException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}