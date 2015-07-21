import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {

	ServerSocket controlServer, dataServer;
	Socket control, data;
	
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
				data = dataServer.accept();
				
				say("connected to: " + control);
				say("connected to: " + data);
				
				//Setup Streams
				ObjectInputStream controlIn = new ObjectInputStream(control.getInputStream());
				InputStream dataIn = data.getInputStream();
				FileOutputStream dataOut;
				
				//while connected
				while (true){
					
					//Get packet from Client
					Object o = controlIn.readObject();
					
					if (o instanceof FilePacket){
						FilePacket packet = (FilePacket)o;
						
						if (packet.message == Messages.INCOMING_FILE){
							
							//Check for and create necessary folders
							File dest = new File("C:\\Users\\Cameron\\Desktop\\FileServer Database\\" + packet.file.getParent());
							if (!dest.exists()){
								dest.mkdirs();
								say("Folder Created: FileServer Database\\" + packet.file.getParent());
								Thread.yield();
							}
							
							//Destination File
							dest = new File("C:\\Users\\Cameron\\Desktop\\FileServer Database\\" + packet.file);
							
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

	protected void say(Object o) {
		System.out.println(o);
	}

	public static void main(String[] args) {
		new FileServer();
	}

}
