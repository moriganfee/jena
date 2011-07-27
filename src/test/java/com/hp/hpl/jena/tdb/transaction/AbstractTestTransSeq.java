/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hp.hpl.jena.tdb.transaction;


import java.util.Iterator ;

import org.junit.Test ;
import org.openjena.atlas.junit.BaseTest ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.core.Quad ;
import com.hp.hpl.jena.sparql.sse.SSE ;
import com.hp.hpl.jena.tdb.DatasetGraphTxn ;
import com.hp.hpl.jena.tdb.ReadWrite ;
import com.hp.hpl.jena.tdb.StoreConnection ;
import com.hp.hpl.jena.tdb.TDBException ;

/** Basic tests and tests of ordering (single thread) */
public abstract class AbstractTestTransSeq extends BaseTest
{
    static Quad q  = SSE.parseQuad("(<g> <s> <p> <o>) ") ;
    static Quad q1 = SSE.parseQuad("(<g> <s> <p> <o1>)") ;
    static Quad q2 = SSE.parseQuad("(<g> <s> <p> <o2>)") ;
    static Quad q3 = SSE.parseQuad("(<g> <s> <p> <o3>)") ;
    static Quad q4 = SSE.parseQuad("(<g> <s> <p> <o4>)") ;
    
   
    private StoreConnection sConn ;

    protected abstract StoreConnection getStoreConnection() ;

    // Basics.
    
    @Test public void trans_01()
    {
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsg = sConn.begin(ReadWrite.READ) ;
        dsg.close() ;
    }
    

    @Test public void trans_02()
    {
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsg = sConn.begin(ReadWrite.WRITE) ;
        try {
            dsg.add(q) ;
            assertTrue(dsg.contains(q)) ;
            dsg.commit() ;
        } finally { dsg.close() ; }
    }
    
    @Test public void trans_03()
    {
        // WRITE-commit-READ
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsg = sConn.begin(ReadWrite.WRITE) ;
        
        dsg.add(q) ;
        assertTrue(dsg.contains(q)) ;
        dsg.commit() ;
        dsg.close() ;
        
        DatasetGraphTxn dsg2 = sConn.begin(ReadWrite.READ) ;
        assertTrue(dsg2.contains(q)) ;
        dsg2.close() ;
    }
    
    @Test public void trans_04()
    {
        // WRITE-abort-READ
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsg = sConn.begin(ReadWrite.WRITE) ;
        
        dsg.add(q) ;
        assertTrue(dsg.contains(q)) ;
        dsg.abort() ;
        dsg.close() ;
        
        DatasetGraphTxn dsg2 = sConn.begin(ReadWrite.READ) ;
        assertFalse(dsg2.contains(q)) ;
        dsg2.close() ;
    }
    
    @Test public void trans_05()
    {
        // READ before WRITE remains seeing old view - READ before WRITE starts 
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsgR1 = sConn.begin(ReadWrite.READ) ;
        DatasetGraphTxn dsgW = sConn.begin(ReadWrite.WRITE) ;
        
        dsgW.add(q) ;
        dsgW.commit() ;
        dsgW.close() ;
        
        assertFalse(dsgR1.contains(q)) ;
        dsgR1.close() ;

        DatasetGraphTxn dsgR2 = sConn.begin(ReadWrite.READ) ;
        assertTrue(dsgR2.contains(q)) ;
        dsgR2.close() ;
    }

    @Test public void trans_06()
    {
        // READ(block)-WRITE-commit-WRITE-abort-WRITE-commit-READ(close)-check
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsgR1 = sConn.begin(ReadWrite.READ) ;
        
        // IF 
        // dsgR1.close() ;
        // THEN it works.
        // ==> deplay replay
        
        DatasetGraphTxn dsgW1 = sConn.begin(ReadWrite.WRITE) ;
        dsgW1.add(q1) ;
        dsgW1.commit() ;
        dsgW1.close() ;
        assertFalse(dsgR1.contains(q1)) ;
        
        DatasetGraphTxn dsgW2 = sConn.begin(ReadWrite.WRITE) ;
        dsgW2.add(q2) ;
        dsgW2.abort() ; // ABORT
        dsgW2.close() ;
        assertFalse(dsgR1.contains(q2)) ;

        DatasetGraphTxn dsgW3 = sConn.begin(ReadWrite.WRITE) ;
        dsgW3.add(q3) ;
        dsgW3.commit() ;
        dsgW3.close() ;
        assertFalse(dsgR1.contains(q3)) ;
        
        dsgR1.close() ;
        
        DatasetGraphTxn dsgR2 = sConn.begin(ReadWrite.READ) ;
        
        assertTrue(dsgR2.contains(q1)) ;
        assertFalse(dsgR2.contains(q2)) ;
        assertTrue(dsgR2.contains(q3)) ;
        dsgR2.close() ;
    }

