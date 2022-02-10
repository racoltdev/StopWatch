import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import java.util.Scanner;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileWriter;
import java.nio.file.StandardOpenOption;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

/**
 * Simple stopwatch program to track my work
 * @author racolt: racoltdev@gmail.com
 */
public class StopWatch {
	
	private static final Scanner sysIn = new Scanner(System.in);

	public static void main(String[] args) {

		ArrayList<String> jobs = getJobs();
		generalPrompt(jobs);
		
		sysIn.close();
	}
	
	/**
	 * Updates the active jobs in the working directory
	 * @return	List of active jobs
	 */
	public static ArrayList<String> getJobs() {
		ArrayList<String> jobs = new ArrayList<String>();
		
		File jobsDir = new File(System.getProperty("user.dir"));
		
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
		
		String input = "";
		while (true) {
			System.out.println("\nActive jobs:" + jobs);
			System.out.println("General Menu:\n(c)reate {job name}, (r)emove {job name}, (u)se {job name}, (e)xit");
			
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
	
	/**
	 * Removes given job from the working directory
	 * @param input : Job name
	 * @param jobs : List of active jobs
	 */
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
	
	/**
	 * Creates a job in the working directory
	 * @param input : Job name
	 * @param jobs : List of active jobs
	 */
	public static void createJob(String input, ArrayList<String> jobs) {
		String job = "";
		
		if (!input.contains(" ")) {
			System.out.println("Enter the name of the job you wish to create");
			job = sysIn.nextLine();
		}
		else {
			job = input.split(" ")[1];
		}
		
		job = job.toLowerCase();
		
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
		return;
	}
	
	/**
	 * Starts the clock for a given job and awaits stop command
	 * @param job: File object of job
	 */
	public static void runClock(File job) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy 'at' HH:mm:ss '\t'");
		
		Date startDate = new Date(System.currentTimeMillis());
		logTime(startDate, job, formatter);

		Date stopDate;		
		System.out.println("Type \"Stop\" to stop clocking time for " + job.getName());
		
		while(!sysIn.nextLine().toLowerCase().equals("stop")) {
		}
		
		stopDate = new Date(System.currentTimeMillis());
		logTime(stopDate, job, formatter);
		
		SimpleDateFormat elapsedFormatter = new SimpleDateFormat("HH:mm:ss'\n'");
		elapsedFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	    long elapsedTime = stopDate.getTime() - startDate.getTime();
	    Date elapsedDate = new Date(elapsedTime);
	    
		logTime(elapsedDate, job, elapsedFormatter);
		System.out.println("Logged time successfully");
		
		return;
	}
	
	/**
	 * Logs time according to given time format in the job file
	 * @param date : Time to be logged
	 * @param job : Job file
	 * @param formatter : time format data will be logged in
	 */
	public static void logTime(Date date, File job, SimpleDateFormat formatter) {
		try {
	    	FileWriter f = new FileWriter(job, true);
	    	f.write(formatter.format(date));
	    	f.close();
	    } catch (Exception e) {
	    	
	    	System.out.println("An error occured. Time not logged successfully");
	    }
	}
	
	/**
	 * Clears time for given job
	 * @param job : Job file
	 */
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
			job = sysIn.nextLine().toLowerCase();
		}
		
		if (job.equals("e")) {
			return null;
		}
		else {
			return job;
		}
	}
	
	/**
	 * Totals all worked time for a given job
	 * @param job : Job file
	 */
	public static void getTotalTime(File job) {
		
		try {
			Scanner in = new Scanner(job);
			
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			long timeTotal = 0;
			
			while (in.hasNextLine()) {
				
				String str = in.nextLine();
				String[] times = str.split("\t");
				
				if (times.length != 3) {
					System.out.println("Invalid time data. Ignoring");
				}
				else {
					long time = formatter.parse(times[2]).getTime();
					timeTotal += time;
				}
				
			}
			
			in.close();
			
			Date total = new Date(timeTotal);
			System.out.println(formatter.format(total));
			
		} catch (FileNotFoundException | ParseException e) {
			e.printStackTrace();
			System.out.println("Failed to add time");
		}
	}
}