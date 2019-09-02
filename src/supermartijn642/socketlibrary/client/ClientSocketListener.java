package supermartijn642.socketlibrary.client;

public interface ClientSocketListener {

	public void onConnect(SocketConnectEvent event);
	public void onDisconnect(SocketDisconnectEvent event);
	public void onMessage(SocketMessageEvent event);
	
}
