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
 * A simple input field to get data from the command line.
 * @version   $Revision: 1.1 $
 */
public abstract class Input {

    protected String prompt = null;
    private String default_value = null;

    /** Constructor for Input  */
    public Input() {
        this("");
    }

    /**
     * Constructor for Input
     *
     * @param prompt
     */
    public Input(String prompt) {
        this.prompt = prompt;
    }

    /**
     * Gets the input attribute of the Input object
     *
     * @return   The input value
     */
    public abstract String getInput();
    
    public void setDefaultValue(String default_value){
        this.default_value = default_value;   
    }
    
    public String getDefaultValue() {
        return default_value;   
    }
}

