/* Copyright (c) 2001-2004, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG, 
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.hsqldb.test;

import java.util.Random;

import org.hsqldb.lib.DoubleIntTable;

import junit.framework.TestCase;

/**
 * @author fredt@users
 */
public class TestHashStructures extends TestCase {

    public TestHashStructures(String s) {
        super(s);
    }

    Random randomgen = new java.util.Random();

    public void testHashMap() throws Exception {

        int                    testSize = 33;
        org.hsqldb.lib.HashMap hMap     = new org.hsqldb.lib.HashMap();
        org.hsqldb.lib.IntKeyHashMap hIntMap =
            new org.hsqldb.lib.IntKeyHashMap();
        java.util.HashMap uMap = new java.util.HashMap();

        // -
        populateBySerialIntKeys(uMap, hMap, testSize);
        compareByUIterator(uMap, hMap);
        compareByHIterator(uMap, hMap);

        // -
        populateByRandomIntKeys(uMap, hMap, testSize);
        compareByUIterator(uMap, hMap);
        compareByHIterator(uMap, hMap);

        //
        depopulateRandomly(uMap, hMap, 20);
        compareByUIterator(uMap, hMap);
        compareByHIterator(uMap, hMap);

        // -
        populateBySerialIntKeys(uMap, hMap, testSize);
        compareByUIterator(uMap, hMap);
        compareByHIterator(uMap, hMap);

        //
        depopulateByIterator(uMap, hMap, 20);
        compareByUIterator(uMap, hMap);
        compareByHIterator(uMap, hMap);
    }

    public void testIntKeyHashMap() throws Exception {

        int testSize = 33;
        org.hsqldb.lib.IntKeyHashMap hIntMap =
            new org.hsqldb.lib.IntKeyHashMap();
        java.util.HashMap uMap = new java.util.HashMap();

        populateBySerialIntKeysInt(uMap, hIntMap, testSize);
        compareByUIteratorInt(uMap, hIntMap);
        populateByRandomIntKeysInt(uMap, hIntMap, testSize);
        compareByUIteratorInt(uMap, hIntMap);
        compareByHIteratorInt(uMap, hIntMap);

        //
        depopulateByIntIterator(uMap, hIntMap, 20);
        compareByUIteratorInt(uMap, hIntMap);
        compareByHIteratorInt(uMap, hIntMap);

        //
        clearByIntIterator(uMap, hIntMap);
        compareByUIteratorInt(uMap, hIntMap);
        compareByHIteratorInt(uMap, hIntMap);

        // -
        populateBySerialIntKeysInt(uMap, hIntMap, testSize);
        compareByUIteratorInt(uMap, hIntMap);
        compareByHIteratorInt(uMap, hIntMap);

        //
        clearByIntIterator(uMap, hIntMap);
        compareByUIteratorInt(uMap, hIntMap);
        compareByHIteratorInt(uMap, hIntMap);

        //-
        assertTrue(true);
    }

    public void testHashMappedList() throws Exception {

        int testSize = 33;
        org.hsqldb.lib.HashMappedList hMap =
            new org.hsqldb.lib.HashMappedList();
        java.util.HashMap uMap = new java.util.HashMap();

        populateBySerialIntKeys(uMap, hMap, testSize);
        compareByUIterator(uMap, hMap);
        compareByHIterator(uMap, hMap);
        populateByRandomIntKeys(uMap, hMap, testSize);
        compareByUIterator(uMap, hMap);
        compareByHIterator(uMap, hMap);
        depopulateRandomly(uMap, hMap, 20);
        compareByUIterator(uMap, hMap);
        compareByHIterator(uMap, hMap);
        populateByRandomIntKeys(uMap, hMap, testSize);
        compareByUIterator(uMap, hMap);
        compareByHIterator(uMap, hMap);
        depopulateRandomly(uMap, hMap, 20);
        populateBySerialIntKeys(uMap, hMap, testSize);
        compareByUIterator(uMap, hMap);
        compareByHIterator(uMap, hMap);

        //-
        assertTrue(true);
    }

    public void testDoubleIntLookup() throws Exception {

        int testSize = 33;
        org.hsqldb.lib.IntKeyHashMap hIntMap =
            new org.hsqldb.lib.IntKeyHashMap();
        org.hsqldb.lib.DoubleIntTable intLookup =
            new org.hsqldb.lib.DoubleIntTable(12);

        populateBySerialIntKeysInt(intLookup, hIntMap, testSize);
        compareByHIteratorInt(intLookup, hIntMap);
        populateByRandomIntKeysInt(intLookup, hIntMap, testSize);
        compareByHIteratorInt(intLookup, hIntMap);

        //-
        assertTrue(true);
    }

