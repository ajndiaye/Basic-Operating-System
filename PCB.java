

public class PCB {

	private static int nextPid = 1; // next process id variable
	
	private int pid; // process id
	private UserlandProcess u; // Associated UserlandProcess instance


	
	public PCB( UserlandProcess user) {
		this.pid = nextPid++;  //assign unique process id
		this.u = user; // Associated Userlandprocess instance

	}
	
	
	
	public int getPid() {
		return pid; // retrieve process ID
	}
	
	public void stop() {
		getU().stop(); // call stop method on the associated UserlandProcess
		
		while(!getU().isStopped()) { // wait until userland process instance is stopped
			try {
				Thread.sleep(50);// 50 ms delay
			} catch (Exception e) {}
		}
		
	}
	
	public boolean isDone() {
		return getU().isDone(); //check if userlandprocess is done
	}
	
	public void run() {
		getU().start(); //Call the start method on associated UserlandProcess
	}



	public UserlandProcess getU() {
		return u; //return associated userlandprocess
	}
}
