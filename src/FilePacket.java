import java.io.Serializable;

@SuppressWarnings("serial")
public class FilePacket implements Serializable{

	String dest, fileName;

	public FilePacket(String dest, String fileName) {
		this.dest = dest;
		this.fileName = fileName;
	}

}
