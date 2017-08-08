import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class ListenFilesRM implements Runnable {
	private MutableBoolean isPrimary;
	private int primaryID;
	private UnicastUtilities getPrimID, receiveFile;
	private HashMap<Integer, linkData> rmData;
	private byte[] file;
	private HashMap<String, Integer> fileList;
	
	public ListenFilesRM(MutableBoolean isPrimary, InetAddress IP, HashMap<Integer, linkData> rmData, HashMap<String, Integer> fileList) throws Exception {
		this.isPrimary = isPrimary;
		getPrimID = new UnicastUtilities(6000, IP);
		this.rmData = rmData;
		this.fileList = fileList;
	}
	
	public void run() {
		getPrimaryID();
		receiveFile();
	}
	
	private byte[] receiveFile() {
		try {
			receiveFile = new UnicastUtilities(8102);
			String message = receiveFile.receiveStringTimeout();
			if(!message.equals("timeout")) {
				int size = Integer.parseInt(message);
				file = receiveFile.receiveFile(size);
				System.out.println("I have received the file of length(" + file.length + ")");
				updateFileList(file);
				sendToPrimaryServer(file);
			}
			receiveFile.closeSocket(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void getPrimaryID() {
		try {
			getPrimID.sendString("give_id");
			primaryID = Integer.parseInt(getPrimID.receiveString());
			//System.out.println("\nThe primary id is " + primaryID +  "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void sendToPrimaryServer(byte[] file) {
		try {
			UnicastUtilities sendToPrimary = new UnicastUtilities(4502, InetAddress.getLocalHost());
			sendToPrimary.sendString(Integer.toString(file.length));
			sendToPrimary.sendFile(file);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateFileList(byte[] file) {
		ClientDataSerializer mySer = new ClientDataSerializer();
		File tempFile = mySer.deserializeManagerFile(file);
		if(fileList.get(tempFile.getName()) == null) {
			fileList.put(tempFile.getName(), 1);
		}
		else {
			fileList.put(tempFile.getName(), fileList.get(tempFile.getName())+1);
		}
	}
}
