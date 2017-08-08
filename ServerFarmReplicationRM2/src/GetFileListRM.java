import java.net.InetAddress;
import java.util.HashMap;

public class GetFileListRM implements Runnable {
	private FileListWrapper fileList;
	private UnicastUtilities getListUtil;
	private ClientDataSerializer mySer;
	
	public GetFileListRM(FileListWrapper fileList, InetAddress IP) throws Exception {
		this.fileList = fileList;
		getListUtil = new UnicastUtilities(5102, IP);
		mySer = new ClientDataSerializer();
	}
	
	public void run() {
		sendMessage();
		fileList.setFileList(receiveList());
	}
	
	private void sendMessage() {
		try {
			getListUtil.sendString("file_list");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private HashMap<String, Integer> receiveList() {
		try {
			return mySer.deserializeManagerPacketHashMap(getListUtil.receiveObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
