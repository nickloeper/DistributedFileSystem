import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Contains some methods to list files and folders from a directory
 *
 */

public class ListFilesUtil {
	
    /**
     * List all the files and folders from a directory
     * @param directoryName to be listed
     * @throws IOException 
     */
    public void listFilesAndFolders(String directoryName) throws IOException{
        File directory = new File(directoryName);
        System.out.println("List of Files and folders in: " + directory.getCanonicalPath());
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            System.out.println(file.getName());
        }
    }
    
    /**
     * List all the files under a directory
     * @param directoryName to be listed
     * @throws IOException 
     */
    public ArrayList<String> listFiles(String directoryName) throws IOException{
        File directory = new File(directoryName);
        //get all the files from a directory
        ArrayList<String> list = new ArrayList<String>();
        System.out.println("List of Files in: " + directory.getCanonicalPath());
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile() && !file.getName().substring(0, 1).equals(".")){
                list.add(file.getName());
                System.out.println("-- " + file.getName());
            }
        }
        return list;
    }

    /**
     *List all the folder under a directory
     * @param directoryName to be listed
     * @throws IOException 
     */
    public void listFolders(String directoryName) throws IOException{
        File directory = new File(directoryName);
        //get all the files from a directory
        System.out.println("List of Folders in: " + directory.getCanonicalPath());

        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isDirectory()){
                System.out.println(file.getName());
            }
        }
    }

    /**
     * List all files from a directory and its subdirectories
     * @param directoryName to be listed
     * @throws IOException 
     */
    public void listFilesAndFilesSubDirectories(String directoryName) throws IOException{
        File directory = new File(directoryName);
        //get all the files from a directory
        System.out.println("List of Files and file subdirectories in: " + directory.getCanonicalPath());
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile()){
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()){
                listFilesAndFilesSubDirectories(file.getAbsolutePath());
            }
        }
    }

    /*public static void main (String[] args) throws IOException{

        ListFilesUtil listFilesUtil = new ListFilesUtil();
        //play with the path, this is using variations on files in folders for eclipse
        final String directoryLinuxMac =".//";
        //final String directoryLinuxMac =".//src";
        System.out.println("-------");
        listFilesUtil.listFiles(directoryLinuxMac);
        System.out.println("-------");
        listFilesUtil.listFilesAndFilesSubDirectories(directoryLinuxMac);
        System.out.println("-------");
        listFilesUtil.listFolders(directoryLinuxMac);
        System.out.println("-------");
        listFilesUtil.listFilesAndFolders(directoryLinuxMac);
    }*/

}