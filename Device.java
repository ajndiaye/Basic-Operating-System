
public interface Device {
	
	int open(String s);
	void close(int id);
	byte[] read(int id, int size);
	int write(int id, byte[] data);
	void seek(int id, int to);
	

}
