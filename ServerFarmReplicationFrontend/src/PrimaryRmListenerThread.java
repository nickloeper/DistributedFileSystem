
public class PrimaryRmListenerThread implements Runnable {
	MutableInteger primaryRmId;
	UnicastUtilities myUtility;
	
	public PrimaryRmListenerThread(MutableInteger primaryRmId) throws Exception {
		this.primaryRmId = primaryRmId;
		 myUtility = new UnicastUtilities(10005);
	}
	
	public void run() {
		while(true) {
			int nextId;
			try {
				nextId = Integer.parseInt(myUtility.receiveString());
				primaryRmId.setValue(nextId);
				System.out.println("The primary RM is RM" + nextId);
			} catch (Exception e) {}
		}
	}

}
