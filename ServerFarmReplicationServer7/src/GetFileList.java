import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GetFileList implements Runnable {
	private HashMap<String, Integer> fileList;
	ListFilesUtil fileUtil;

	public GetFileList(HashMap<String, Integer> fileList) {
		this.fileList = fileList;
		fileUtil = new ListFilesUtil();
	}
	
	public void run() {
		try {
			getFiles();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	private void getFiles() throws IOException {
		ArrayList<String> files = fileUtil.listFiles(".//");
		for(String file:files) {
			fileList.put(file, 1);
		}
	}
}
