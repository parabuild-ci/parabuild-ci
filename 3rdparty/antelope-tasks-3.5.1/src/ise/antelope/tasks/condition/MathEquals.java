/*
* The Apache Software License, Version 1.1
*
* Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution, if
*    any, must include the following acknowlegement:
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowlegement may appear in the software itself,
*    if and wherever such third-party acknowlegements normally appear.
*
* 4. The names "The Jakarta Project", "Ant", and "Apache Software
*    Foundation" must not be used to endorse or promote products derived
*    from this software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache"
*    nor may "Apache" appear in their names without prior written
*    permission of the Apache Group.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation.  For more
* information on the Apache Software Foundation, please see
* <http://www.apache.org/>.
*/
package ise.antelope.tasks.condition;

import org.apache.tools.ant.taskdefs.condition.Equals;
import org.apache.tools.ant.BuildException;

/**
 * Extends Equals condition to test if the first argument is greater than the
 * second argument. Will handle base 10 integer and decimal numbers, otherwise,
 * treats arguments as Strings.
 * @author Dale Anson
 */
public class MathEquals extends Equals {

    private String arg1, arg2;
    private boolean trim = false;
    private boolean caseSensitive = true;

    public void setArg1(String a1) {
        arg1 = a1;
    }

    public void setArg2(String a2) {
        arg2 = a2;
    }

    /**
     * Should we want to trim the arguments before comparing them?
     *
     * @since Revision: 1.3, Ant 1.5
     */
    public void setTrim(boolean b) {
        trim = b;
    }

    /**
     * Should the comparison be case sensitive?
     *
     * @since Revision: 1.3, Ant 1.5
     */
    public void setCasesensitive(boolean b) {
        caseSensitive = b;
    }

    public boolean eval() throws BuildException {
        if (arg1 == null || arg2 == null) {
            throw new BuildException("both arg1 and arg2 are required in "
                                     + "math equals");
        }

        if (trim) {
            arg1 = arg1.trim();
            arg2 = arg2.trim();
        }
        
        // check if args are numbers
        boolean first_is_num = arg1.matches("\\d*[.]?\\d*");
        boolean second_is_num = arg2.matches("\\d*[.]?\\d*");
        if (first_is_num && second_is_num) {
           double first_double = Double.parseDouble(arg1);
           double second_double = Double.parseDouble(arg2);
           return first_double == second_double;
        }
        
        
        return caseSensitive ? arg1.compareTo(arg2) == 0 : arg1.compareToIgnoreCase(arg2) == 0;
    }

}
