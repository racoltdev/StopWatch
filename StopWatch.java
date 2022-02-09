import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

//Simple stopwatch program to clock my work
public class StopWatch {
	
	private static final Scanner sysIn = new Scanner(System.in);

	public static void main(String[] args) {

		ArrayList<String> jobs = getJobs();
		generalPrompt(jobs);
		
		sysIn.close();
	}
	
	public static ArrayList<String> getJobs() {
		ArrayList<String> jobs = new ArrayList<String>();
		
		File jobsDir= new File(System.getProperty("user.dir"));
		
		File[] files = jobsDir.listFiles((File path) -> path.getName().endsWith(".txt"));
		for (File file : files) {
			jobs.add(file.getName().substring(0, file.getName().length() - 4));
		}

		return jobs;
	}
	
	private static void generalPrompt(ArrayList<String> jobs) {
		
		if (jobs.isEmpty()) {
			System.out.println("There are no active jobs. Use \"(c)reate\" to create a new job");
		}
		else {
			System.out.println("Active jobs:" + jobs);
		}
		
		System.out.println("General Menu:");
		
		String input = "";
		while (true) {
			System.out.println("(c)reate, (r)emove, (u)se {job name}, (e)xit");
			
			input = sysIn.nextLine().toLowerCase();
			
			if (input.charAt(0) == 'e') {
				System.out.println("Goodbye");
				sysIn.close();
				System.exit(0);
			}
			else if (input.charAt(0) == 'u') {
				manageJobPrompt(input, jobs);
			}
			else if (input.charAt(0) == 'c') {
				createJob(input, jobs);
				jobs = getJobs();
			}
			else if (input.charAt(0) == 'r') {
				removeJob(input, jobs);
				jobs = getJobs();
			}
			else {
				System.out.println("Invalid option, try again");
			}
		}
	}
	
	public static void removeJob(String input, ArrayList<String> jobs) {
		String job = "";
		
		while (job.isEmpty() || !jobs.contains(job)) {
			job = verifyJobName(input, jobs);
			if (job == null) {
				return;
			}
		}
		
		File jobFile = new File(job + ".txt");
		jobFile.delete();

		System.out.println("Removed job successfully");
	}
	
	public static void createJob(String input, ArrayList<String> jobs) {
		String job = "";
		
		if (!input.contains(" ")) {
			System.out.println("Enter the name of the job you wish to manage");
			job = sysIn.nextLine();
		}
		else {
			job = input.split(" ")[1];
		}
		
		while (job.isEmpty() || jobs.contains(job)) {
			
			if (jobs.contains(job)) {
				System.out.println("Cannot create job, already exists. Try again");
			}
			
			job = sysIn.nextLine().toLowerCase();
		}
		
		File jobFile = new File(job + ".txt");
		try {
			jobFile.createNewFile();
			System.out.println("Created job successfully");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void manageJobPrompt(String input, ArrayList<String> jobs) {
		
		String job = verifyJobName(input, jobs);
		if (job == null) {
			return;
		}
		
		boolean flag = false;
		//Scanner in = new Scanner(System.in);
		System.out.println("Manage Job "+job+": ");
		
		while (!flag) {
			System.out.println("(c)lear time, (t)otal time, (s)tart clock, (e)xit");
			input = sysIn.nextLine();
			char inputChar = input.charAt(0);
			
			if (inputChar == 'e') {
				flag = true;
			}
			else {
				File jobFile = new File(job +".txt");
				if (inputChar == 't') {
					getTotalTime(jobFile);
				}
				else if (inputChar == 'c') {
					clearTime(jobFile);
				}
				else if (inputChar == 's') {
					runClock(jobFile);
				}
				else {
					System.out.println("Invalid option. Try again");
				}
			}
			
		}
		//in.close();
		return;
	}
	
	public static void runClock(File job) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy 'at' HH:mm:ss '\t'");
		
		Date startDate = new Date(System.currentTimeMillis());
		logTime(startDate, job, formatter);

		Date stopDate;
		//Scanner in = new Scanner(System.in);
		
		System.out.println("Type \"Stop\" to stop clocking time for " + job.getName());
		
		while(!sysIn.nextLine().toLowerCase().equals("stop")) {
		}
		
		stopDate = new Date(System.currentTimeMillis());
		logTime(stopDate, job, formatter);
		
		SimpleDateFormat elapsedFormatter = new SimpleDateFormat("HH:mm:ss '\n'");
	    long elapsedTime = stopDate.getTime() - startDate.getTime();
	    Date elapsedDate = new Date(elapsedTime);
	    
		logTime(elapsedDate, job, elapsedFormatter);
		System.out.println("Logged time successfully");
		
		//in.close();
		
		return;
	}
	
	public static void logTime(Date date, File job, SimpleDateFormat formatter) {
		try {
	    	FileWriter f = new FileWriter(job, true);
	    	f.write(formatter.format(date));
	    	f.close();
	    } catch (Exception e) {
	    	
	    	System.out.println("An error occured. Time not logged successfully");
	    }
	}
	
	public static void clearTime(File job) {
		try {
			Files.newBufferedWriter(Path.of(job.getPath()), StandardOpenOption.TRUNCATE_EXISTING);
			System.out.println("Cleared logs for " + job.getName() + " successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Determines if given job is valid and prompts user until valid job is given
	private static String verifyJobName(String input, ArrayList<String> jobs) {

		String job;
		//Scanner in = new Scanner(System.in);
		
		//Determine if job was already given and identify job
		if (!input.contains(" ")) {
			System.out.println("Enter the name of the job you wish to manage");
			job = sysIn.nextLine();
		}
		else {
			job = input.split(" ")[1];
		}
		
		job = job.toLowerCase();
		
		//Loop until valid input is given
		while(!jobs.contains(job) && !job.equals("e")) {
			System.out.println("Invalid job name: try again, or (e)xit");
			job = sysIn.nextLine();
		}
		
		//in.close();
		
		if (job.equals("e")) {
			return null;
		}
		else {
			return job;
		}
	}
	
	public static void getTotalTime(File job) {
		
		try {
			Scanner in = new Scanner(job);
			
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			long timeTotal = 0;
			
			while (in.hasNextLine()) {
				
				String str = in.nextLine();
				String[] times = str.split("\t");
				
				if (times.length != 3) {
					System.out.println("Invalid time data. Ignoring");
				}
				else {
					long time = Long.parseLong(times[2]);
					timeTotal += time;
				}
				
			}
			
			in.close();
			
			Date total = new Date(timeTotal);
			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			System.out.println(formatter.format(total));
			
		} catch (FileNotFoundException e) {

			System.out.println("Failed to add time");
		}
	}
}