import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;


public class Kernel implements Runnable, Device {

	private Thread thread;
	private Semaphore sema;
	private Scheduler sched;
	private VFS vfs = new VFS();
	static LinkedList<UserlandProcess>[] Q;

	public Kernel() {
		
		this.thread = new Thread(); // initialize thread
		sema = new Semaphore(0); // initialize semaphore
		sched = new Scheduler(); // initialize sxheduler
		thread.start(); // start thread
		Q = new LinkedList[OS.Priority.values().length];
		for (int i =0; i < Q.length; i++) {
			Q[i] = new LinkedList<>();
		}
		
	}
	
	public class Kerneland {
		private int[] handles = new int[10];// kerneland process array
		
		public Kerneland() {
			Arrays.fill( getHandles(), -1); // initialize array
		}

		public int[] getHandles() {
			return handles;
		}
	}
	
	private static ArrayList<Kerneland> KernelandProcess = new ArrayList<>();
	
	public static Kerneland findkp(UserlandProcess p) {
		for (int i = 0; i< Q.length; i++) { // iterate through scheduling q 
			LinkedList<UserlandProcess> line = Q[i]; // get list representing current queue
			if(line.contains(p)) {
				int Lineindex = line.indexOf(p);// get index of userlandprocess in queue
				return KernelandProcess.get(Lineindex);// return correspondind kerneland process
			} 
		}
		return null;
	}
	
	public static int getQlength() {
		return Q.length;
	}

	public static LinkedList<UserlandProcess> GetQ(OS.Priority priority){
		return Q[priority.ordinal()];
	}

	public void run() {
		do {
			try {
				getSema().acquire(); // wait for acquiring semaphore.
			} catch (Exception e) {
			} // catch exceptions

			switch (OS.currentC) {
			case Create_Process:

				sched.CreateProcess(sched.currentProcess); // in this case create a new process in the scheduler
				OS.RValue = sched.getProcesse().size() - 1; // set value to next process in thescheduler
				break;

			case Switch_Process:
				sched.SwitchProcess(); // in this case change process
				break;
			case Sleep:
				sched.Sleep((int)OS.param.get(0));
			default:
				throw new UnsupportedOperationException("kernel issue");
			}
			if (sched.currentProcess == null) { // if the current calling is taken away
				sched.SwitchProcess(); // switch process
			} else {
				if (sched.currentProcess != null) {
					sched.currentProcess.run();
				} // run current process, only when not this is null
			}
		} while (true);

	}

	public Semaphore getSema() {
		return sema; // semaphore getter
	}

	public void start() {
		getSema().release(); // releases semaphore
	}

	@Override
	public int open(String s) {
		UserlandProcess curr = sched.getCurrentlyRunning(); // get currently running process
		if(curr == null) { return -1; } // if none are running return -1
		Kerneland kp = findkp(curr);// find kerneland associated with current process
		if(kp == null ) {return -1; }
		for (int i = 0; i < kp.getHandles().length; i++) { // iterate through handles to find free slot
			if(kp.getHandles()[i]==-1) {
				int vID = vfs.open(s);//open virtual file system
				if(vID != -1) {
					kp.getHandles()[i] = vID;// update kerneland handles if file opens successfully
					return i; //return handle index
				}else {
					return -1;
				}
			}
		}
		return -1;
	}

	@Override
	public void close(int id) {
	UserlandProcess curr = sched.getCurrentlyRunning(); // get currently running process
	if(curr == null) {
		return; // if none are running return nothing
	}
	Kerneland kp = findkp(curr); // find kerneland associated with current process
	if(kp==null) {return;}
	if(id >=0 && id < kp.getHandles().length && kp.getHandles()[id] != 1) { // iterate through handles to find free slot
		vfs.close(kp.getHandles()[id]); // close file in VFS and mark handle as closed
		kp.getHandles()[id]= -1;
	}
		
	}

	@Override
	public byte[] read(int id, int size) {
	UserlandProcess curr = sched.getCurrentlyRunning(); // get currently running process
	if(curr == null) {
		return new byte[0]; //if none are running return -1
	}
	Kerneland kp = findkp(curr);  // find kerneland associated with current process
	if(kp==null) {return new byte[0];} 
	if(id >=0 && id < kp.getHandles().length && kp.getHandles()[id] != 1) { // iterate through handles to find free slot
		return vfs.read(kp.handles[id], size);// read data from VFS using handle and size
	}else {
		return new byte[0];// else return empty byte array
	}
	
	}

	@Override
	public int write(int id, byte[] data) {
	UserlandProcess curr = sched.getCurrentlyRunning(); // get currently running process
	if(curr == null) { // if none are running return 0
		return 0;
	}
	Kerneland kp = findkp(curr); // find kerneland associated with current process
	if(kp==null) {return 0;}
	if(id >=0 && id < kp.getHandles().length && kp.getHandles()[id] != 1) { // iterate through handles to find free slot
		return vfs.write(kp.handles[id], data); // write data to VFS using handle and data
	}else {
		return 0; // else return 0
	}
	}
	

	@Override
	public void seek(int id, int to) {
	UserlandProcess curr = sched.getCurrentlyRunning(); // get currently running process
	if(curr == null) { 
		return ; // if none are running return nothing
	}
	Kerneland kp = findkp(curr); // find kerneland associated with current process
	if(kp==null) {return;}
	if(id >=0 && id < kp.getHandles().length && kp.getHandles()[id] != 1) { // iterate through handles to find free slot
		vfs.seek(kp.handles[id], to); //seek on VFS using handle and specific position
	}
		
	}

}