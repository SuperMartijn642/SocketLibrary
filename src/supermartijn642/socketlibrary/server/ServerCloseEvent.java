package supermartijn642.socketlibrary.server;

import java.net.SocketAddress;

public class ServerCloseEvent {

	private ServerSocket socket;
	private SocketAddress address;
	
	public ServerCloseEvent(ServerSocket socket, SocketAddress address){
		this.socket = socket;
		this.address = address;
	}
	
	public ServerSocket getSocket(){
		return this.socket;
	}
	
	/**
	 * @return the local address the socket was bound to
	 */
	public SocketAddress getAddress(){
		return this.address;
	}
	
}
