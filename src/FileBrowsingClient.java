import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class FileBrowsingClient {

	String currentDir = "";
	String newDir;
	
	@SuppressWarnings({ "resource"})
	public FileBrowsingClient() {

		try {
			Socket control = new Socket("0", 21);
			/*Socket data =*/ new Socket("0", 20);
			ObjectOutputStream out = new ObjectOutputStream(control.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(control.getInputStream());
			
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					while (true){
						try {
							
							Object o = in.readObject();
							
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
									printHeader();
								}
								else if (packet.message == Messages.CD_FAIL){
									System.err.println("failed to change Dir");
									printHeader();
								}
								
							}
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					
				}
			}).start();
			
			
			Scanner input = new Scanner(System.in);
			
			System.out.print(">");
			
			while (input.hasNextLine()){
				String next = input.nextLine();
				
				if (next.startsWith("list"))
					out.writeObject(new RequestListPacket(currentDir));
				else if (next.startsWith("cd")){
					
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
					
					out.writeObject(new CDPacket(newDir));
					
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

	protected void say(Object o) {
		System.out.println(o);
	}

	public static void main(String[] args) {
		new FileBrowsingClient();
	}

}
