/*
 * (c) Copyright 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sdb.util;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.core.ARQConstants;
import com.hp.hpl.jena.query.engine2.op.Op;
import com.hp.hpl.jena.sdb.core.sqlnode.GenerateSQL;
import com.hp.hpl.jena.sdb.core.sqlnode.SqlNode;
import com.hp.hpl.jena.sdb.engine.QueryEngineQuadSDB;
import com.hp.hpl.jena.sdb.store.Store;
import com.hp.hpl.jena.shared.PrefixMapping;



/** Print utilities */

public class PrintSDB
{
    public static String divider = "----------------" ;
    
    public static void print(Store store, Query query, QueryEngineQuadSDB queryEngine)
    {
        if ( queryEngine == null )
            queryEngine = new QueryEngineQuadSDB(store, query) ;
        Op op = queryEngine.getOp() ;
        System.out.println(op.toString(query.getPrefixMapping())) ;
    }

    public static void print(Op op)
    { print(op, null) ; }

    
    public static void print(Op op, PrefixMapping pmap)
    {
        if ( pmap == null )
            pmap = ARQConstants.getGlobalPrefixMap() ;
        System.out.println(op.toString(pmap)) ;
    }
    
    public static void printSQL(SqlNode sqlNode)
    {
        System.out.println(sqlNode.toString()) ;
        System.out.println(divider) ;
        String sqlString = GenerateSQL.toSQL(sqlNode) ;
        System.out.println(sqlString) ;
    }
    
}

/*
 * (c) Copyright 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */