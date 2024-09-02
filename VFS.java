import java.util.HashMap;
import java.util.Map;

public class VFS implements Device {
	
private static final int OpenDevices = 10;// max number of open devices is VFS

private Map<Integer, deviceNid> deviceMap = new HashMap<>(); //stores opened devices and their id
private int Nextid = 0;// tracker for next avaialable device

private static class deviceNid{
	public Device device;
	public int DevID;
	
	public deviceNid(Device device, int DevID) {
		this.device = device;
		this.DevID = DevID;
	}
}

private Device getDevice(String name) {
	//returns corresponding device based on device name
	switch(name.toLowerCase()) {
	case "random":
		return new RandomDevice();
	case "file":
		return new FakeFileSystem();
	default:
		System.err.println("Unknown Device");
		return null;
	
	}
}



@Override
public int open(String s) {
	if(Nextid >= OpenDevices) { // check if max number of devices is reached
		return -1;
	}
	//split input string into name and parameters for device 
	String[] split = s.split(" ", 2); 
	String Dname = split[0];
	String Dparam;
	if(split.length > 1) {
		Dparam = split[1];
	}else {
		Dparam = " ";
	}
	
	Device Dev = getDevice(Dname); //get corresponding name based on device name
	if(Dev != null) {
		int DevID =  Dev.open(Dparam); // open with parameters
		if(DevID >= 0) {
			deviceMap.put(Nextid, new deviceNid(Dev, DevID)); // add device to device man and return id
			return Nextid;
		}
	}
	return -1;
}

@Override
public void close(int id) {
	deviceNid oo = deviceMap.remove(id);// retrieve the device and id associated with device id
	if(oo != null) {
		oo.device.close(oo.DevID);// close device
	}
	
}

@Override
public byte[] read(int id, int size) {
	deviceNid oo = deviceMap.get(id); // retrieve the device and id associated with device id
	if(oo != null) {
		return oo.device.read(oo.DevID, size); // read on associated device
	}else {
		return new byte[0];
	}
}

@Override
public int write(int id, byte[] data) {
	deviceNid oo = deviceMap.get(id); // retrieve the device and id associated with device id
	if(oo != null) {
		return oo.device.write(oo.DevID, data);// write on associated device
	}else {
		return 0;
	}
}

@Override
public void seek(int id, int to) {
deviceNid oo = deviceMap.get(id); // retrieve the device and id associated with device id
if(oo != null) {
	oo.device.seek(oo.DevID,to);// perform seek on associated device
}
}





}
