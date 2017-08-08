public class Server9 {
	public static void main(String[] args) throws Exception {
		/*Thread for listening to file updates*/
		Thread fileListener = new Thread(new ListenForFileUpdates());
		fileListener.start();
		
		Thread fileOtherRmListener = new Thread(new ListenForFilesFromPrimary());
		fileOtherRmListener.start();
	}
}
