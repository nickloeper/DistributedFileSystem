import java.net.InetAddress;
import java.util.HashMap;

public class Server7 {
	private boolean primary = true;
	
	public static void main(String[] args) throws Exception {
		UnicastUtilities RMlistener = new UnicastUtilities(9301);
		HashMap<String, Integer> fileList = new HashMap<String, Integer>();
		ClientDataSerializer mySer = new ClientDataSerializer();
		
		/*Thread for getting file list on startup*/
		Thread getFileList = new Thread(new GetFileList(fileList));
		getFileList.start();
		getFileList.join();

		/*Thread for sending file list to RM when needed*/
		Thread sendFileList = new Thread(new SendFileList(fileList));
		sendFileList.start();
		
		/*Thread for getting files on boot update or sending them for other boot-ups*/
		Thread bootupHandler = new Thread(new BootupFileWrite(fileList));
		bootupHandler.start();
		
		/*Thread listening for files from other RMs if parent RM is not primary*/
		Thread getFromOtherRMs = new Thread(new FilesFromOthersRms());
		getFromOtherRMs.start();
		
		while(true) {
			clientData clientData = mySer.deserializeManagerPacket(RMlistener.receiveObject());
			
			/*Reads or writes to/from server and then updates other servers*/
			Thread HandleRequests = new Thread(new HandleClientRequest(clientData));
			HandleRequests.start();
			HandleRequests.join();
			System.out.println("Done with file request");
			
			/*Streams the file to its RM*/
			Thread sendFileToRM = new Thread(new SendFileToRMs(clientData));
			sendFileToRM.start();
			sendFileToRM.join();
		}
		
	}

}