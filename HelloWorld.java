
public class HelloWorld extends UserlandProcess {

	@Override
	public void main() {
		while (true) {
			System.out.println("Hello World!"); // print to console
			try {
				Thread.sleep(50); // 50 ms delay
			} catch (Exception e) {
			}
			cooperate(); // cooperate to switch process
		}

	}

}
