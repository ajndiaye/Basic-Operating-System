
public class GoodbyeWorld extends UserlandProcess {

	@Override
	public void main() {// implements abstract main method in UserlandProcess
		while (true) {// infinite loop
			System.out.println("Goodbye World");// print to console
			try {
				Thread.sleep(50);// 50 ms delay
			} catch (Exception e) {}
			
			cooperate(); // cooperate to switch process
		}

	}

}
