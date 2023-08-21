package Login;
import com.intel.crypto.RsaAlg;
import com.intel.util.DebugPrint;
import com.intel.util.FlashStorage;


public class keys {
	
	
	private RsaAlg rsaAlg = RsaAlg.create();
	public keys() { 
	
		if(FlashStorage.getFlashDataSize(1) == 0) {
			
			rsaAlg.generateKeys((short)256);
			rsaAlg.setHashAlg(RsaAlg.HASH_TYPE_SHA256);
			rsaAlg.setPaddingScheme(RsaAlg.PAD_TYPE_PKCS1);
			
			
			byte[] newArray = new byte[516];
			rsaAlg.getKey(newArray, (short)0, newArray, (short)256, newArray, (short)260);
			
			//save to the flash storage.
			FlashStorage.writeFlashData(1, newArray, 0, 516);
			
		
		}
		else {//we already have keys
			byte[] keysArray = new byte[516];
			FlashStorage.readFlashData(1, keysArray, 0);
			DebugPrint.printBuffer(keysArray);
			byte[] modulus = new byte[256];
			byte[] publicKey = new byte[4];
			byte[] privateKey = new byte[256];
			System.arraycopy(keysArray, 0, modulus, 0, modulus.length);
			System.arraycopy(keysArray, modulus.length, publicKey, 0, publicKey.length);
			System.arraycopy(keysArray, modulus.length+publicKey.length, privateKey, 0, privateKey.length);
			rsaAlg.setHashAlg(RsaAlg.HASH_TYPE_SHA256);
			rsaAlg.setPaddingScheme(RsaAlg.PAD_TYPE_PKCS1);
			rsaAlg.generateKeys((short)256);
			rsaAlg.setKey(modulus, (short)0, rsaAlg.getModulusSize(), publicKey, (short)0, (short)publicKey.length, privateKey, (short)0, (short)privateKey.length);
			
		}
}
public byte[] generatePublicKey() {
	byte [] keys = new byte[516];
	FlashStorage.readFlashData(1, keys, 0);
	byte[] publicKey = new byte[4];
	System.arraycopy(keys, 256, publicKey, 0, 4);
	return publicKey;
}
public byte[] getModulus() {
	byte [] keys = new byte[516];
	FlashStorage.readFlashData(1, keys, 0);
	byte[] Modulus = new byte[256];
	System.arraycopy(keys, 0, Modulus, 0, 256);
	return Modulus;
}

/**
 * signs the message using rsaAlg and returns the signature.
 * @param message
 * @return
 */
public byte[] signMessage(byte[] message) {
	byte[] signature = new byte[256];
	rsaAlg.signComplete(message, (short)0, (short)message.length , signature, (short)0);
	return signature;
}

}
