package Controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * The class HashSalt is used when hashing and salting a password.
 */

public class HashSalt {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * The method generates a hashed and salted password.
     * @param password is the password.
     * @param salt is the salt which will be implemented in the hashed password.
     * @return the generated hashed and salted password as a String.
     */
    public static String genHashSalted(String password, byte[] salt) {
        try{
            // Hash-algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Resets digest
            digest.reset();
            // Updates
            digest.update(salt);
            // Hashes password in bytes
            byte[] hash = digest.digest(password.getBytes());
            // Converts from bytes to hexadecimal in form of a string and returns
            return bytesToStringHex(hash);
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        // Returns null if it is not possible to convert and return
        return null;
    }

    /**
     * The method converts bytes to a String with hexadecimals.
     * @param bytes an array of bytes
     */
    private static String bytesToStringHex(byte[] bytes){
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++){
            int j = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[j >>> 4];
            hexChars[i * 2 + 1] = hexArray[j & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * The method creates a random array of bytes.
     * @return an array of bytes.
     */
    public static byte[] createSalt(){
        // Random generator
        SecureRandom random = new SecureRandom();
        // Byte variable
        byte[] salt = new byte[16];
        // Fills up the array with random numbers
        random.nextBytes(salt);
        // Returns salt
        return salt;
    }

    /**
     * The method converts from byte to a String hexadecimal.
     * @param num is an amount of byte.
     * @return an amount of hexadecimals of chars converted to String.
     */
    private static String byteToHex(byte num) {
        // An array of two chars
        char[] hexDigits = new char[2];
        // This returns the char representation of the specified digit in the specified radix.
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    /**
     * The method converts from hexadecimal to byte.
     * @param hexString is a String of hexadecimals.
     * @return a converted String of hexadecimals to byte.
     */
    private static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    /**
     * The method uses an inbuilt method Character.digit.
     * @param hexChar is an amount of chars.
     * @return the numeric value of hexChar in the specified radix.
     */
    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    /**
     * The method converts byte to hexadecimal and adds it into a StringBuffer
     * @param byteArray is an array of bytes
     * @return a String of hexadecimals
     */
    public static String encodeHexString(byte[] byteArray) {
        // Creates a StringBuffer
        StringBuffer hexStringBuffer = new StringBuffer();
        // Converts every byte to hexadecimal and adds text at the end of the existence text
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    /**
     * The method converts hexadecimal to byte
     * @param hexString is a String of hexadecimals
     * @return an array of byte
     */
    public static byte[] decodeHexString(String hexString) {
        // Checks if the length of the String is valid
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        // Converts from 0-3, 4-7, 8-11, 12-15 from hexadecimal to byte and adds into the array
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }
}