    void populateBySerialIntKeys(java.util.HashMap uMap,
                                 org.hsqldb.lib.HashMap hMap,
                                 int testSize) throws Exception {

        for (int i = 0; i < testSize; i++) {
            int intValue = randomgen.nextInt();

            uMap.put(new Integer(i), new Integer(intValue));
            hMap.put(new Integer(i), new Integer(intValue));

            if (uMap.size() != hMap.size()) {
                throw new Exception("HashMap size mismatch");
            }
        }
    }

    void populateBySerialIntKeysInt(java.util.HashMap uMap,
                                    org.hsqldb.lib.IntKeyHashMap hMap,
                                    int testSize) throws Exception {

        for (int i = 0; i < testSize; i++) {
            int intValue = randomgen.nextInt();

            uMap.put(new Integer(i), new Integer(intValue));
            hMap.put(i, new Integer(intValue));

            if (uMap.size() != hMap.size()) {
                throw new Exception("HashMap size mismatch");
            }
        }
    }

    void populateBySerialIntKeysInt(DoubleIntTable intLookup,
                                    org.hsqldb.lib.IntKeyHashMap hMap,
                                    int testSize) throws Exception {

        for (int i = 0; i < testSize; i++) {
            int intValue = randomgen.nextInt();

            intLookup.add(i, intValue);
            hMap.put(i, new Integer(intValue));

            if (intLookup.size() != hMap.size()) {
                throw new Exception("HashMap size mismatch");
            }
        }
    }

    void populateByRandomIntKeysInt(DoubleIntTable intLookup,
                                    org.hsqldb.lib.IntKeyHashMap hMap,
                                    int testSize) throws Exception {

        for (int i = 0; i < testSize; i++) {
            int intValue = randomgen.nextInt();

            intLookup.add(intValue, i);
            hMap.put(intValue, new Integer(i));

            // actually this can happen as duplicates are allowed in DoubleIntTable
            if (intLookup.size() != hMap.size()) {
                throw new Exception("Duplicate random in int lookup");
            }
        }
    }

    void populateByRandomIntKeys(java.util.HashMap uMap,
                                 org.hsqldb.lib.HashMap hMap,
                                 int testSize) throws Exception {

        for (int i = 0; i < testSize; i++) {
            int intValue = randomgen.nextInt();

            uMap.put(new Integer(intValue), new Integer(i));
            hMap.put(new Integer(intValue), new Integer(i));

            if (uMap.size() != hMap.size()) {
                throw new Exception("HashMap size mismatch");
            }
        }
    }

    void populateByRandomIntKeysInt(java.util.HashMap uMap,
                                    org.hsqldb.lib.IntKeyHashMap hMap,
                                    int testSize) throws Exception {

        for (int i = 0; i < testSize; i++) {
            int intValue = randomgen.nextInt();

            uMap.put(new Integer(intValue), new Integer(i));
            hMap.put(intValue, new Integer(i));

            if (uMap.size() != hMap.size()) {
                throw new Exception("HashMap size mismatch");
            }
        }
    }

    void depopulateRandomly(java.util.HashMap uMap,
                            org.hsqldb.lib.HashMap hMap,
                            int testCount) throws Exception {

        int removeCount = 0;
        int size        = uMap.size();

        if (testCount > size / 2) {
            testCount = size / 2;
        }

        while (removeCount < testCount) {
            java.util.Iterator uIt = uMap.keySet().iterator();

            for (int i = 0; uIt.hasNext(); i++) {
                Object uKey     = uIt.next();
                int    intValue = randomgen.nextInt(size);

                if (intValue == i) {
                    uIt.remove();
                    hMap.remove(uKey);

                    removeCount++;
                }

                if (uMap.size() != hMap.size()) {
                    throw new Exception("HashMap size mismatch");
                }
            }
        }
    }

    void depopulateByIterator(java.util.HashMap uMap,
                              org.hsqldb.lib.HashMap hMap,
                              int testCount) throws Exception {

        org.hsqldb.lib.Iterator hIt = hMap.keySet().iterator();

        System.out.println(uMap.size());

        for (int i = 0; hIt.hasNext(); i++) {
            Object key   = hIt.next();
            int    check = randomgen.nextInt(2);

            if (check == i % 2) {
                hIt.remove();
                uMap.remove(key);
            }

            if (uMap.size() != hMap.size()) {
                throw new Exception("HashMap size mismatch");
            }
        }

        System.out.println(uMap.size());
    }

