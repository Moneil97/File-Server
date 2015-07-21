import java.io.File;
import java.io.Serializable;

@SuppressWarnings("serial")
public class FilePacket implements Serializable{

	String dest, fileName;
	Messages message;
	File file;

	public FilePacket(Messages message, String dest, String fileName) {
		this.dest = dest;
		this.fileName = fileName;
		this.message = message;
	}

	public FilePacket(Messages message, File file) {
		this.message = message;
		this.file = file;
	}

	public FilePacket(Messages message) {
		this.message = message;
	}

}

enum Messages{
	
	START, INCOMING_FILE, NEXT_FILE, FINISH,  
	
	
}