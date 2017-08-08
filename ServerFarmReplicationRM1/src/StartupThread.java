import java.net.InetAddress;

public class StartupThread implements Runnable {
	private MutableBoolean primaryAlive;
	private MutableBoolean isPrimary;
	private MutableInteger priority;
	private multicastUtilities priorityMcast;
	private int rmCount;
	private int id;
	private UnicastUtilities frontend;
	
	public StartupThread(MutableInteger priority, MutableBoolean primaryAlive, MutableBoolean isPrimary, InetAddress IP) throws Exception{
		this.primaryAlive = primaryAlive;
		this.isPrimary = isPrimary;
		this.priority = priority;
		priorityMcast = new multicastUtilities(InetAddress.getByName("224.50.8.244"), 9998);
		priorityMcast.joinGroup();
		rmCount = 0;
		id = 1;
		frontend = new UnicastUtilities(10005, IP);
	}
	
	public void run() {
		sendRequest();
		recycleMessage();
		String message = "";
		while(!message.equals("timeout")) {
			message = receiveMessage();
			if(!message.equals("timeout")) {
				rmCount++;
			}
		}
		setInformation();
	}
	
	private void sendRequest() {
		try {
			priorityMcast.sendString("get_priority" + id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void recycleMessage() {
		try {
			priorityMcast.receiveString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String receiveMessage() {
		try {
			return priorityMcast.receiveString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void setInformation() {
		priority.setValue(3-rmCount);
		if(priority.getValue() == 3) {
			isPrimary.setValue(true);
			primaryAlive.setValue(true);
			System.out.println("I am now the primary and my priority is: " + priority.getValue());
			try {
				frontend.sendString(Integer.toString(id));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			primaryAlive.setValue(true);
			System.out.println("I am not the primary and my priority is: " + priority.getValue());
		}
	}

}
