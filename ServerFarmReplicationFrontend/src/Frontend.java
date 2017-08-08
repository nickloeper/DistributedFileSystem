import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.util.HashMap;

public class Frontend {
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		/*Client communication*/
		UnicastUtilities myUtility = new UnicastUtilities(10000); 
		
		/*RM communication*/
		ClientDataSerializer serUtility = new ClientDataSerializer(); 
		HashMap<Integer, linkData> rmMap = new HashMap<Integer, linkData>();
		MutableInteger primaryRmId = new MutableInteger(-1); 
		
		linkData linkData1 = new linkData(InetAddress.getByName(args[0]), 6001); // RM1 & Change IP
		linkData linkData2 = new linkData(InetAddress.getByName(args[1]), 6002); // RM2 & Change IP
		linkData linkData3 = new linkData(InetAddress.getByName(args[2]), 6003); // RM3 & Change IP
		
		rmMap.put(1, linkData1);
		rmMap.put(2, linkData2);
		rmMap.put(3, linkData3);
		
		/*Primary RM listener*/
		Thread PrimListenerWrap = new Thread(new PrimaryRmListenerThread(primaryRmId));// Primary RM Listener
		PrimListenerWrap.setDaemon(true);
		PrimListenerWrap.start();
		
		/* Wait for a primary to come alive and thread for listening for primary RM*/
		boolean onePrint = true;
		while(primaryRmId.getValue() == -1) {
			if(onePrint) {
				System.out.println("There are no available RM's currently");
			}
			onePrint = false;
		}
		
		/*Gives primary id to RM's that need it*/
		Thread givePrimaryId = new Thread(new GivePrimaryRM(primaryRmId));
		givePrimaryId.setDaemon(true);
		givePrimaryId.start();
		
		/*Listen for client queries*/
		while(true) {
			clientData clientData = serUtility.deserializeManagerPacket(myUtility.receiveObject());
			System.out.println("Filename/Command " + clientData.getFileName() + "/" + clientData.getCommand());
			clientData.setIP(myUtility.getPacketIP());
			Thread myThreadWrapper = new Thread(new FrontendThread(clientData, rmMap, primaryRmId));
			myThreadWrapper.start();				
			myThreadWrapper.join();
		}
	}
}