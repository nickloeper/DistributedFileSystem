import java.net.InetAddress;

public class SendFileOtherRMs implements Runnable {
	private UnicastUtilities RM1, RM2, serverCommunicatorSend, serverCommunicatorReceive;
	
	public SendFileOtherRMs(linkData RM2, linkData RM3) throws Exception {
		this.RM1 = new UnicastUtilities(8101, RM2.getIP());
		this.RM2 = new UnicastUtilities(8102, RM3.getIP());
		serverCommunicatorReceive = new UnicastUtilities(7101);
		serverCommunicatorSend = new UnicastUtilities(7102, InetAddress.getLocalHost());
	}
	
	public void run() {
		byte[] fileBytes = receiveFileBytes();
		System.out.println("Sending filebytes(" + fileBytes.length + ") to the other RM's\n");
		sendFileBytes(RM1, fileBytes);
		sendFileBytes(RM2, fileBytes);
		serverCommunicatorReceive.closeSocket();
		serverCommunicatorSend.closeSocket();
	}

	private void sendFileBytes(UnicastUtilities tempUtil, byte[] fileBytes) {
		try {
			tempUtil.sendString(Integer.toString(fileBytes.length));
			tempUtil.sendFile(fileBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private byte[] receiveFileBytes() {
		try {
			int fileLength = Integer.parseInt(serverCommunicatorReceive.receiveString());
			serverCommunicatorSend.sendString("I am waiting for the file...");
			return serverCommunicatorReceive.receiveFile(fileLength);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
