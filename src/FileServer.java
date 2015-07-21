import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {

	ServerSocket server;
	private Socket control;
	
	public FileServer() {

		try {
			server = new ServerSocket(21);
			say("waiting");
			control = server.accept();
			say("connected to: " + control);
			
			File file = new File("C:\\Users\\Cameron\\Desktop\\Test Folder\\test.docx");
			
			InputStream in = control.getInputStream();
			FileOutputStream out = new FileOutputStream(file);
			
			byte[] bytes = new byte[8192];

		    int count;
		    while ((count = in.read(bytes)) > 0) {
		    	say("writing");
		        out.write(bytes, 0, count);
		        say("done writing");
		    }
		    
		    say("closing");
		    in.close();
		    out.close();
		    say("done");
			
			
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					
//					InputStream in;
//					try {
//						in = control.getInputStream();
//						ObjectInputStream oi = new ObjectInputStream(in);
//						
//						while (true){
//							//say((byte)in.read());
//							//say(oi.readObject());
//							
//							Object o = oi.readObject();
//							
//							//say(o);
//							
//							if (o instanceof File){
//								say("received file: " + o);
//							}
//							else{
//								say("unknown object: " + o.getClass());
//							}
//							
//							
//						}
//						
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					} catch (ClassNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
//			}).start();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
//			try {
//				server.close();
//				control.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		
		
		
		
	}

	protected void say(Object o) {
		System.out.println(o);
	}

	public static void main(String[] args) {
		new FileServer();
	}

}
