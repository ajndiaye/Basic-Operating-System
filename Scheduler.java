
import java.time.Clock;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;




public class Scheduler {

	private Clock clock = Clock.systemUTC();
	private LinkedList<PCB> sleepProcess;
	private LinkedList<UserlandProcess> processes; // list of process.
	private Timer timer; // instance of the timer.
	public UserlandProcess currentProcess; // current process
	private Random random = new Random();
	private Kernel kernel;

	public Scheduler() {
		this.sleepProcess = new LinkedList<>();
		this.timer = new Timer(); // initialize timer
		this.setProcesses(new LinkedList<>());// set the list of processes
		this.currentProcess = null;
		this.kernel = new Kernel();
		timer.schedule(new TimerTask() { // the task will be done every 250 milliseconds
			public void run() {
				synchronized (this) { // synchronize run
					if (currentProcess != null) {
						currentProcess.requestStop(); // Stop the running process
					}
				}
			}
		}, 0, 250); // initial delay is zero and repeats after every quarter of a second.

	}
	
	private OS.Priority getRandomPriority() {
		OS.Priority[] p = OS.Priority.values();
		return p[random.nextInt(p.length)]; // return random priority
	}

	public LinkedList<UserlandProcess> getProcesse() {
		return processes; // return processes linked list

	}
	
	public UserlandProcess getCurrentlyRunning() {
		return currentProcess;
	}

	public void SwitchProcess() {
		if (currentProcess != null && !currentProcess.isDone()) { // check is current process that not done
			Kernel.GetQ(currentProcess.getPriority()).add(currentProcess); // re-insert current process to list.
			currentProcess.stop(); // process n use of resources
		}
		OS.Priority next = getRandomPriority(); // get random prority for next process
		currentProcess = Kernel.GetQ(next).poll(); // get the next process from specified queue
		
		if(currentProcess != null) { // start next process if not null
			currentProcess.start();
		}
		demoteProcess(); // demote process based on priority
		
		if(currentProcess != null && currentProcess.isDone()) {
			for(int i = 0; i < Kernel.findkp(currentProcess).getHandles().length; i++) {
				if(Kernel.findkp(currentProcess).getHandles()[i] != -1)
				kernel.close(i);
			}
		}
	}
	

	private void demoteProcess() {
		for(int i = 0; i < Kernel.getQlength() - 1; i++) {
			OS.Priority Pcurrent = OS.Priority.values()[i]; //get current priority, (should be Realtime)
			OS.Priority Pnext = OS.Priority.values()[i + 1];//get next priority (should be interactive)
			if(!Kernel.GetQ(Pcurrent).isEmpty() && Kernel.GetQ(Pnext).size() < 2) { 
				UserlandProcess process = Kernel.GetQ(Pcurrent).poll();// remove a process from currentpriority Q
				Kernel.GetQ(Pnext).add(process);// add the process to nextprority Q
				process.setPriority(Pnext); // set the priority of the process to next priority
			}
		}
		
	}

	void setProcesses(LinkedList<UserlandProcess> processes) {
		this.processes = processes; // setter for process linked list
	}
	
	public int CreateProcess(UserlandProcess up, OS.Priority priority) {
		Kernel.GetQ(priority).add(up);
		if(currentProcess == null || currentProcess.isDone()) {
			SwitchProcess();
		}
		return Kernel.GetQ(priority).size()-1;
	}

	public int CreateProcess(UserlandProcess up) {
		getProcesse().add(up); // add new process to list of processes.

		if (currentProcess == null || currentProcess.isDone()) {
			SwitchProcess(); // if there is no current process or its done switch to new one.
		}

		return getProcesse().size() - 1; // return index of the next process in list.
	}

	public void Sleep(int ms) {
		long WakeUp = clock.millis() + ms; //calcualte sleep time
		
		PCB pcb = new PCB(currentProcess); // create PCB for current process
		pcb.getU().requestStop(); // request userlandprocess to stop
		sleepProcess.add(pcb); // add PCB sleep process to sleep process queue

		currentProcess = null; //set current process to null and switch to next process
		SwitchProcess();
		
		while (true) { //loop
			long cTime = clock.millis();//get current time
			if(cTime >= WakeUp) {  //check if current time has reahced wake up timr
				for(int i = 0; i < sleepProcess.size(); i++) {
					PCB sleeping = sleepProcess.get(i); // iterate through sleep process queue and get the PCB representing a sleeping process
					if(sleeping.getU() == currentProcess) {
						processes.add(currentProcess);  // add current process back to list of processes
						currentProcess = sleeping.getU();
						sleepProcess.remove(sleeping); // remove sleeping process from sleeping process queue
						currentProcess.start();// start current process
						break; // break loop
					}
				}
				break;
			}
			try {
				Thread.sleep(ms);// 50 ms delay
			} catch (Exception e) {break; }
		}
}
	
	public static void resetScheduler() { //helper method for testing (not relevant to scheduler)
		for(int i =0; i < Kernel.getQlength(); i++) {
			Kernel.Q[i].clear();
		}
	}
	
	
	public static UserlandProcess getProcessByIndex(int index) { //helper method for testing (not relevant to scheduler)
		for(int i = 0; i < Kernel.getQlength(); i++) {
			LinkedList<UserlandProcess> queue  = Kernel.Q[i];
			for(int k =0; k < queue.size(); k++) {
				UserlandProcess process = queue.get(k);
				if(process.hashCode() == index) {
					return process;
				}
			}
		}
		return null;
	}
}
