import java.util.concurrent.Semaphore;

public abstract class UserlandProcess implements Runnable {

	private static Thread thread; // process Thread
	private Semaphore semaphore; // Semaphore for synchronization
	private boolean QExpired; // Stop request Flag
	private OS.Priority priority;

	public UserlandProcess() {
		thread = new Thread(this);
		this.semaphore = new Semaphore(1); // intialize 1 permit to semaphore
		this.QExpired = false;
		this.setPriority(priority);
	}
	
	

	public void requestStop() { // requests stopping process
		QExpired = true;
	}

	public abstract void main(); // represents main functionality of process

	public void run() { // runnable run method
		try {
			semaphore.acquire(); // get sempahore
		} catch (Exception e) {
		}
		main(); // call main abstract void method
		semaphore.release();// release semaphore after execution
	}

	public boolean isStopped() {
		return semaphore.availablePermits() == 0; // checks that process stopped
	}

	public boolean isDone() {
		return !getThread().isAlive(); // check if process is done (Thread not alive)
	}

	public void start() { // start process
		semaphore.release();
		getThread().start(); // start thread
	}

	public void stop() { // acquire semaphore to stop process
		try {
			semaphore.acquire();
		} catch (Exception e) {
		}
	}

	public void cooperate() {
		if (QExpired) {
			QExpired = false;
			OS.switchProcess(); // switch process
		}
		semaphore.release();
	}

	public static Thread getThread() {
		return thread;
	}



	public OS.Priority getPriority() {
		return priority;
	}



	public void setPriority(OS.Priority priority) {
		this.priority = priority;
	}

}
