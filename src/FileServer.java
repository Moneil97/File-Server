import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {

	ServerSocket controlServer, dataServer;
	Socket control, data;
	final String root = "C:\\Users\\Cameron\\Desktop\\FileServer Database\\";
	int rootSub = root.toString().length();
	
	public FileServer() {
		
		say(root.toString());

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
				data = dataServer.accept();
				
				say("connected to: " + control);
				say("connected to: " + data);
				
				//Setup Streams
				ObjectInputStream controlIn = new ObjectInputStream(control.getInputStream());
				ObjectOutputStream controlOut = new ObjectOutputStream(control.getOutputStream());
				InputStream dataIn = data.getInputStream();
				FileOutputStream dataOut;
				
				//while connected
				while (true){
					
					//Get packet from Client
					Object o = controlIn.readObject();
					
					if (o instanceof RequestListPacket){
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
					else if (o instanceof FilePacket){
						FilePacket packet = (FilePacket)o;
						
						if (packet.message == Messages.INCOMING_FILE){
							
							//Check for and create necessary folders
							File dest = addRoot(packet.file.getParent());//new File(root + packet.file.getParent());
							if (!dest.exists()){
								dest.mkdirs();
								say("Folder Created: FileServer Database\\" + packet.file.getParent());
								Thread.yield();
							}
							
							//Destination File
							dest = addRoot(packet.file);//new File(root.toString() + packet.file.toString());
							
							dataOut = new FileOutputStream(dest);
							
							say("writing");
							//Write Client's file to HDD
							byte[] bytes = new byte[8192];
							int count;
							while ((count = dataIn.read(bytes)) > 0) {
								dataOut.write(bytes, 0, count);
							}
							
							say("File Created: " + dest);
							dataOut.close();

						}
						else if (packet.message == Messages.FINISH){
							
							dataIn.close();
							controlIn.close();
							data.close();
							control.close();
							break;
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}


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
	
	private File[] addRoots(File[] files){
		
		for (int i=0; i<files.length; i++)
			files[i] = addRoot(files[i]);
		
		return files;
	}

	protected void say(Object o) {
		System.out.println(o);
	}

	public static void main(String[] args) {
		new FileServer();
	}

}
