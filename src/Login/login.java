package Login;

import com.intel.crypto.Random;
import com.intel.util.DebugPrint;
import com.intel.util.FlashStorage;

public class login {
	public int getNum(byte[] byteNumbers) {
		int value = 0;
		for (int i = byteNumbers.length - 1; i >= 0; i--) {
		    value = (value << 8) + (byteNumbers[i] & 0xFF);
		}
		return value;
	}
	
	public login(){}
	private static boolean _loggedIn = false;
	public boolean getLoggedIn() {
		return _loggedIn;
	}
	
	// Function to handle REGISTER_CMD
	public boolean handleRegisterCmd(byte[] request) {
		int flashDataSize = FlashStorage.getFlashDataSize(0);
	
	    if (flashDataSize != 0) {
	        //final byte[] myResponse = { 'U', 'S', 'E', 'R' , ' ' , 'A', 'L' , 'R', 'E', 'D', 'Y', ' ', 'E', 'X', 'I', 'S', 'T', 'S' };
	        return false;
	    } else {
	        FlashStorage.writeFlashData(0, request, 0, request.length);
	        //final byte[] myResponse = { 'S', 'U', 'C', 'C' , 'E' , 'S', 'S'};
	        return true;
	    }
	}

	// Function to handle LOGIN_CMD
	public boolean handleLoginCmd(byte[] request) {
		int flashDataSize = FlashStorage.getFlashDataSize(0);
		if (flashDataSize == 0) {
	        final byte[] myResponse = { 'N', 'O', ' ', 'U' , 'S' , 'E', 'R' , ' ', 'E', 'X', 'I', 'S', 'T', 'S' };
	        return false;
	    } else {
	        byte[] savedPassword = new byte[flashDataSize];
	        FlashStorage.readFlashData(0, savedPassword, 0);
	        DebugPrint.printBuffer(request);
	        DebugPrint.printBuffer(savedPassword);
	        
	        String req = new String(request);
	        String savedPass = new String(savedPassword);
	        
	        if (req.equals(savedPass)) {
	            _loggedIn = true;
	            final byte[] myResponse = { 'L', 'O', 'G', 'I' , 'N' , ' ', 'S' , 'U', 'C', 'C', 'E', 'S', 'S'};
	            return true;
	        } else {
	            final byte[] myResponse = { 'W', 'R', 'O', 'N' , 'G' , ' ', 'P' , 'A', 'S', 'S', 'W', 'O', 'R', 'D'};
	            return false;
	        }
	    }
	}

	public void logout() {
		_loggedIn = false;
	}

	// Function to handle RESET_PASSWORD_CMD
	public byte[] handleResetPasswordCmd(byte[] request) {
	    if (_loggedIn) {
	        FlashStorage.eraseFlashData(0);
	        FlashStorage.writeFlashData(0, request, 0, request.length);
	        byte[] myResponse = { 'S', 'U', 'C', 'C' , 'E' , 'S', 'S'};
	        return myResponse;
	    }
	    else {
	    	byte[] myResponse = { 'L', 'O', 'G', 'I' , 'N' , ' ', 'F' , 'O', 'R', ' ', 'U', 'S', 'E'};
	    	return myResponse;
	    }
	}
}
