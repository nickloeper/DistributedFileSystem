import java.net.InetAddress;

public class PriorityListenerThread implements Runnable {
	private multicastUtilities priorityMcast;
	private int id;
	
	public PriorityListenerThread() throws Exception {
		priorityMcast = new multicastUtilities(InetAddress.getByName("224.50.8.244"), 9998);
		priorityMcast.joinGroup();
		id = 2;
	}
	
	public void run() {
		while(true) {
			String message = receiveMessage();
			while(message.equals("alive")) {
				message = receiveMessage();
			}
			if(message.substring(0, 12).equals("get_priority") && !message.substring(12, 13).equals(Integer.toString(id))) {
				sendAliveMessage();
			}
		}
	}
	
	private String receiveMessage() {
		try {
			return priorityMcast.receiveString1();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void sendAliveMessage() {
		try {
			priorityMcast.sendString("alive");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
