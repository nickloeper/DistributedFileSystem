import java.util.HashMap;

public class SendFileList implements Runnable {
	private HashMap<String, Integer> fileList;
	ClientDataSerializer mySer;
	UnicastUtilities rmCommunicator;
	
	public SendFileList(HashMap<String, Integer> fileList) throws Exception {
		this.fileList = fileList;
		mySer = new ClientDataSerializer();
		rmCommunicator = new UnicastUtilities(5101);
	}
	
	public void run() {
		while(true) {
			String message = receiveMessage();
			if(message.equals("file_list")) {
				sendFileList();
			}
		}
	}
	
	public String receiveMessage() {
		String message;
		try {
			message = rmCommunicator.receiveString();
			return message;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void sendFileList() {
		try {
			UnicastUtilities sendFile = new UnicastUtilities(rmCommunicator.getPacketPort(), rmCommunicator.getPacketIP());
			sendFile.sendObject(mySer.serializeManagerPacket(fileList));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
