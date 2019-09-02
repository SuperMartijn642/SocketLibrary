package supermartijn642.socketlibrary.client;

public class SocketMessageEvent {

	private ClientSocket socket;
	private byte[] message;
	
	public SocketMessageEvent(ClientSocket socket, byte[] message){
		this.socket = socket;
		this.message = message;
	}
	
	public ClientSocket getSocket(){
		return this.socket;
	}
	
	public byte[] getMessage(){
		return this.message;
	}
	
}
