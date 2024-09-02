import java.util.Random;


public class RandomDevice implements Device{





private Random[] randomDev = new Random[10];// random device object array.




@Override

public void seek(int id, int k){

if(id>=0 && id < 10 && randomDev[id]!=null) { // it will check that device index is valid and is open.

	long skip = Math.max(0, k);// long skip =.

	randomDev[id].nextBytes(new byte[(int) skip]);//skip to byte with random sequence.

	}

}






@Override // method has no function

public int write(int id, byte[] write){

	return 0;

}


@Override

public void close(int id){

	if (id >= 0 && id < 10) { // Check if device index is valid.
	randomDev[id] = null;//set the random device slot to null.

}

}


@Override

	public int open(String seedstring) {

		for(int i = 0; i < randomDev.length; i++) { // iterate through devices to check availability of slots
			if(randomDev[i] == null) {

				if(seedstring != null && !seedstring.isEmpty()) {
					randomDev[i] = new Random(Integer.parseInt(seedstring)); //if seed value is provided.
		}else {

			randomDev[i]= new Random(); // don't create a new random device.

		}

					return i; // return index of opened arbitrary device.
			}
		}
		return -1;
	}


@Override

public byte[] read(int id, int length) {

	if (id >= 0 && id < 10 && randomDev[id] != null) { // confirm that device index is valid and is open

		byte[] data = new byte[length]; // create a byte array to hold random data.

		randomDev[id].nextBytes(data);// random bytes are created by using device.

		return data; // give back data

	}else {
		
		return new byte[0];

		}

	}



}

