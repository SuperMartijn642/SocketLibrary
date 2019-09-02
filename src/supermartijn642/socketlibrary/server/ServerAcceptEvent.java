package supermartijn642.socketlibrary.server;

import java.net.SocketAddress;

import supermartijn642.socketlibrary.client.ClientSocket;

public class ServerAcceptEvent {

	private ServerSocket server;
	private ClientSocket client;
	private SocketAddress address;
	
	public ServerAcceptEvent(ServerSocket server, ClientSocket client, SocketAddress address){
		this.server = server;
		this.client = client;
		this.address = address;
	}
	
	public ServerSocket getServerSocket(){
		return this.server;
	}
	
	public ClientSocket getClientSocket(){
		return this.client;
	}
	
	public SocketAddress getAddress(){
		return this.address;
	}
	
}
