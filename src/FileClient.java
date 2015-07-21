import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileClient {
	
	public FileClient() {
		
		try {
			//Setup Sockets
			Socket control = new Socket("0", 21);
			Socket data = new Socket("0", 20);
			
			ObjectOutputStream controlOut = new ObjectOutputStream(control.getOutputStream());
			
			//Send Destination of the file about to be sent
			controlOut.writeObject(new FilePacket("C:\\Users\\Cameron\\Desktop\\Test Folder", "Invoice.docx"));
			
			//File to send
			File file = new File("F:\\Cloud Storages\\Dropbox\\My Documents\\Smart TV Installation Invoice.docx");
			
			//Setup Streams
			InputStream in = new FileInputStream(file);
			OutputStream out = data.getOutputStream();
			
			
			//Send File
			byte[] bytes = new byte[8192];
		    int count;
		    while ((count = in.read(bytes)) > 0) {
		        out.write(bytes, 0, count);
		    }
		    
		    //Close Streams/Sockets
		    in.close();
		    out.close();
		    control.close();
		    data.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		new FileClient();
	}

}
