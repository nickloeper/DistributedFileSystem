import java.net.InetAddress;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.Iterator;

public class GetFileListRM implements Runnable {
	private FileListWrapper fileList;
	private UnicastUtilities getListUtil;
	private ClientDataSerializer mySer;
	
	public GetFileListRM(FileListWrapper fileList, InetAddress IP) throws Exception {
		this.fileList = fileList;
		getListUtil = new UnicastUtilities(5101, IP);
		mySer = new ClientDataSerializer();
	}
	
	public void run() {
		sendMessage();
		fileList.setFileList(receiveList());
		System.out.println(fileList.getFileList().toString());
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
