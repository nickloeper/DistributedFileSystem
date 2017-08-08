
public class GivePrimaryRM implements Runnable {
	private MutableInteger primaryId;
	UnicastUtilities listener, sender;
	
	public GivePrimaryRM(MutableInteger primaryId) throws Exception {
		this.primaryId = primaryId;
		listener = new UnicastUtilities(6000);
	}
	
	public void run() {
		while(true) {
			String message;
			try {
				message = listener.receiveString();
				if(message.equals("give_id")); {
					sender = new UnicastUtilities(listener.getPacketPort(), listener.getPacketIP());
					sender.sendString(Integer.toString(primaryId.getValue()));
					sender.closeSocket();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
