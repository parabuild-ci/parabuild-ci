/*
Copyright (c) Dale Anson, 2004
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  1. Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
  3. The name of the author may not be used to endorse or promote products
  derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package ise.antelope.tasks.password;

import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import ise.antelope.tasks.util.Base64;

/**
 * Simple class to encode and decode passwords. The encrypted password is a
 * base-64 encoded string, so it's suitable for storage in a properties file or
 * where ever a string might be stored. This class could be used for larger
 * strings than passwords, but all I need is an easy way to store passwords in a
 * file.
 *
 * @author    Dale Anson, May 2002
 * @version   $Revision: 138 $
 */
public class PasswordHandler {

    /** use Triple-DES as algorithm */
    public final static String DESEDE = "DESede";
    /** key to use if none given */
    public final static String DEFAULT_KEY = "The quick brown fox jumped over the lazy dog.";
    /** default character encoding */
    private final static String ENCODING = "UTF8";

    private KeySpec keySpec;
    private SecretKeyFactory keyFactory;
    private Cipher cipher;

    /**
     * Constructor for PasswordHandler, uses default key.
     *
     * @exception PasswordHandlerException  Description of Exception
     */
    public PasswordHandler() throws PasswordHandlerException {
        this(DEFAULT_KEY);
    }

    /**
     * Constructor for PasswordHandler
     *
     * @param encryptKey                    String to use for a key, must be at
     *      least 32 characters long.
     * @exception PasswordHandlerException  Description of Exception
     */
    public PasswordHandler(String encryptKey) throws PasswordHandlerException {
        if (encryptKey == null) {
            throw new IllegalArgumentException("Encrypt key cannot be null.");
        }
        if (encryptKey.trim().length() < 32) {
            throw new IllegalArgumentException("Encrypt key cannot be less than 32 characters.");
        }

        try {
            byte[] bytes = encryptKey.getBytes(ENCODING);
            keySpec = new DESedeKeySpec(bytes);
            keyFactory = SecretKeyFactory.getInstance(DESEDE);
            cipher = Cipher.getInstance(DESEDE);
        }
        catch (Exception e) {
            throw new PasswordHandlerException(e);
        }
    }

    /**
     * Encrypt a password.
     *
     * @param password                      the password to encrypt
     * @return                              the encrypted password.
     * @exception PasswordHandlerException  Description of Exception
     */
    public String encrypt(String password) throws PasswordHandlerException {
        if (password == null || password.trim().length() == 0)
            throw new IllegalArgumentException("Password cannot be null or empty.");

        try {
            SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plain_bytes = password.getBytes(ENCODING);
            byte[] cipher_bytes = cipher.doFinal(plain_bytes);
            return Base64.encodeBytes(cipher_bytes);
        }
        catch (Exception e) {
            throw new PasswordHandlerException(e);
        }
    }

    /**
     * Decrypt a password.
     *
     * @param encryptedPassword             the encrypted password, needing to
     *      be decrypted
     * @return                              the original, plain text password
     * @exception PasswordHandlerException  Description of Exception
     */
    public String decrypt(String encryptedPassword) throws PasswordHandlerException {
        if (encryptedPassword == null || encryptedPassword.trim().length() <= 0)
            throw new IllegalArgumentException("Encrypted password cannot be null or empty.");

        try {
            SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] cipher_bytes = Base64.decode(encryptedPassword);
            byte[] plain_bytes = cipher.doFinal(cipher_bytes);
            //StringBuffer sb = new StringBuffer();
            //for (int i = 0; i < plain_bytes.length; i++) {
            //    sb.append((char) plain_bytes[i]);
            //}
            return new String(plain_bytes);
        }
        catch (Exception e) {
            throw new PasswordHandlerException(e);
        }
    }

    /**
     * for testing only
     *
     * @param args  The command line arguments
     */
    public static void main(String[] args) {
        try {
                //testPassword.append(String.valueOf(i % 10));
                String testPassword = "abcdef1234567890";
                System.out.println("original: " + testPassword.toString());
                System.out.println("original length: " + testPassword.length());
                PasswordHandler ph = new PasswordHandler();
                String encrypted = ph.encrypt(testPassword.toString());
                System.out.println("encrypted: " + encrypted);
                System.out.println("encrypted length: " + encrypted.length());
                if (encrypted.length() >= 32)
                    System.exit(0);
                ph = new PasswordHandler();
                String plain = ph.decrypt(encrypted);
                System.out.println("decrypted: " + plain);
                if (testPassword.toString().compareTo(plain) != 0) {
                    throw new Exception("test failed!");
                }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

