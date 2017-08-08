import java.net.InetAddress;

public class HeartbeatThread implements Runnable {
	MutableInteger priority;
	multicastUtilities heartbeatMcast;
	MutableBoolean isPrimary;
	MutableBoolean primaryAlive;
	private int id = 1;
	private UnicastUtilities frontend;
	
	public HeartbeatThread(MutableBoolean isPrimary, MutableInteger priority, MutableBoolean primaryAlive, InetAddress IP) throws Exception {
		heartbeatMcast = new multicastUtilities(InetAddress.getByName("224.51.8.254"), 9999);
		heartbeatMcast.joinGroup();
		this.isPrimary = isPrimary;
		this.priority = priority;
		this.primaryAlive = primaryAlive;
		frontend = new UnicastUtilities(10005, IP);
	}
	
	public void run() {
		while(!isPrimary.getValue()) {
			int primaryDeadTries = 0;
			while(primaryDeadTries < 3) {
				if(receiveBeat().equals("timeout")) {
					primaryDeadTries++;
				}
				System.out.println("Primary is alive");
			}
			setInformation();
			sleep();
		}
		if(isPrimary.getValue()) {
			while(true) {
				sendBeat();
			}
		}
	}
	
	private void sendBeat() {
		try {
			System.out.println("Sending...");
			heartbeatMcast.sendString(Integer.toString(priority.getValue()));
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String receiveBeat() {
		String beatMessage = "";
		try {
			beatMessage = heartbeatMcast.receiveString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beatMessage;
	}
	
	private void sleep() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void setInformation() {
		System.out.println("Primary has died, now starting election");
		primaryAlive.setValue(false);
		priority.setValue(priority.getValue()+1);
		if(priority.getValue() == 3) {
			isPrimary.setValue(true);
			primaryAlive.setValue(true);
			try {
				frontend.sendString(Integer.toString(id));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
