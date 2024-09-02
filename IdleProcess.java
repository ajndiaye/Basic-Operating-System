
public class IdleProcess extends UserlandProcess {

	@Override
	public void main() {
		while (true) { // inifite loop
			cooperate(); // cooperate ti allow switching of processes
			try {
				Thread.sleep(50);// sleep for 50 ms
			} catch (Exception e) {
			}
		}

	}

}
