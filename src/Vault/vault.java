package Vault;
import com.intel.util.FlashStorage;

public class vault {
	
	
	/* Function to get slice of a primitive array in Java
	 * helper method taken from geeksforgeeks
	 */
    public static byte[] getSliceOfArray(byte[] arr,
                                        int start, int end)
    {
 
        // Get the slice of the Array
    	
        byte[] slice = new byte[end - start+1];
        
 
        // Copy elements of arr to slice
        for (int i = 0; i < slice.length; i++) {
            slice[i] = arr[start + i];
        }
 
        // return the slice
        return slice;
    }
	/**
	 * function to add a [website, username, pswd] to the file which stores this information
	 * @param info
	 * @return
	 */
	public static boolean setPswd(byte[] info)
	{
		try {
		int storageSize = FlashStorage.getFlashDataSize(1);
		if(storageSize == 0) {
			int len = info.length;
			FlashStorage.writeFlashData(1, info, 0, info.length);
			return true;
		}
		byte[] fileContents = new byte[storageSize + 50];
		FlashStorage.readFlashData(1, fileContents, 0);
		//concatenate the new passwd to the current list of info
		System.arraycopy(info, 0, fileContents, storageSize, 50);
		//save to the flashStorage
		FlashStorage.writeFlashData(1, fileContents, 0, storageSize+50);
		return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	public static byte[] getPswd(byte[] website) {
		try {
		String site = new String(website);
		int storageSize = FlashStorage.getFlashDataSize(1);
		byte[] fileContents = new byte[storageSize];
		FlashStorage.readFlashData(1, fileContents, 0);
		for(int i = 0; i < storageSize/50; i++) {
			int start; 
			int end;
			if(i == 0) {
				start = 0;
				end = 49;
			}else {
				start = i*50;
				end = i*50+49;
			}
			
			String test = new String(getSliceOfArray(fileContents, start, start+9));
			if(site.equals(test)) { 
				return getSliceOfArray(fileContents, start, end);
			}
		}
		return "website not found".getBytes();
		}catch(Exception e) {
			return "failed".getBytes();
		}
	}
}
