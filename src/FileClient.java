import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class FileClient {

	private Socket sock;
	
	public FileClient() {
		
		
		try {
			
			sock = new Socket("0", 21);
			File file = new File("F:\\Cloud Storages\\Dropbox\\My Documents\\Smart TV Installation Invoice.docx");
			//new ObjectOutputStream(sock.getOutputStream()).writeObject(new File("F:\\Cloud Storages\\Dropbox\\My Documents\\Smart TV Installation Invoice.docx"));
			
			 InputStream in = new FileInputStream(file);
			 OutputStream out = sock.getOutputStream();
			
			byte[] bytes = new byte[8192];

		    int count;
		    while ((count = in.read(bytes)) > 0) {
		        out.write(bytes, 0, count);
		    }
		    
		    out.close();
			
//			while (true){
//				try {
//					Thread.sleep(50);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public static void main(String[] args) {
		new FileClient();
	}

}
