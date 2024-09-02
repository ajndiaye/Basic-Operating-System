
import java.util.ArrayList;


public class OS {

	static Kernel k; // one and only kernel instance
	public static ArrayList<Object> param = new ArrayList<>(); // system call parameters
	public static CallType currentC; // current system call type
	public static Object RValue; // return value of system call

	public enum CallType { // enum for system call types
		Create_Process, Switch_Process, Sleep
	}
	
	public enum Priority{
		RealTime,Interactive,Background
		
	}


	public static int CreateProcess(UserlandProcess up, Priority priority) {
		param.clear(); // clear parameters
		param.add(up); // add new process
		param.add(priority);
		currentC = CallType.Create_Process; // set to current system call
		switchToKernel(); // switch to kernel
		return (int) RValue; // return result of system call

	}
	
	public static int CreateProcess(UserlandProcess up) {
		return CreateProcess(up, Priority.Interactive);
	}
	
	
	
	public static void Sleep(int millisecs) {
		param.clear(); // clear parameters
		param.add(millisecs); // add new process
		currentC = CallType.Sleep; // set to current system call
		switchToKernel(); // switch to kernel
	}

	public static void Startup(UserlandProcess init) {
		k = new Kernel(); // initialize kernel
		CreateProcess(init); // create initial userland process
		CreateProcess(new IdleProcess()); // create idleprocess

	}

	public static void switchProcess() {
		Scheduler scheduler = new Scheduler();// initialize scheduler
		scheduler.SwitchProcess(); // switch to next process
	}

	public static void switchToKernel() {
		Scheduler scheduler = new Scheduler(); // initialize scheduler
		getK().start(); // start kennel
		getK().getSema().release(); // release semaphore
		while (true) {
			try {
				getK().getSema().acquire(); // wait for acquire semaphore
				break;
			} catch (Exception e) {
			}
		}
		UserlandProcess currentp = scheduler.currentProcess;// get current process
		if (currentp != null) {
			if (currentp.isDone()) {
				// current process is done, so nothing is done here
			} else {
				currentp.requestStop(); // request to stop current process
			}
		} else {// else create and start new idle process
			currentp = new IdleProcess();
			currentp.start();
		}

		switch (currentC) {
		case Create_Process: // if current call is create process
			RValue = scheduler.CreateProcess((UserlandProcess) param.get(0)); // create new process
			break; // break loop
		case Switch_Process:
			scheduler.SwitchProcess();
			break;
		case Sleep:
			 scheduler.Sleep((int)param.get(0));
			
		default:
			throw new UnsupportedOperationException("OS problems!");// unknown system call error

		}
		getK().getSema().release();// release semaphore

	}

	public static Kernel getK() {
		return k;
	}

}
