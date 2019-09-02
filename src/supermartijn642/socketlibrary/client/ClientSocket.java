package supermartijn642.socketlibrary.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import supermartijn642.socketlibrary.StreamUtils;

public class ClientSocket {

	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private final ArrayList<ClientSocketListener> listeners = new ArrayList<>();
	private boolean isConnected = false;
	private boolean isChecking = false;
	private boolean isCheckingAsync = false;
	private boolean shouldStopChecking = false;
	
	public ClientSocket() {
		this.socket = new Socket();
	}
	
	public ClientSocket(Socket socket){
		this.socket = socket;
		if(socket.isConnected()){
			try {
				this.inputStream = socket.getInputStream();
				this.outputStream = socket.getOutputStream();
				this.isConnected = true;
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	public SocketAddress getLocalAddress(){
		return this.socket.getLocalSocketAddress();
	}
	
	public boolean isConnected(){
		return this.isConnected;
	}
	
	/**
	 * @return true if succesfully connected
	 */
	public boolean connect(SocketAddress address) {
		if(this.isConnected)
			return false;
		try {
			this.socket.connect(address, 0);
			this.inputStream = this.socket.getInputStream();
			this.outputStream = this.socket.getOutputStream();
			this.isConnected = true;
		} catch (Exception e) {e.printStackTrace(); return false;}
		SocketConnectEvent event = new SocketConnectEvent(this, this.getRemoteAddress());
		synchronized (this.listeners) {
			for(ClientSocketListener listener : this.listeners)
				listener.onConnect(event);
		}
		return true;
	}
	
	public SocketAddress getRemoteAddress(){
		return this.socket.getRemoteSocketAddress();
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
		if(!this.isConnected || this.isChecking || this.isCheckingAsync)
			return false;
		this.isChecking = true;
		while(!this.shouldStopChecking){
			if(check()){
				new Thread(this::disconnect).start();
				break;
			}
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
		if(!this.isConnected || this.isChecking || this.isCheckingAsync)
			return false;
		this.isCheckingAsync = true;
		new Thread(() -> {
			while(!shouldStopChecking){
				if(check()){
					new Thread(ClientSocket.this::disconnect).start();
					break;
				}
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

	private boolean check(){
		try {
			int i = this.inputStream.read();
			if(i == -1){
				return true;
			}
			else if(i == 1){
				int length = StreamUtils.readInt(this.inputStream);
				byte[] bytes = StreamUtils.readBytes(this.inputStream, length);
				SocketMessageEvent event = new SocketMessageEvent(this, bytes);
				synchronized (this.listeners) {
					for(ClientSocketListener listener : this.listeners)
						listener.onMessage(event);
				}
			}
		} catch (Exception e) {if(e instanceof SocketException) return true; e.printStackTrace();}
		return false;
	}
	
	/**
	 * @return true if the socket was connected
	 */
	public boolean disconnect(){
		if(!this.isConnected)
			return false;
		SocketAddress address = this.getRemoteAddress();
		try {
			if(this.isChecking || this.isCheckingAsync)
				this.shouldStopChecking = true;
			while(this.isCheckingAsync || this.isChecking)
				Thread.sleep(10);
			this.socket.close();
			this.inputStream = null;
			this.outputStream = null;
			this.isConnected = false;
		} catch (Exception e) {e.printStackTrace(); return false;}
		SocketDisconnectEvent event = new SocketDisconnectEvent(this, address);
		synchronized (this.listeners) {
			for(ClientSocketListener listener : this.listeners)
				listener.onDisconnect(event);
		}
		return true;
	}
	
	/**
	 * @return true if the listener was not yet registered
	 */
	public boolean addListener(ClientSocketListener listener){
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
	public boolean removeListener(ClientSocketListener listener){
		synchronized (this.listeners) {
			return this.listeners.remove(listener);
		}
	}
	
	public boolean writeMessage(byte[] bytes){
		if(!this.isConnected)
			return false;
		try {
			synchronized (this.outputStream) {
				this.outputStream.write(1);
				StreamUtils.writeInt(this.outputStream, bytes.length);
				this.outputStream.write(bytes);
			}
		} catch (Exception e) {
			if(e instanceof SocketException) {
				new Thread(this::disconnect).start();
				return false;
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
