import java.util.HashMap;

public class FileListWrapper {
	private HashMap<String,Integer> fileList;
	
	public FileListWrapper() {
		fileList = new HashMap<String,Integer>();
	}

	public HashMap<String,Integer> getFileList() {
		return fileList;
	}

	public void setFileList(HashMap<String,Integer> fileList) {
		this.fileList = fileList;
	}
}
