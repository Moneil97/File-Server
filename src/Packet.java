import java.io.File;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Packet implements Serializable{}

@SuppressWarnings("serial")
class FilePacket extends Packet implements Serializable{

	//String dest, fileName;
	Messages message;
	File file;

//	public FilePacket(Messages message, String dest, String fileName) {
//		this.dest = dest;
//		this.fileName = fileName;
//		this.message = message;
//	}

	public FilePacket(Messages message, File file) {
		this.message = message;
		this.file = file;
	}

}

@SuppressWarnings("serial")
class MessagePacket extends Packet implements Serializable{
	
	Messages message;
	
	public MessagePacket(Messages message) {
		this.message = message;
	}
	
}

@SuppressWarnings("serial")
class ListPacket extends Packet implements Serializable{
	
	File[] files;
	
	public ListPacket(File[] files) {
		this.files = files;
	}
	
}

@SuppressWarnings("serial")
class RequestListPacket extends Packet implements Serializable{
	
	String currentDir;
	
	public RequestListPacket(String currentDir) {
		this.currentDir = currentDir;
	}
	
}

@SuppressWarnings("serial")
class CDPacket extends Packet implements Serializable{
	
	String /*currentDir,*/ newDir;
	
	public CDPacket(String newDir) {
		//this.currentDir = currentDir;
		this.newDir = newDir;
	}
	
}

enum Messages{
	
	START, INCOMING_FILE, NEXT_FILE, FINISH, 
	LIST, CD, CD_SUCCESS, CD_FAIL
	
}