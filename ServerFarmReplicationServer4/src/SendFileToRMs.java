import java.io.File;
import java.net.InetAddress;
import java.nio.file.*;

public class SendFileToRMs implements Runnable {
	private UnicastUtilities rmCommunicatorSend, rmCommunicatorReceive;
	private clientData clientData;
	private ClientDataSerializer mySer;
	
	public SendFileToRMs(clientData clientData) throws Exception{
		this.clientData = clientData;
		this.rmCommunicatorSend = new UnicastUtilities(7101, InetAddress.getLocalHost());
		rmCommunicatorReceive = new UnicastUtilities(7102);
		mySer = new ClientDataSerializer();
	}
	
	public void run() {
		try {
			System.out.println("Sending file to RM");
			File temp = new File("./" + clientData.getFileName());
			rmCommunicatorSend.sendString(Integer.toString((int)temp.length()));
			System.out.println(rmCommunicatorReceive.receiveString());
			rmCommunicatorSend.sendObject(mySer.serializeManagerPacket(temp));
			System.out.println("Done sending the file to the RMs...\n\n");
			rmCommunicatorReceive.closeSocket();
			rmCommunicatorSend.closeSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
