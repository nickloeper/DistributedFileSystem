import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class ClientDataSerializer {

	public ClientDataSerializer() {}
	
	public byte[] serializeManagerPacket(clientData temp) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream buf;
		byte[] clientData = new byte[1024];
		try {
			buf = new ObjectOutputStream(out);
			buf.writeObject(temp);
			clientData = out.toByteArray();
			return clientData;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public clientData deserializeManagerPacket(byte[] data) {
		try {
			ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(data));
			clientData obj = (clientData) objStream.readObject();
			objStream.close();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public HashMap<String, Integer> deserializeManagerPacketHashMap(byte[] data) {
		try {
			ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(data));
			HashMap<String, Integer> obj = (HashMap<String, Integer>) objStream.readObject();
			objStream.close();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] serializeManagerPacket(HashMap<String,Integer> temp) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream buf;
		byte[] fileList = new byte[1024];
		try {
			buf = new ObjectOutputStream(out);
			buf.writeObject(temp);
			fileList = out.toByteArray();
			return fileList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
