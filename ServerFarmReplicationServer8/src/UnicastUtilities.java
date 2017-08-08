import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.*;

public class UnicastUtilities {
	private DatagramSocket socket;
	private int port;
	private InetAddress IP;
	private int packetPort;
	private InetAddress packetIP;
	
	public UnicastUtilities() throws Exception{
		socket = new DatagramSocket();
	}
	
	public UnicastUtilities(int port) throws Exception{
		this.port = port;
		socket = new DatagramSocket(port);
	}
	
	public UnicastUtilities(int port, InetAddress IP) throws Exception{
		this.port = port;
		this.IP = IP;
		socket = new DatagramSocket();
	}
	
	public void closeSocket() {
		socket.close();
	}
	
	public String readString() throws Exception {
		BufferedReader stdin;
		String message;
		stdin = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Message: ");
		message = stdin.readLine();
		return message;
	}
	
	public void sendObject(byte[] object) throws Exception{
		DatagramPacket sendMessage = new DatagramPacket(object, object.length, IP, port);
		socket.send(sendMessage);
	}
	
	public void sendString(String message) throws Exception{
		DatagramPacket sendMessage = new DatagramPacket(message.getBytes(), message.length(), IP, port);
		socket.send(sendMessage);
	}
	
	public String receiveString() throws Exception {
		byte[] receive = new byte[1024];
		DatagramPacket receivePacket=  new DatagramPacket(receive, receive.length);
		socket.receive(receivePacket);
		packetIP = receivePacket.getAddress();
		packetPort = receivePacket.getPort();
		String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
		return message;
	}
	
	public String receiveStringListener() throws Exception {
		byte[] receive = new byte[1024];
		DatagramPacket receivePacket=  new DatagramPacket(receive, receive.length);
		socket.setSoTimeout(100);
		try {
			socket.receive(receivePacket);
			packetIP = receivePacket.getAddress();
			packetPort = receivePacket.getPort();
			String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
			return message;
		}
		catch(InterruptedIOException timeout) {
			return "timeout";
		}
	}
	
	public byte[] receiveObject() throws Exception {
		byte[] receive = new byte[1024];
		DatagramPacket receivePacket=  new DatagramPacket(receive, receive.length);
		socket.receive(receivePacket);
		packetIP = receivePacket.getAddress();
		packetPort = receivePacket.getPort();
		return receive;
	}
	
	public int getPort() {
		return port;
	}

	public int getPacketPort() {
		return packetPort;
	}

	public InetAddress getPacketIP() {
		return packetIP;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetAddress getIP() {
		return IP;
	}

	public void displayMessage(String message) {
		System.out.println("Multicast Text: " + message);
	}

}
