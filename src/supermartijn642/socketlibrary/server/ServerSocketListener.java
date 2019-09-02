package supermartijn642.socketlibrary.server;

public interface ServerSocketListener {

	void onBind(ServerBindEvent event);
	void onAccept(ServerAcceptEvent event);
	void onClose(ServerCloseEvent event);
	
}
