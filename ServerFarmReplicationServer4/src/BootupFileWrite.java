import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class BootupFileWrite implements Runnable {
	private UnicastUtilities rmCommunicator, server1, server3;
	private int primaryId, myId;
	private HashMap<String, Integer> filelist;
	private ClientDataSerializer mySer;
	String filename;
	
	public BootupFileWrite(HashMap<String, Integer> filelist) throws Exception {
		myId = 2;
		this.filelist = filelist;
		rmCommunicator = new UnicastUtilities(6512);
		mySer = new ClientDataSerializer();
		server1 = new UnicastUtilities(4202, InetAddress.getLocalHost()); 
		server3 = new UnicastUtilities(4203, InetAddress.getLocalHost());
	}
	
	public void run() {
		while(true) {
			String rmMessage = "";
			try {
				rmMessage = rmCommunicator.receiveString();
			} catch (Exception e) {e.printStackTrace();}
	
			if(rmMessage.equalsIgnoreCase("primary")) {
				getAndSendFile();
			}
			else {
				System.out.println("here");
				receiveAndWriteSend();
			}
		}
	}

	private void receiveAndWriteSend() {
		try {
			int fileSize = Integer.parseInt(rmCommunicator.receiveString());
			byte[] file = rmCommunicator.receiveFile(fileSize);
			System.out.println(file.length);
			writeFile(file);
			updateServers();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeFile(byte[] file) throws IOException {
		File thisFile = mySer.deserializeManagerFile(file);
		filename = thisFile.getName();
		File newFile = new File("./" + filename);
		FileOutputStream fis;
		try {
			fis = new FileOutputStream(newFile);
			BufferedOutputStream buf = new BufferedOutputStream(fis);
			buf.write(file);
			buf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void updateServers() {
		updateServer(server1);
		updateServer(server3);
		System.out.println("\nServer farm has been updated");
	}
	
	private void updateServer(UnicastUtilities serverCommunicator) {
		try {
			serverCommunicator.sendString("3101");
			sendFile(3101, serverCommunicator);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendFile(int port, UnicastUtilities util) throws Exception {
		ServerSocket ssocket = new ServerSocket(port);
		util.sendString(filename); 
		Socket socket = ssocket.accept();
		
		File file = new File("./" + filename);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis); 
        
        OutputStream out = socket.getOutputStream();
        byte[] contents;
        long fileLength = file.length(); 
        long current = 0;
         
        while(current!=fileLength) { 
            int size = 10000;
            if(fileLength - current >= size) {
            	 current += size;    
            }
            else { 
                size = (int)(fileLength - current); 
                current = fileLength;
            } 
            contents = new byte[size]; 
            bis.read(contents, 0, size); 
            out.write(contents);
        }   
        
        out.flush(); 
        socket.close();
        ssocket.close();
        bis.close();
	}
	
	private void getAndSendFile() {
		try {
			String fileName = rmCommunicator.receiveString();
			File newFile = new File("./" + fileName);
			UnicastUtilities sendFile = new UnicastUtilities(rmCommunicator.getPacketPort(), rmCommunicator.getPacketIP());
			sendFile.sendString(Long.toString(newFile.length()));
			sendFile.sendFile(mySer.serializeManagerPacket(newFile));
			sendFile.closeSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
