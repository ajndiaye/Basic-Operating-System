
public class Main {

	public static void main(String[] args) {

		OS.Startup(new HelloWorld());// initialize OS and start helloworld process

		OS.CreateProcess(new GoodbyeWorld()); // crreate new process and add it to OS

	}
}
