/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.security;

import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import org.parabuild.ci.common.*;

/**
 * Security utility class
 */
public final class SecurityUtils {

//  private static final String ALGORITHM = "DES";

  // 8-byte Salt
  private static final byte[] salt = {
    (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
    (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
  };

  // Iteration count
  private static final int ITERATION_COUNT = 19;


  /**
   * Utility class constructor.
   */
  private SecurityUtils() {
  }


  public static String encrypt(final String original, final String passPhrase) {
    try {
      final Cipher cipher = makeCipher(Cipher.ENCRYPT_MODE, passPhrase);
      final byte[] encryptedOriginal = cipher.doFinal(original.getBytes());
      return StringUtils.encodeToHex(encryptedOriginal);
    } catch (final NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | IllegalStateException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchPaddingException | InvalidKeySpecException e) {
      return "";
    }
  }


  public static String decrypt(final String encryptedOriginal, final String passPhrase) {
    try {
      final Cipher cipher = makeCipher(Cipher.DECRYPT_MODE, passPhrase);
      final byte[] originalBytes = cipher.doFinal(StringUtils.decodeFromHex(encryptedOriginal));
      return new String(originalBytes); // NOPMD
    } catch (final NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | IllegalStateException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchPaddingException | InvalidKeySpecException e) {
      return "";
    }
  }


  private static Cipher makeCipher(final int cipherMode, final String passPhrase) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
    final AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
    final KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, ITERATION_COUNT);
    final SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
    final Cipher cipher = Cipher.getInstance(key.getAlgorithm());
    cipher.init(cipherMode, key, paramSpec);
    return cipher;
  }
}
