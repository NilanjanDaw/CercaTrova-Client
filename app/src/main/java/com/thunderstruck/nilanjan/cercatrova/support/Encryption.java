package com.thunderstruck.nilanjan.cercatrova.support;

import android.util.Base64;
import android.util.Log;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Nilanjan and Debapriya :) on 14-May-17.
 * Project CercaTrova
 */

public class Encryption {
    /*
     Hexadecimal lookup array for byte array to Hexadecimal string conversion
     */
    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    private static final String TAG = Encryption.class.getName();
    private static SecretKey secretKey;

    public Encryption() throws NoSuchPaddingException, NoSuchAlgorithmException {
        byte[] decodedKey = Base64.decode(Constants.PASS_KEY, Base64.DEFAULT);
        // rebuild key using SecretKeySpec
        secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /*
    conversion of byte array to hexadecimal
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String encryptPassword(String arg) throws Exception {

        byte[] encryptMessage = encryptMsg(arg, secretKey);
        Log.d(TAG, "getCipherText: " + bytesToHex(encryptMessage));
        String decryptedMessage = decryptMsg(encryptMessage, secretKey);
        Log.d(TAG, "getCipherText: " + decryptedMessage);
        return bytesToHex(encryptMessage);
    }

    private byte[] encryptMsg(String message, SecretKey secret)
            throws Exception {
       /* Encrypt the message. */
        Cipher cipher;
        //the application calls the Cipher's getInstance method in order to create a Cipher object,
        // and passes the name of the requested transformation to it
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        //A transformation is of the form : "algorithm/mode/padding"
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        return cipher.doFinal(message.getBytes("UTF-8"));
    }

    private String decryptMsg(byte[] cipherText, SecretKey secret)
            throws Exception {
	    /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher;
        //the application calls the Cipher's getInstance method in order to create a Cipher object,
        // and passes the name of the requested transformation to it
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        //A transformation is of the form : "algorithm/mode/padding"
        cipher.init(Cipher.DECRYPT_MODE, secret);
        return new String(cipher.doFinal(cipherText), "UTF-8");
    }
}