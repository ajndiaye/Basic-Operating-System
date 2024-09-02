
import java.io.RandomAccessFile;


public class FakeFileSystem implements Device{
	
	private static final int Fnum = 10;
	
	private RandomAccessFile[] files = new RandomAccessFile[Fnum]; // # of files in fake file system
	

	
	@Override
public int write(int id, byte[] data) {
	
	if(id >= 0 && id < Fnum && files[id] != null) {	 //iterate through files to find free slot
		try {
			
		     	files[id].write(data); // write data to file
		
			  return data.length; // return length of written data
		
			
	}catch(Exception e) {
	
			System.err.println("Error writing file");  //throw error
		
			return 0; // return 0
		}
	
	}else {
		return 0; // return 0
	}
	}

	
	@Override
public void seek(int id, int to) {
	
	if(id >= 0 && id < Fnum && files[id] != null) { //iterate through files to find free slot
		
		try {
		files[id].seek(to);// seek to specified position in file
	
		}catch (Exception e) {		System.err.println("Error seeking file"); //throw error
		} 
	}
	}
	


	
	@Override
	public byte[] read(int id, int size) {
		
		if(id >= 0 && id < Fnum && files[id] != null) { //iterate through files to find free slot
			
			byte [] data = new byte[size];// create new byte array to store data
		try {
				
				 int Read = files[id].read(data);// read data from file
			if(Read < size) {
		byte[]result = new byte[Read];
					
				System.arraycopy(data, 0, result, 0, Read);// if read sixe is smaller than requested, create new array
					
					return result;
	 	} else { return data; }// return read data
			  }catch (Exception e) { 
			      	System.err.println("Error reading file"); //throw error
		
	return new byte[0]; // return empty byte array
		
			}
		   }else { return new byte[0]; } // return empty byte array
	}

	
	@Override
	public void close(int id) {
if(id >= 0 && id < Fnum && files[id] != null) { //iterate through files to find free slot
		 try {
		
	     files[id].close();// close the file
			
			  files[id]= null; // set file slot to null
	     	}catch (Exception e) {
		System.err.println("Error closing file");} //throw error
	}
		
	}
	
	@Override
	public int open(String s) {
		if(s == null || s.isEmpty()) { // check if file name is empty
			throw new RuntimeException("file name null"); // throw exception
		}
		for(int i = 0; i<Fnum; i++) {//iterate through files to find free slot
			
		if(files[i]== null) {
			try {
				
			files[i] = new RandomAccessFile( s, "rw'");// create new random access file for name
					
				return i;// return specific file index
		} catch (Exception e) {
				
			System.err.println("Error opening file"); //throw error
					return -1; }
		}
		}
		return -1;
	}
}

