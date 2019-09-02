package supermartijn642.socketlibrary.server;

public class ServerBindEvent {
	
	private ServerSocket socket;
	private int port;
	
	public ServerBindEvent(ServerSocket socket, int port){
		this.socket = socket;
		this.port = port;
	}
	
	public ServerSocket getSocket(){
		return this.socket;
	}
	
	public int getPort(){
		return this.port;
	}

}
