import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class ServerCommunicationThread implements Runnable {
	private clientData clientData;
	private UnicastUtilities myUtility, send;
	int tcpPort;
	
	public ServerCommunicationThread(clientData clientData) throws Exception {
		this.clientData = clientData;
		myUtility = new UnicastUtilities(3000);
	}
	
	public void run() {
		if(clientData.getCommand().equalsIgnoreCase("upload")) {
			try {
				myUtility.receiveString();
				send = new UnicastUtilities(myUtility.getPacketPort(), myUtility.getPacketIP());
				send.sendString("5400");
				sendFile();
				System.out.println(clientData.getFileName() + " has been uploaded to the server farm");
			} catch (Exception e) {e.printStackTrace();}
		}
		else {
			try {
				tcpPort = Integer.parseInt(myUtility.receiveString());
				receiveFile();
				System.out.println(clientData.getFileName() + " has been downloaded from the server farm");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void receiveFile() throws Exception {
		//this must wait for server to be created
		System.out.println(myUtility.receiveString());
		Socket socket = new Socket(myUtility.getPacketIP(), tcpPort);
		byte[] contents = new byte[10000];
		File newFile = new File("./"+clientData.getFileName());
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
	
	private void sendFile() throws Exception {
		ServerSocket ssocket = new ServerSocket(5400);
		send.sendString("Sending the file now...");
		Socket socket = ssocket.accept();
		
		File file = new File("./"+clientData.getFileName());
		if(file.exists()) {
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
		else {System.out.println("This file does not exist");}     
	}
}
