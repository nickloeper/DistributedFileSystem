import java.util.HashMap;

public class FrontendThread implements Runnable {
	clientData clientData;
	UnicastUtilities myUtility;
	HashMap<Integer, linkData> rmMap;
	MutableInteger primaryRmId;
	ClientDataSerializer serUtility;
	
	public FrontendThread(clientData clientData, HashMap<Integer, linkData> rmMap, MutableInteger primaryRmId) {
		this.clientData = clientData;
		this.rmMap = rmMap;
		this.primaryRmId = primaryRmId;
		serUtility = new ClientDataSerializer();
	}
	
	public void run() {
		linkData myLinkData = rmMap.get(primaryRmId.getValue()); 
		try {
			myUtility = new UnicastUtilities(myLinkData.getPort(), myLinkData.getIP());
			myUtility.sendObject(serUtility.serializeManagerPacket(clientData));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
