import java.io.Serializable;
import java.net.InetAddress;

public class linkData implements Serializable {
	InetAddress IP;
	int port;
	
	public linkData() {}
	
	public linkData(InetAddress IP, int port) {
		this.IP = IP;
		this.port = port;
	}

	public InetAddress getIP() {
		return IP;
	}

	public int getPort() {
		return port;
	}
}
