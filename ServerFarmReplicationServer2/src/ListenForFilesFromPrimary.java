import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class ListenForFilesFromPrimary implements Runnable {
	private UnicastUtilities primaryCommunicator;
	private int tcpPort;
	
	public ListenForFilesFromPrimary() throws Exception {
		primaryCommunicator = new UnicastUtilities(4102);
	}
	
	public void run() {
		while(true) {
			try {
				tcpPort = Integer.parseInt(primaryCommunicator.receiveString());
				receiveFile();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void receiveFile() throws Exception {
		String fileName = primaryCommunicator.receiveString();
		Socket socket = new Socket(primaryCommunicator.getPacketIP(), tcpPort);
		byte[] contents = new byte[10000];
		File newFile = new File("./" + fileName);
		FileOutputStream fis = new FileOutputStream(newFile);
		BufferedOutputStream buf = new BufferedOutputStream(fis);
		InputStream in = socket.getInputStream();
		
		int bytesRead = 0;
		while((bytesRead=in.read(contents))!=-1) {
			 buf.write(contents, 0, bytesRead); 
		}
		
		buf.flush();
		socket.close();
		buf.close();
	}
}

