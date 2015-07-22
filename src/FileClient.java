import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FileClient {
	
	private List<File> files = new ArrayList<File>();
	private List<File> unknown = new ArrayList<File>();
	
	public FileClient() {
		
		try {
			
			say("Getting Files");
			File sourceFile = new File("C:\\Users\\Cameron\\Desktop\\Test Folder");
			getAllFiles(sourceFile);
			int sub = sourceFile.getParent().length()+1;
			say("All files ready");
			
			//Setup Sockets
			Socket control = new Socket("0", 21);
			Socket data = new Socket("0", 20);
			
			ObjectOutputStream controlOut = new ObjectOutputStream(control.getOutputStream());
			
			for (File file : files){
				
				say(file.toString().substring(sub));
				controlOut.writeObject(new FilePacket(Messages.INCOMING_FILE, new File(file.toString().substring(sub))));
				
				//Setup Streams
				InputStream in = new FileInputStream(file);
				OutputStream out = data.getOutputStream();
				
				//Send File
				byte[] bytes = new byte[8192];
			    int count;
			    while ((count = in.read(bytes)) > 0) {
			        out.write(bytes, 0, count);
			    }
			    
			    in.close();
				
			}
			
			controlOut.writeObject(new MessagePacket(Messages.FINISH));
			
			//Close Streams/Sockets
			control.close();
		    data.close();

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void getAllFiles(File f) {
		
		unknown.add(f);
		
		for (int i=0; i < unknown.size(); i++){
			
			File next = unknown.get(i);
			
			if (next.isFile())
				files.add(next);
			else
				for (File file : next.listFiles())
					unknown.add(file);
			
			unknown.remove(i--);
			
		}
		
	}
	
	protected void say(Object o) {
		System.out.println(o);
	}

	public static void main(String[] args) {
		new FileClient();
	}

}
