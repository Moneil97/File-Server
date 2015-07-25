import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FileServer {

	ServerSocket controlServer, dataServer;
	Socket control, data;
	final String root = "C:\\Users\\Cameron\\Desktop\\FileServer Database\\";
	int rootSub = root.toString().length();
	ObjectOutputStream controlOut;
	
	public FileServer() {

		try {
			//Setup Server
			controlServer = new ServerSocket(21);
			dataServer = new ServerSocket(20);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (true){
			
			try {
				
				say("waiting");
				//Wait for a Client to connect
				control = controlServer.accept();
//				data = dataServer.accept();
				
				say("connected to: " + control);
				//say("connected to: " + data);
				
				//Setup Streams
				ObjectInputStream controlIn = new ObjectInputStream(control.getInputStream());
				controlOut = new ObjectOutputStream(control.getOutputStream());
				//InputStream dataIn = data.getInputStream();
				//FileOutputStream dataOut;
				
				//while connected
				while (true){
					
					//Get packet from Client
					Object o = controlIn.readObject();
					
					
					if (o instanceof FilePacket){
//						FilePacket packet = (FilePacket)o;
//						
//						//Check for and create necessary folders
//						File dest = addRoot(packet.file.getParent());//new File(root + packet.file.getParent());
//						if (!dest.exists()){
//							dest.mkdirs();
//							say("Folder Created: FileServer Database\\" + packet.file.getParent());
//							Thread.yield();
//						}
//						
//						//Destination File
//						dest = addRoot(packet.file);//new File(root.toString() + packet.file.toString());
//						
//						dataOut = new FileOutputStream(dest);
//						
//						say("writing");
//						//Write Client's file to HDD
//						byte[] bytes = new byte[8192];
//						int count;
//						while ((count = dataIn.read(bytes)) > 0) {
//							dataOut.write(bytes, 0, count);
//						}
//						
//						say("File Created: " + dest);
//						dataOut.close();

					}
					else if (o instanceof MessagePacket){
						MessagePacket packet = (MessagePacket)o;
						
//						if (packet.message == Messages.FINISH){
//							
//							dataIn.close();
//							controlIn.close();
//							data.close();
//							control.close();
//							break;
//						}
					}
					else if (o instanceof RequestListPacket){
						RequestListPacket packet = (RequestListPacket)o;
						
						say("Client has requested list from dir: " + packet.currentDir);
						say("Client has requested list from dir: " + root + packet.currentDir);
						
						controlOut.writeObject(new ListPacket(removeRoots(addRoot(packet.currentDir).listFiles())));
					}
					else if (o instanceof CDPacket){
						CDPacket packet = (CDPacket)o;
						
						say("received CDPacket");
						
						File requestedDir = addRoot(packet.newDir);
						say("Client Requested Access to: " + requestedDir);
						
						if (requestedDir.exists())
							controlOut.writeObject(new MessagePacket(Messages.CD_SUCCESS));
						else
							controlOut.writeObject(new MessagePacket(Messages.CD_FAIL));
						
					}
					else if (o instanceof RequestFilePacket){
						
						RequestFilePacket packet = (RequestFilePacket)o;
						
						File file = addRoot(packet.file);
						
						say("Client getting: " + file);
						
						if (file.exists()){
							say(file + " exists");
							controlOut.writeObject(new MessagePacket(Messages.GET_SUCCESS));
							sendFile(getAllFiles(file));
							
							
						}
						else{
							say(file + " does not exist");
							controlOut.writeObject(new MessagePacket(Messages.File_NOT_FOUND));
						}
						
						
					}
					
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}


	}
	
	private void sendFile(List<File> files) throws IOException {
		
		//dataServer = new ServerSocket(20);
		data = dataServer.accept();
		
		for (File file : files){
			
			//File fileWithRoot = addRoot(file);
			
			say("Sending: " + file);
			controlOut.writeObject(new FilePacket(removeRoot(file)));
			
			//Setup Streams
			InputStream in = new FileInputStream(file);
			OutputStream out = data.getOutputStream();
			
			//Send File
			byte[] bytes = new byte[8192];
		    int count;
		    while ((count = in.read(bytes)) > 0) {
		        out.write(bytes, 0, count);
		    }
		    
		    say("finished sending file");
		    in.close();
		    //out.flush();
			
		}
		
		data.close();
		controlOut.writeObject(new MessagePacket(Messages.FINISH));
		
	}
	
	private List<File> getAllFiles(File f) {
		
		List<File> files = new ArrayList<File>();
		List<File> unknown = new ArrayList<File>();
		
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
		
		return files;
		
	}

	private File removeRoot(File file){
		return removeRoot(file.toString());
	}
	
	private File removeRoot(String file){
		return new File(file.substring(rootSub));
	}
	
	private File[] removeRoots(File[] files){
		
		for (int i=0; i<files.length; i++)
			files[i] = removeRoot(files[i]);
		
		return files;
	}
	
	private File addRoot(File file){
		return addRoot(file.toString());
	}
	
	private File addRoot(String file){
		return new File(root.toString() + file);
	}
	
	@SuppressWarnings("unused")
	private File[] addRoots(File[] files){
		
		for (int i=0; i<files.length; i++)
			files[i] = addRoot(files[i]);
		
		return files;
	}
	
	protected void err(Object o) {
		System.err.println(o);
		Thread.yield();
	}

	protected void say(Object o) {
		System.out.println(o);
	}

	public static void main(String[] args) {
		new FileServer();
	}

}
