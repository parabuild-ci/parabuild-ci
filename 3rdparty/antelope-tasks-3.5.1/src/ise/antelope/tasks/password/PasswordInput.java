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

import java.io.*;

/**
 * Extends Input to mask user input for a password.
 * @version   $Revision: 138 $
 */
public class PasswordInput extends Input {

    /** Constructor for PasswordInput  */
    public PasswordInput() {
        super();
    }

    /**
     * Constructor for PasswordInput
     *
     * @param prompt
     */
    public PasswordInput(String prompt) {
        super(prompt);
    }

    /**
     * Gets the input attribute of the PasswordInput object
     *
     * @return   The encrypted password, or "" if the user did not enter a password.
     */
    public String getInput() {
        try {
            System.out.print(prompt + " ");
            BufferedReader br = new BufferedReader(new InputStreamReader(new PasswordInputStream()));
            String plain = br.readLine().trim();
            if (plain == null || plain.length() == 0)
                return "";
            
            String password = null;
            try {
                PasswordHandler ph = new PasswordHandler();
                password = ph.encrypt(plain);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return password;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }
}

