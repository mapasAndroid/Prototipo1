package com.example.cristhian.prototipo2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by cristhian on 5/11/15.
 */
public class Encriptador {

    public String getSha1(String cadena){

        MessageDigest mDigest = null;
        try {
            mDigest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] result = mDigest.digest(cadena.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
