import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class HandleClientRequest implements Runnable {
	private clientData request;
	private UnicastUtilities clientCommunicator, server2, server3;
	private int tcpPort;

	public HandleClientRequest(clientData request) throws Exception {
		this.request = request;
		clientCommunicator = new UnicastUtilities(request.getPort(), request.getIP());
		server2 = new UnicastUtilities(4102, InetAddress.getLocalHost());
		server3 = new UnicastUtilities(4103, InetAddress.getLocalHost());
	}
	
	public void run() {
		/*Handle upload and update other servers*/
		if(request.getCommand().equals("upload")) {
			getTcpPort();
			try {
				receiveFile();
			} catch (Exception e) {e.printStackTrace();
			}
			updateServers();
			System.out.println("The file _" + request.getFileName() + "_ has been received and saved in the farm\n");
		}
		/*Handle download requests*/
		else {
			try {
				clientCommunicator.sendString("5400");
				sendFile(5400, clientCommunicator);
				System.out.println("The file _" + request.getFileName() + "_ has been sent to client\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateServers() {
		updateServer(server2);
		updateServer(server3);
		System.out.println("\nServer farm has been updated");
	}
	
	private void updateServer(UnicastUtilities serverCommunicator) {
		try {
			serverCommunicator.sendString("3102");
			sendFile(3102, serverCommunicator);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendFile(int port, UnicastUtilities util) throws Exception {
		ServerSocket ssocket = new ServerSocket(port);
		util.sendString(request.getFileName()); 
		Socket socket = ssocket.accept();
		
		File file = new File("./" + request.getFileName());
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
	
	private synchronized void receiveFile() throws Exception {
		System.out.println(clientCommunicator.receiveString());
		Socket socket = new Socket(clientCommunicator.getIP(), tcpPort);
		byte[] contents = new byte[10000];
		File newFile = new File("./" + request.getFileName());
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
	
	private void getTcpPort() {
		try {
			clientCommunicator.sendString("ready_for_file");
			tcpPort = Integer.parseInt(clientCommunicator.receiveString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
