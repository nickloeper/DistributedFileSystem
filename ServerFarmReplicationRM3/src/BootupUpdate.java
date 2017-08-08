import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;

public class BootupUpdate implements Runnable {
	private MutableBoolean isPrimary;
	private UnicastUtilities getPrimID;
	private HashMap<String, Integer> fileList, primaryFileList, neededFiles;
	private int primaryId, myServerPort;
	private int[] ports;
	InetAddress IP1, IP2;
	
	public BootupUpdate(MutableBoolean isPrimary, InetAddress frontendIP, HashMap<String, Integer> fileList, InetAddress rm1IP, InetAddress rm2IP) throws Exception {
		this.isPrimary = isPrimary;
		getPrimID = new UnicastUtilities(6000, frontendIP);
		this.fileList = fileList;
		ports = new int[3];
		ports[0] = 6501;
		ports[1] = 6502;
		ports[2] = 6503;
		IP1 = rm1IP;
		IP2 = rm2IP;
		myServerPort = 6513;
		neededFiles = new HashMap<String, Integer>();
	}
	
	public void run() {
		if(!isPrimary.getValue()) {
			getPrimaryID();
			UnicastUtilities getPrimFileList;
			if(primaryId == 1) {
				try {
					getPrimFileList = new UnicastUtilities(ports[primaryId-1], IP1);
					getPrimaryFileList(getPrimFileList);
					getPrimFileList.closeSocket();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(primaryId == 2) {
				try {
					getPrimFileList = new UnicastUtilities(ports[primaryId-1], IP2);
					getPrimaryFileList(getPrimFileList);
					getPrimFileList.closeSocket();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			compareLists();
			sendNeededFilesList();
		}
		while(!isPrimary.getValue()) { 
			System.out.print("");
		}
		if(isPrimary.getValue()) {
			while(true) {
				System.out.println("Now in primary");
				sendList();
				streamFiles();
			}
		}
	}

	private void getPrimaryID() {
		try {
			getPrimID.sendString("give_id");
			primaryId = Integer.parseInt(getPrimID.receiveString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getPrimaryFileList(UnicastUtilities myUtil) {
		try {
			myUtil.sendString("give_file_list");
			ClientDataSerializer mySer = new ClientDataSerializer();
			primaryFileList = mySer.deserializeManagerPacketHashMap(myUtil.receiveObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void compareLists() {
		Iterator<String> it = primaryFileList.keySet().iterator();
		while(it.hasNext()) {
			String item = it.next();
			if(!fileList.containsKey(item)) {
				neededFiles.put(item, primaryFileList.get(item));
			}
		}
	}
	
	private void sendList() {
		ClientDataSerializer mySer = new ClientDataSerializer();
		try {
			UnicastUtilities sendList = new UnicastUtilities(6503);
			String message = sendList.receiveString();
			if(message.equals("give_file_list")) {
				UnicastUtilities sendOverList = new UnicastUtilities(sendList.getPacketPort(), sendList.getPacketIP());
				sendOverList.sendObject(mySer.serializeManagerPacket(fileList));
				sendOverList.closeSocket();
				sendList.closeSocket();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void streamFiles() {
		ClientDataSerializer mySer = new ClientDataSerializer();
		UnicastUtilities receiveNeededFiles, streamer, serverCommunicator;
		try {
			receiveNeededFiles = new UnicastUtilities(6503);
			HashMap<String, Integer> filesToStream = mySer.deserializeManagerPacketHashMap(receiveNeededFiles.receiveObject());
			streamer = new UnicastUtilities(receiveNeededFiles.getPacketPort(), receiveNeededFiles.getPacketIP());
			Iterator<String> stringIterator = filesToStream.keySet().iterator();
			serverCommunicator = new UnicastUtilities(myServerPort, InetAddress.getLocalHost());
			while(stringIterator.hasNext()) {
				serverCommunicator.sendString("primary");
				serverCommunicator.sendString(stringIterator.next()); /*Sends filename to server*/
				int fileSize = Integer.parseInt(serverCommunicator.receiveString());
				byte[] file = serverCommunicator.receiveFile(fileSize); /*Receivers file via udp*/
				streamer.sendString(Integer.toString(file.length));
				streamer.sendFile(file);
			}
			receiveNeededFiles.closeSocket();
			streamer.closeSocket();
			serverCommunicator.closeSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void sendNeededFilesList() {
		ClientDataSerializer mySer = new ClientDataSerializer();
		UnicastUtilities sendNeededFiles = null;
		if(primaryId == 1) {
			try {
				sendNeededFiles = new UnicastUtilities(ports[primaryId-1], IP1);
				sendNeededFiles.sendObject(mySer.serializeManagerPacket(neededFiles));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(primaryId == 2) {
			try {
				sendNeededFiles = new UnicastUtilities(ports[primaryId-1], IP2);
				sendNeededFiles.sendObject(mySer.serializeManagerPacket(neededFiles));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(neededFiles.toString());
		receiveStreamedFiles(neededFiles, sendNeededFiles);
	}
	
	private void receiveStreamedFiles(HashMap<String, Integer> neededFiles, UnicastUtilities sendNeededFiles) {
		int x = 0;
		try {
			ClientDataSerializer mySer = new ClientDataSerializer();
			UnicastUtilities sendToServer = new UnicastUtilities(myServerPort, InetAddress.getLocalHost());
			while(x < neededFiles.size()) {
				sendToServer.sendString("not primary");
				int fileSize = Integer.parseInt(sendNeededFiles.receiveString());
				System.out.println(fileSize);
				byte[] file = sendNeededFiles.receiveFile(fileSize);
				addToList(mySer.deserializeManagerFile(file).getName());
				sendToServer.sendString(Integer.toString(file.length)); /*Send to my primary server*/
				sendToServer.sendFile(file);
				x++;
			}
			System.out.println("Files have been updated on reboot");
			sendToServer.closeSocket();
			sendNeededFiles.closeSocket();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addToList(String fileName) {
		if(fileList.containsKey(fileName)) {
			fileList.put(fileName, fileList.get(fileName)+1);
		}
		else {
			fileList.put(fileName, 1);
		}
	}
}
