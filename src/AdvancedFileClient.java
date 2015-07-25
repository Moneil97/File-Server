import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class AdvancedFileClient {

	String currentDir = "";
	String newDir;
	ObjectOutputStream controlOut;
	ObjectInputStream controlIn;
	Socket control, data;
	
	@SuppressWarnings({ "resource"})
	public AdvancedFileClient() {

		try {
			control = new Socket("0", 21);
			//data = new Socket("0", 20);
			controlOut = new ObjectOutputStream(control.getOutputStream());
			controlIn = new ObjectInputStream(control.getInputStream());
			
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					while (true){
						try {
							
							Object o = controlIn.readObject();
							
							if (o instanceof ListPacket){
								ListPacket packet = (ListPacket)o;
								
								for (File file : packet.files)
									say(file);
								
								printHeader();
								
							}
							if (o instanceof MessagePacket){
								MessagePacket packet = (MessagePacket)o;
								
								if (packet.message == Messages.CD_SUCCESS){
									currentDir = newDir;
								}
								else if (packet.message == Messages.CD_FAIL){
									err("failed to change Dir");
								}
								else if (packet.message == Messages.GET_SUCCESS){
									say("Get Starting");
									receiveFiles();
									say("Get Complete");
								}
								else if (packet.message == Messages.File_NOT_FOUND){
									err("file not found");
								}
								else{
									err("Unknown Message: " + packet.message);
								}
								
								printHeader();
								
							}
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
							System.exit(0);
						}
					}
					
					
				}
			}).start();
			
			
			Scanner input = new Scanner(System.in);
			
			System.out.print(">");
			
			while (input.hasNextLine()){
				String next = input.nextLine().trim();
				
				if (next.startsWith("list"))
					controlOut.writeObject(new RequestListPacket(currentDir));
				else if (next.startsWith("cd")){
					
					cdCommand(next);
					
				}
				else if (next.startsWith("get")){
					
					//check syntax
					
					//Parse input
					String fileToCopy = currentDir + next.substring(3).trim();
					String dest = "C:\\Users\\Cameron\\Desktop\\Test Folder\\";
					getDestRoot = dest;
					
					//send request
					controlOut.writeObject(new RequestFilePacket(fileToCopy));
					
				}
				else{
					say("Unknown command");
					printHeader();
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	String getDestRoot = "";
	
	protected void receiveFiles() throws IOException, ClassNotFoundException {
		
		data = new Socket("0", 20);
		
		FileOutputStream dataOut;
		InputStream dataIn = data.getInputStream();
		
		while (true){
			Object o = controlIn.readObject();
		
			if (o instanceof FilePacket){
				FilePacket packet = (FilePacket)o;
				
				//Check for and create necessary folders
				File dest = new File(getDestRoot + packet.file.getParent());//new File(root + packet.file.getParent());
				if (!dest.exists()){
					dest.mkdirs();
					say("Folder Created: FileServer Database\\" + packet.file.getParent());
					Thread.yield();
				}
				
				//Destination File
				dest = new File(getDestRoot + packet.file);//new File(root.toString() + packet.file.toString());
				
				dataOut = new FileOutputStream(dest);
				
				say("writing");
				//Write Client's file to HDD
				byte[] bytes = new byte[8192];
				int count;
				while ((count = dataIn.read(bytes)) > 0) {
					//say("Writing: " + bytes + " to hdd");
					dataOut.write(bytes, 0, count);
				}
				
				say("File Created: " + dest);
				dataOut.close();
			}
			else if (o instanceof MessagePacket && ((MessagePacket)o).message == Messages.FINISH){
				data.close();
				return;
			}
		
		}
		
	}

	private void cdCommand(String next) throws IOException{
		String s = next.substring("cd".length()).trim();
		
		if (s.startsWith("../")){
			
			String temp = currentDir;
			
			while (s.startsWith("../")){
				s = s.substring("../".length());
				temp = getParent(temp);
			}
			
			newDir = temp + "\\" + s;
			
		}
		else{
			newDir = currentDir + s + "\\";
		}
		
		controlOut.writeObject(new CDPacket(newDir));
	}
	
	private void printHeader(){
		if (currentDir.length() > 0)
			System.out.print(currentDir.substring(0,currentDir.length()-1) + ">");
		else
			System.out.print(currentDir + ">");
	}
	
	private String getParent(String temp) {
		say("Received: " + temp);
		
		if (temp.length() < 1)
			return "";
		
		int index = temp.substring(0,temp.length()-1).lastIndexOf("\\");
		
		if (index > 0)
			return "";
		
		return temp.substring(0, index);
	}

	protected void err(Object o) {
		System.err.println(o);
	}
	
	protected void say(Object o) {
		System.out.println(o);
	}

	public static void main(String[] args) {
		new AdvancedFileClient();
	}

}
