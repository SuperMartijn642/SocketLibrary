package supermartijn642.socketlibrary.client;

import java.net.SocketAddress;

public class SocketConnectEvent {

	private ClientSocket socket;
	private SocketAddress address;
	
	public SocketConnectEvent(ClientSocket socket, SocketAddress address){
		this.socket = socket;
		this.address = address;
	}
	
	public ClientSocket getSocket(){
		return this.socket;
	}
	
	public SocketAddress getRemoteAddress(){
		return this.address;
	}
	
}
