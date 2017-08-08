import java.net.InetAddress;
import java.util.Scanner;

public class Client {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		Scanner kbReader = new Scanner(System.in);
		ClientDataSerializer serUtility = new ClientDataSerializer();
		UnicastUtilities myUtility = new UnicastUtilities(10000, InetAddress.getByName(args[0]));
		/*UnicastUtilities myUtility = new UnicastUtilities(10000, InetAddress.getByName("localhost"));*/
		
		System.out.print("Enter a filename (lowercase): "); //Get filename and send to middleware
		String fileName = kbReader.nextLine();
		System.out.print("Enter a filename (upload or download): "); //Get filename and send to middleware
		String command = kbReader.nextLine();
		System.out.println();
		
		clientData clientData = new clientData(fileName, command);
		clientData.setPort(3000); //Port for sending message before the socket accept
		myUtility.sendObject(serUtility.serializeManagerPacket(clientData));
		
		Thread SCT = new Thread(new ServerCommunicationThread(clientData));
		SCT.start();
		SCT.join();
	}
}
