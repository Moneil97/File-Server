import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class FileBrowsingClient {

	String currentDir = "";//"FileServer Database\\";
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
								
								System.out.print(currentDir + ">");
								
								
							}
							if (o instanceof MessagePacket){
								MessagePacket packet = (MessagePacket)o;
								
								if (packet.message == Messages.CD_SUCCESS){
									currentDir = newDir;
									System.out.print(currentDir + ">");
								}
								else if (packet.message == Messages.CD_FAIL){
									System.err.println("failed to change Dir");
									System.out.print(currentDir + ">");
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
				//say(next);
				
				if (next.startsWith("list"))
					out.writeObject(new RequestListPacket(currentDir));
				else if (next.startsWith("cd")){
					newDir = currentDir + next.substring("cd".length()).trim() + "\\";
					out.writeObject(new CDPacket(newDir));
				}
				else{
					say("Unknown command");
					System.out.print(currentDir + ">");
				}
				
			}

			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	protected void say(Object o) {
		System.out.println(o);
	}

	public static void main(String[] args) {
		new FileBrowsingClient();
	}

}
