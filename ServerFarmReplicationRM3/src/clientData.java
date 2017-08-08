import java.io.Serializable;
import java.net.InetAddress;

@SuppressWarnings("serial")
public class clientData implements Serializable{
	private String fileName;
	private String command;
	private InetAddress IP;
	private int port;
	
	public clientData(String fileName, String command) {
		this.fileName = fileName;
		this.command = command;
	}

	public InetAddress getIP() {
		return IP;
	}

	public int getPort() {
		return port;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setIP(InetAddress IP) {
		this.IP = IP;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public String getCommand() {
		return command;
	}
}