    void depopulateByIntIterator(java.util.HashMap uMap,
                                 org.hsqldb.lib.IntKeyHashMap hIntMap,
                                 int testCount) throws Exception {

        org.hsqldb.lib.Iterator hIt = hIntMap.keySet().iterator();

        System.out.println(uMap.size());

        for (int i = 0; hIt.hasNext(); i++) {
            Object key   = new Integer(hIt.nextInt());
            int    check = randomgen.nextInt(2);

            if (check == i % 2) {
                hIt.remove();
                uMap.remove(key);
            }

            if (uMap.size() != hIntMap.size()) {
                throw new Exception("HashMap size mismatch");
            }
        }

        System.out.println(uMap.size());
    }

    void clearByIntIterator(java.util.HashMap uMap,
                            org.hsqldb.lib.IntKeyHashMap hIntMap)
                            throws Exception {

        org.hsqldb.lib.Iterator hIt = hIntMap.keySet().iterator();

        System.out.println(uMap.size());

        for (int i = 0; hIt.hasNext(); i++) {
            Object key = new Integer(hIt.nextInt());

            hIt.remove();
            uMap.remove(key);

            if (uMap.size() != hIntMap.size()) {
                throw new Exception("HashMap size mismatch");
            }
        }

        System.out.println(uMap.size());
    }

    void compareByUIterator(java.util.HashMap uMap,
                            org.hsqldb.lib.HashMap hMap) throws Exception {

        java.util.Iterator uIt = uMap.keySet().iterator();

        for (int i = 0; uIt.hasNext(); i++) {
            Object uKey = uIt.next();
            Object oU   = uMap.get(uKey);
            Object hU   = hMap.get(uKey);

            if (!oU.equals(hU)) {
                throw new Exception("HashMap value mismatch");
            }
        }
    }

    void compareByHIterator(java.util.HashMap uMap,
                            org.hsqldb.lib.HashMap hMap) throws Exception {

        org.hsqldb.lib.Iterator hIt = hMap.keySet().iterator();

        for (int i = 0; hIt.hasNext(); i++) {
            Object hKey = hIt.next();
            Object oU   = uMap.get(hKey);
            Object hU   = hMap.get(hKey);

            if (!oU.equals(hU)) {
                throw new Exception("HashMap value mismatch");
            }
        }
    }

    void compareByUIteratorInt(java.util.HashMap uMap,
                               org.hsqldb.lib.IntKeyHashMap hMap)
                               throws Exception {

        java.util.Iterator uIt = uMap.keySet().iterator();

        for (int i = 0; uIt.hasNext(); i++) {
            Object uKey = uIt.next();
            Object oU   = uMap.get(uKey);
            Object hU   = hMap.get(((Integer) uKey).intValue());

            if (!oU.equals(hU)) {
                throw new Exception("HashMap value mismatch");
            }
        }
    }

    void compareByHIteratorInt(java.util.HashMap uMap,
                               org.hsqldb.lib.IntKeyHashMap hMap)
                               throws Exception {

        org.hsqldb.lib.Iterator hIt = hMap.keySet().iterator();

        for (int i = 0; hIt.hasNext(); i++) {
            Object hKey = new Integer(hIt.nextInt());
            Object oU   = uMap.get(hKey);
            Object hU   = hMap.get(((Integer) hKey).intValue());

            if (!oU.equals(hU)) {
                throw new Exception("HashMap value mismatch");
            }
        }
    }

    void compareByHIteratorInt(DoubleIntTable intLookup,
                               org.hsqldb.lib.IntKeyHashMap hMap)
                               throws Exception {

        org.hsqldb.lib.Iterator hIt = hMap.keySet().iterator();

        for (int i = 0; hIt.hasNext(); i++) {
            int     hK     = hIt.nextInt();
            int     lookup = intLookup.find(0, hK);
            int     lV     = intLookup.get(lookup, 1);
            Integer hV     = (Integer) hMap.get(hK);

            if (hV.intValue() != lV) {
                throw new Exception("HashMap value mismatch");
            }
        }
    }

    public static void main(String[] argv) {

        try {
            TestHashStructures test = new TestHashStructures("testHashMap");

            test.testHashMap();
            test.testIntKeyHashMap();
            test.testHashMappedList();
            test.testDoubleIntLookup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
