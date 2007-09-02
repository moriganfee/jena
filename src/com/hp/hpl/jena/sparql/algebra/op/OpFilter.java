/*
 * (c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sparql.algebra.op;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVisitor;
import com.hp.hpl.jena.sparql.algebra.Transform;
import com.hp.hpl.jena.sparql.expr.E_LogicalAnd;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.util.NodeIsomorphismMap;

public class OpFilter extends Op1
{
    // Canonicalization turns "&&" into multiple expr lists items
    // 
    private static boolean canonicalize = false ;
    ExprList expressions ;
    
    public static OpFilter filter(Expr expr, Op op)
    {
        if ( op instanceof OpFilter )
        {
            OpFilter f = (OpFilter)op ;
            f.getExprs().add(expr) ;
            return f ;
        }
        ExprList exprList = asExprList(expr) ;
        return new OpFilter(exprList, op) ;
        
//        ExprList x = asExprList(expr) ;
//        if ( /*canonicalize &&*/ ( op instanceof OpFilter ) )
//        {
//            OpFilter f = (OpFilter)op ;
//            f.getExprs().addAll(x) ;
//            return f ;
//        }
//        return new OpFilter(x, op) ;
    }
    
    public static OpFilter filter(ExprList exprs, Op op)
    {
        if ( op instanceof OpFilter )
        {
            OpFilter f = (OpFilter)op ;
            f.getExprs().addAll(exprs) ;
            return f ;
        }
        
        return new OpFilter(exprs, op) ;
    }

    private static ExprList asExprList(Expr expr)
    {
        ExprList exprList = new ExprList() ;
        mergeExprList(exprList, expr) ;
        return exprList ;
    }
    
    private static void mergeExprList(ExprList exprList, Expr expr)
    {
        if ( canonicalize )
        {
            // Explode &&-chain to exprlist.
            while ( expr instanceof E_LogicalAnd )
            {
                E_LogicalAnd x = (E_LogicalAnd)expr ;
                Expr left = x.getArg1() ;
                Expr right = x.getArg2() ;
                mergeExprList(exprList, left) ;
                expr = right ;
            }
            // Drop through and add remaining
        }
        exprList.add(expr) ;
    }
    
    private OpFilter(Expr expr , Op sub)
    { 
        super(sub) ;
        expressions = new ExprList() ;
        expressions.add(expr) ;
    }
    

    private OpFilter(ExprList exprs , Op sub)
    { 
        super(sub) ;
        expressions = exprs ;
    }
    
    // Compress a filter(filter(filter(op)))) into one filter(op) 
    public static OpFilter tidy(OpFilter base)
    {
        ExprList exprs = new ExprList() ;
        
        Op op = base ; 
        while ( op instanceof OpFilter )
        {
            OpFilter f = (OpFilter)op ;
            exprs.addAll(f.getExprs()) ;
            //expr = new E_LogicalAnd(expr, f.getExpr()) ;
            op = f.getSubOp() ;
        }
        return OpFilter.filter(exprs, op) ;
    }
    
//    // Drill down a chain of OpFilters.
//    public Op underlyingOp()
//    {
//        Op sub = this ;
//        while ( sub instanceof OpFilter )
//        {
//            OpFilter f = (OpFilter)sub ;
//            sub = f.getSubOp() ;
//        }
//        return sub ;
//    }
    
    public ExprList getExprs() { return expressions ; }
    
    public String getName() { return "filter" ; }
    
    public Op apply(Transform transform, Op subOp)
    { return transform.transform(this, subOp) ; }

    public void visit(OpVisitor opVisitor) { opVisitor.visit(this) ; }
    
    public Op copy(Op subOp)                { return new OpFilter(expressions, subOp) ; }
    
    public int hashCode()
    {
        return expressions.hashCode() ;
    }
    
    public boolean equalTo(Op other, NodeIsomorphismMap labelMap)
    {
        if ( ! (other instanceof OpFilter) ) return false ;
        OpFilter opFilter = (OpFilter)other ;
        if ( ! expressions.equals(opFilter.expressions) )
            return false ;
        
        return getSubOp().equalTo(opFilter.getSubOp(), labelMap) ;
    }
}

/*
 * (c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
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