    @Test public void trans_07()
    {
        // READ before WRITE remains seeing old view - READ after WRITE starts 
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsgW = sConn.begin(ReadWrite.WRITE) ;
        DatasetGraphTxn dsgR = sConn.begin(ReadWrite.READ) ;
        
        dsgW.add(q) ;
        dsgW.commit() ;
        dsgW.close() ;
        
        assertFalse(dsgR.contains(q)) ;
        dsgR.close() ;
    }
    
    @Test public void trans_08()
    {
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsgW = sConn.begin(ReadWrite.WRITE) ;
        dsgW.add(q) ;
        
        DatasetGraphTxn dsgR1 = sConn.begin(ReadWrite.READ) ;
        assertFalse(dsgR1.contains(q)) ;  
        
        dsgW.commit() ;
        dsgW.close() ;
        
        DatasetGraphTxn dsgR2 = sConn.begin(ReadWrite.READ) ;
        
        assertFalse(dsgR1.contains(q)) ;    // Before view
        assertTrue(dsgR2.contains(q)) ;     // After view
        dsgR1.close() ;
        dsgR2.close() ;
    }


    @Test public void trans_09()
    {
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsgW1 = sConn.begin(ReadWrite.WRITE) ;
        dsgW1.add(q1) ;
        dsgW1.commit() ;
        dsgW1.close() ;

        DatasetGraphTxn dsgW2 = sConn.begin(ReadWrite.WRITE) ;
        dsgW2.add(q2) ;
        dsgW2.commit() ;
        dsgW2.close() ;

        DatasetGraphTxn dsgR2 = sConn.begin(ReadWrite.READ) ;
        
        assertTrue(dsgR2.contains(q1)) ;
        assertTrue(dsgR2.contains(q2)) ;
        
        dsgR2.close() ;
    }

    @Test public void trans_10()
    {
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsgW1 = sConn.begin(ReadWrite.WRITE) ;
        dsgW1.add(q1) ;
        dsgW1.commit() ;
        dsgW1.close() ;

        DatasetGraphTxn dsgR1 = sConn.begin(ReadWrite.READ) ;
        DatasetGraphTxn dsgW2 = sConn.begin(ReadWrite.WRITE) ;
        dsgW2.add(q2) ;
        dsgW2.commit() ;
        dsgW2.close() ;

        DatasetGraphTxn dsgR2 = sConn.begin(ReadWrite.READ) ;
        assertTrue(dsgR1.contains(q1)) ;
        assertFalse(dsgR1.contains(q2)) ;
        
        assertTrue(dsgR2.contains(q1)) ;
        assertTrue(dsgR2.contains(q2)) ;
        
        dsgR1.close() ;
        dsgR2.close() ;
    }

    @Test (expected=TDBTransactionException.class)
    public void trans_20()
    {
        // Two WRITE
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsgW1 = sConn.begin(ReadWrite.WRITE) ;
        DatasetGraphTxn dsgW2 = sConn.begin(ReadWrite.WRITE) ;
    }
    
    @Test (expected=TDBException.class) 
    public void trans_21()
    {
        // READ-add
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsg = sConn.begin(ReadWrite.READ) ;
        dsg.add(q) ;
    }
    
    //@Test 
    public void trans_30()
    {
        // WRITE lots
        StoreConnection sConn = getStoreConnection() ;
        DatasetGraphTxn dsg = sConn.begin(ReadWrite.WRITE) ;
        for ( int i = 0 ; i < 400 ; i++ )
        {
            Quad q = SSE.parseQuad("(_ <s> <p> "+i+")") ;
            dsg.add(q) ;
        }
        dsg.commit() ;
        dsg.close() ;
    }
}

