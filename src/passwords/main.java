package passwords;

import com.intel.util.*;
import Login.login;
import Vault.vault;
import com.intel.util.FlashStorage;
//
// Implementation of DAL Trusted Application: passwordVault 
//
// **************************************************************************************************
// NOTE:  This default Trusted Application implementation is intended for DAL API Level 7 and above
// **************************************************************************************************

public class main extends IntelApplet {
	private static final int REGISTER_CMD = 1;
	private static final int LOGIN_CMD = 2;
	private static final int SAVE_PSWD = 3;
	private static final int GET_PSWD = 4;
	private static final int LOGOUT = 5;
	private static final int DELETE = 6;
	//MAYBE MAKE A CHANGE PSWD.
	private static login login = new login();
	
	/**
	 * This method will be called by the VM when a new session is opened to the Trusted Application 
	 * and this Trusted Application instance is being created to handle the new session.
	 * This method cannot provide response data and therefore calling
	 * setResponse or setResponseCode methods from it will throw a NullPointerException.
	 * 
	 * @param	request	the input data sent to the Trusted Application during session creation
	 * 
	 * @return	APPLET_SUCCESS if the operation was processed successfully, 
	 * 		any other error status code otherwise (note that all error codes will be
	 * 		treated similarly by the VM by sending "cancel" error code to the SW application).
	 */
	public int onInit(byte[] request) {
		DebugPrint.printString("Hello, DAL!");
		return APPLET_SUCCESS;
	}
	
	/**
	 * This method will be called by the VM to handle a command sent to this
	 * Trusted Application instance.
	 * 
	 * @param	commandId	the command ID (Trusted Application specific) 
	 * @param	request		the input data for this command 
	 * @return	the return value should not be used by the applet
	 */
	public int invokeCommand(int commandId, byte[] request) {
		
		DebugPrint.printString("Received command Id: " + commandId + ".");
		if(request != null)
		{
			DebugPrint.printString("Received buffer:");
			DebugPrint.printBuffer(request);
		}
		
		
		switch(commandId) {
		case REGISTER_CMD: 
			
			if(login.handleRegisterCmd(request)) {
				setResponseCode(1);
			}else {
				setResponseCode(0);
			}
			break;
		case LOGIN_CMD: 
			if(login.handleLoginCmd(request)) {
				setResponseCode(1);
			}else {
				setResponseCode(0);
			}
			break;
		case SAVE_PSWD:
			if(login.getLoggedIn()) {
				if(vault.setPswd(request)) {
					byte[] success = "successful".getBytes();
					setResponse(success, 0, success.length);
					setResponseCode(1);
				}
				else {
					setResponseCode(0);
					setResponse("not successful".getBytes(), 0, 15);
				}
			}
			break;
		case GET_PSWD:
			if(login.getLoggedIn()) {
				byte[] info = vault.getPswd(request);
				setResponse(info, 0, info.length);
				if(info.length != 50) {
					setResponseCode(0);
				}else {
					setResponseCode(1);
				}
			}else {
				//setResponse("not successful".getBytes(), 0, 15);
				setResponseCode(0);
			}
			break;
		case LOGOUT:
			login.logout();
			break;
		case DELETE:
		{
			FlashStorage.eraseFlashData(0);
			FlashStorage.eraseFlashData(1);
		}
			}
		
	
		return APPLET_SUCCESS;
	}

	/**
	 * This method will be called by the VM when the session being handled by
	 * this Trusted Application instance is being closed 
	 * and this Trusted Application instance is about to be removed.
	 * This method cannot provide response data and therefore
	 * calling setResponse or setResponseCode methods from it will throw a NullPointerException.
	 * 
	 * @return APPLET_SUCCESS code (the status code is not used by the VM).
	 */
	public int onClose() {
		DebugPrint.printString("Goodbye, DAL!");
		return APPLET_SUCCESS;
	}
}
