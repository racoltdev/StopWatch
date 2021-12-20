import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

//Simple stopwatch program to clock my work
public class stopWatch {

	public static void main(String[] args) {

		String[] jobs = {"landon", "skryl"};
		boolean flag = false;
		boolean addFlag = false;
		boolean delFlag = false;
		long startTime = 0;
		long stopTime = 0;
		
		Scanner in = new Scanner(System.in);
		
		System.out.println("Which job would you like to start");
		String input = in.nextLine();
		
		String[] temp = new String[2];
		
		if (input.contains(" ")) {
			
			temp = input.split(" ");
			
			if (temp[0].equals("add")) {
				
				addFlag = true;
				input = temp[1];
				System.out.println(input);
			}
			else if (temp[0].equals("del")) {
				
				delFlag = true;
				input = temp[1];
			}
			else {
				
				System.out.println("error1");
				in.close();
				return;
			}
		}		
		
		for (String job: jobs) {
			
			if (job.toLowerCase().equals(input)) {
				
				flag = true;
				break;
			}
		}
		
		if (addFlag && flag) {
			
			addShit(input);
			in.close();
			System.out.println("done");
			return;
		}
		else if (delFlag && flag) {
			
			File f = new File(input + ".txt");
			f.delete();

			System.out.println("Cleared logs for " + input + " successfully");
			in.close();
			return;
			
		}
		else if (flag) {
			
			startTime = System.currentTimeMillis();
			
			do {
				stopTime = stopLoop(input, in);
			} while (stopTime == 0);
			
			in.close();
		}
		else {
			in.close();
			System.out.println("error2");
			return;
		}

	    
	    long elapsedTime = stopTime - startTime;
	    
	    int seconds = (int) (elapsedTime / 1000) % 60 ;
	    int minutes = (int) ((elapsedTime / (1000*60)) % 60);
	    int hours   = (int) ((elapsedTime / (1000*60*60)) % 24);
	    
	    String timeLogged = hours + ":" + minutes + ":" + seconds;
	    System.out.println(timeLogged);
	    
	    try {
	    	FileWriter f = new FileWriter(input + ".txt", true);
	    	f.write(timeLogged + "\n");
	    	f.close();
	    	
	    	System.out.println("Logged successfully");
	    } catch (Exception e) {
	    	
	    	System.out.println("An error occured. Time not logged successfully");
	    }
	}
	
	public static long stopLoop(String input, Scanner in) {
		
		System.out.println("Type \"Stop\" to stop clocking time for " + input);
		
		String stop = in.nextLine();
		
		if (stop.toLowerCase().equals("stop")) {
			
			long stopTime = System.currentTimeMillis();
			
			return stopTime;
		}
		else return 0;
	}
	
	public static void addShit(String input) {
		
		try {
			Scanner in = new Scanner(new File(input + ".txt"));
			
			long hours = 0;
			long minutes = 0;
			long seconds = 0;
			
			while (in.hasNextLine()) {
				
				String str = in.nextLine();
				
				String[] tempList = str.split(":");
				hours += Long.parseLong(tempList[0]);
				minutes += Long.parseLong(tempList[1]);
				seconds += Long.parseLong(tempList[2]);
			}
			
			if (seconds >= 60) {
				
				long tempNum = seconds % 60;
				minutes += (seconds - tempNum) / 60;
				seconds %= 60;
			}
			
			if (minutes >= 60) {
				
				long tempNum = minutes % 60;
				hours += (minutes - tempNum) / 60;
				minutes %= 60;
			}
			
			System.out.println("Total time worked for " + input);
			System.out.println(hours + ":" + minutes + ":" + seconds);
			
		} catch (FileNotFoundException e) {

			System.out.println("Failed to add shit");
		}
	}
}
