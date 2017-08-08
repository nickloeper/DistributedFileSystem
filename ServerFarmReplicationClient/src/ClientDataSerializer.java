import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
	
	public static clientData deserializeManagerPacket(byte[] data) {
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
}
