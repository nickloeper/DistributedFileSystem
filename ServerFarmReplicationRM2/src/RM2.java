import java.net.InetAddress;
import java.util.HashMap;

public class RM2 {
	public static void main(String[] args) throws Exception {
		/*linkData server1 = new linkData(InetAddress.getByName(args[0]), 9896); 
		linkData server2 = new linkData(InetAddress.getByName(args[1]), 9796); */
		linkData RM1 = new linkData(InetAddress.getByName(args[0]), 9001); 
		linkData RM3 = new linkData(InetAddress.getByName(args[1]), 9003); 
		
		HashMap<Integer, linkData> rmData = new HashMap<Integer, linkData>();
		rmData.put(1, RM1);
		rmData.put(3, RM3);
		
		/*Primary information*/
		MutableInteger priority = new MutableInteger(-1);
		MutableBoolean primaryAlive = new MutableBoolean(false);
		MutableBoolean isPrimary = new MutableBoolean(false);
		UnicastUtilities frontendListener = new UnicastUtilities(6002);
		
		/*Server information*/
		UnicastUtilities primaryServer = new UnicastUtilities(9201, InetAddress.getByName("localhost")); // TODO Keep as local host
		FileListWrapper fileList = new FileListWrapper();
		ClientDataSerializer serUtility = new ClientDataSerializer();
		
		/*Thread that grabs the file list from primary server*/
		Thread getFiles = new Thread(new GetFileListRM(fileList, InetAddress.getByName("localhost"))); // TODO Keep as local host
		getFiles.start();
		getFiles.join();
		
		/*Listens for priority requests*/
		Thread priorityListener = new Thread(new PriorityListenerThread());
		priorityListener.start();
		
		/*Gets priority and sets primary if equal to 3*/
		Thread startupThread = new Thread(new StartupThread(priority, primaryAlive, isPrimary, InetAddress.getByName(args[2]))); // TODO Change to args[3]
		startupThread.start();
		startupThread.join();
		
		/*Heartbeat's with primary RM*/
		Thread heartbeat = new Thread(new HeartbeatThread(isPrimary, priority, primaryAlive, InetAddress.getByName(args[2]))); // TODO Change to args[3]
		heartbeat.start();
		
		/*Thread that streams all the files that the file list lacks from primary RM*/
		Thread getFilesOnBootup = new Thread(new BootupUpdate(isPrimary, InetAddress.getByName(args[2]), fileList.getFileList(), InetAddress.getByName(args[0]), InetAddress.getByName(args[1])));
		getFilesOnBootup.setDaemon(true);
		getFilesOnBootup.start();
		
		/*Handle front-end queries or writes form primary RM*/
		while(true) {
			while(isPrimary.getValue()) {
				clientData cData = serUtility.deserializeManagerPacket(frontendListener.receiveObject());
				System.out.println("\nFilename/Command " + cData.getFileName() + "/" + cData.getCommand());
				primaryServer.sendObject(serUtility.serializeManagerPacket(cData));
				
				if(cData.getCommand().equals("upload")) {
					if(fileList.getFileList().get(cData.getFileName()) == null) {
						fileList.getFileList().put(cData.getFileName(), 1);
					}
					else {
						fileList.getFileList().put(cData.getFileName(), fileList.getFileList().get(cData.getFileName())+1);
					}
				}
				/*Talk to other RM's if there is a write*/
				Thread fileOtherRms = new Thread(new SendFileOtherRMs(rmData.get(1), rmData.get(3)));
				fileOtherRms.start();
				fileOtherRms.join();
			}
			while(!isPrimary.getValue()) {
				System.out.print("");
				
				/*Listen for file uploads to primary RM*/
				Thread listenForFiles = new Thread(new ListenFilesRM(isPrimary,InetAddress.getByName(args[2]), rmData, fileList.getFileList()));
				listenForFiles.start();
				listenForFiles.join();
			}
		}
	}
}
