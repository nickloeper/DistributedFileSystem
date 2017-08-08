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

public class FilesFromOthersRms implements Runnable {
	UnicastUtilities rmCommunicator, server2, server3;
	ClientDataSerializer mySer;
	String fileName;
	byte[] file;
	
	public FilesFromOthersRms() throws Exception {
		rmCommunicator = new UnicastUtilities(4501);
		mySer = new ClientDataSerializer();
		server2 = new UnicastUtilities(4012, InetAddress.getLocalHost()); 
		server3 = new UnicastUtilities(4013, InetAddress.getLocalHost());
	}
	
	public void run() {
		while(true) {
			try {
				int size = Integer.parseInt(rmCommunicator.receiveString());
				file = rmCommunicator.receiveFile(size);
				writeFile(file);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateServers();
		}
	}

	private void writeFile(byte[] file) throws IOException {
		File thisFile = mySer.deserializeManagerFile(file);
		fileName = thisFile.getName();
		File newFile = new File("./" + fileName);
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
	
	private void sendFile(int port, UnicastUtilities util) throws Exception {
		ServerSocket ssocket = new ServerSocket(port);
		util.sendString(fileName); 
		Socket socket = ssocket.accept();
		
		File file = new File("./" + fileName);
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
	
	private void updateServers() {
		updateServer(server2);
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
}
