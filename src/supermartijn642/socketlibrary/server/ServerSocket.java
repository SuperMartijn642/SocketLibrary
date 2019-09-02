package supermartijn642.socketlibrary.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import supermartijn642.socketlibrary.client.ClientSocket;

public class ServerSocket {

	private java.net.ServerSocket socket;
	private final ArrayList<ServerSocketListener> listeners = new ArrayList<>();
	private boolean isBound = false;
	private boolean isChecking = false;
	private boolean isCheckingAsync = false;
	private boolean shouldStopChecking = false;
	
	public ServerSocket(){
		try {
			this.socket = new java.net.ServerSocket();
			this.socket.setSoTimeout(1000);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public SocketAddress getLocalAddress(){
		return this.socket.getLocalSocketAddress();
	}
	
	public boolean isBound(){
		return this.isBound;
	}

	/**
	 * @return true if bound succesfully
	 */
	public boolean bind(int port){
		if(this.isBound)
			return false;
		try {
			this.socket.bind(new InetSocketAddress("0.0.0.0", port));
		} catch (IOException e) {e.printStackTrace(); return false;}
		this.isBound = true;
		ServerBindEvent event = new ServerBindEvent(this, port);
		synchronized (this.listeners) {
			for(ServerSocketListener listener : this.listeners)
				listener.onBind(event);
		}
		return true;
	}

	/**
	 * @return true if bound succesfully
	 */
	public boolean bind(String hostname, int port){
		if(this.isBound)
			return false;
		try {
			this.socket.bind(new InetSocketAddress(hostname, port));
		} catch (IOException e) {e.printStackTrace(); return false;}
		this.isBound = true;
		ServerBindEvent event = new ServerBindEvent(this, port);
		synchronized (this.listeners) {
			for(ServerSocketListener listener : this.listeners)
				listener.onBind(event);
		}
		return true;
	}
	
	/**
	 * @return true if checking either in sync or async
	 */
	public boolean isChecking(){
		return this.isChecking || this.isCheckingAsync;
	}
	
	public boolean isCheckingAsync(){
		return this.isCheckingAsync;
	}
	
	/**
	 * The socket must be connected first and must not be already checking.
	 * @return false if socket is not connected or socket is already checking
	 */
	public boolean startChecking(){
		if(!this.isBound || this.isChecking || this.isCheckingAsync)
			return false;
		this.isChecking = true;
		while(!this.shouldStopChecking){
			try {
				ClientSocket socket = new ClientSocket(this.socket.accept());
				ServerAcceptEvent event = new ServerAcceptEvent(this, socket, socket.getRemoteAddress());
				synchronized (this.listeners) {
					for(ServerSocketListener listener : this.listeners)
						listener.onAccept(event);
				}
			} catch (SocketTimeoutException e) {}
			catch(Exception e) {e.printStackTrace();}
		}
		this.shouldStopChecking = false;
		this.isChecking = false;
		return true;
	}
	
	/**
	 * The socket must be connected first and must not be already checking.
	 * @return false if socket is not connected or socket is already checking
	 */
	public boolean startCheckingAsync(){
		if(!this.isBound || this.isChecking || this.isCheckingAsync)
			return false;
		this.isCheckingAsync = true;
		new Thread(() -> {
			while(!shouldStopChecking){
				try {
					ClientSocket socket = new ClientSocket(ServerSocket.this.socket.accept());
					ServerAcceptEvent event = new ServerAcceptEvent(ServerSocket.this, socket, socket.getRemoteAddress());
					synchronized (listeners) {
						for(ServerSocketListener listener : listeners)
							listener.onAccept(event);
					}
				} catch (SocketTimeoutException e) {}
				catch(Exception e) {e.printStackTrace();}
			}
			shouldStopChecking = false;
			isCheckingAsync = false;
		}).start();
		return true;
	}
	
	/**
	 * stops all checking, both in sync and async
	 * @return false if the socket was not being checked
	 */
	public boolean stopChecking(){
		if(!this.isChecking && !this.isCheckingAsync)
			return false;
		this.shouldStopChecking = true;
		return true;
	}
	
	public boolean close(){
		if(!this.isBound)
			return false;
		SocketAddress address = this.getLocalAddress();
		try {
			if(this.isChecking || this.isCheckingAsync)
				this.shouldStopChecking = true;
			while(this.isCheckingAsync || this.isChecking)
				Thread.sleep(10);
			this.socket.close();
			this.isBound = false;
		} catch (Exception e) {e.printStackTrace(); return false;}
		ServerCloseEvent event = new ServerCloseEvent(this, address);
		synchronized (this.listeners) {
			for(ServerSocketListener listener : this.listeners)
				listener.onClose(event);
		}
		return true;
	}
	
	/**
	 * @return true if the listener was not yet registered
	 */
	public boolean addListener(ServerSocketListener listener){
		synchronized (this.listeners) {
			if(this.listeners.contains(listener))
				return false;
			this.listeners.add(listener);
		}
		return true;
	}
	
	/**
	 * @return true if the listener was registered
	 */
	public boolean removeListener(ServerSocketListener listener){
		synchronized (this.listeners) {
			return this.listeners.remove(listener);
		}
	}
	
}
