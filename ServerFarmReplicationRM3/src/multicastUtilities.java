import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.*;

public class multicastUtilities {
	private MulticastSocket socket;
	private int port;
	private int ttl = 1;
	private InetAddress group; 
	
	public multicastUtilities(InetAddress group, int port) throws Exception{
		this.group = group;
		this.port = port;
		this.socket = new MulticastSocket(port);
		socket.setTimeToLive(ttl);
	}
	
	public void joinGroup() throws Exception{
		socket.joinGroup(group);
	}
	
	public void leaveGroup() throws Exception{
		socket.leaveGroup(group);
		socket.close();
	}
	
	public String readMessage() throws Exception {
		BufferedReader stdin;
		String message;
		stdin = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter Text: ");
		message = stdin.readLine();
		return message;
	}
	
	public void sendString(String message) throws Exception{
		DatagramPacket sendMessage = new DatagramPacket(message.getBytes(), message.length(), group, port);
		socket.send(sendMessage);
	}
	
	public String receiveString() throws Exception {
		byte[] receive = new byte[1024];
		DatagramPacket receivePacket=  new DatagramPacket(receive, receive.length);
		socket.setSoTimeout(1200);
		try {
			socket.receive(receivePacket);
			String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
			return message;
		}
		catch(InterruptedIOException timeout) {
			return "timeout";
		}
	}
	
	public String receiveString1() throws Exception {
		byte[] receive = new byte[1024];
		DatagramPacket receivePacket=  new DatagramPacket(receive, receive.length);
		socket.receive(receivePacket);
		String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
		return message;
	}
	
	public void displayMessage(String message) {
		System.out.println("Multicast Text: " + message);
	}
	

}
