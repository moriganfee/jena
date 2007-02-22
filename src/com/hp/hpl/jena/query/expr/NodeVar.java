/*
 * (c) Copyright 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.query.expr;

//import org.apache.commons.logging.LogFactory;

import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.core.ARQInternalErrorException;
import com.hp.hpl.jena.query.core.Var;
import com.hp.hpl.jena.query.engine.binding.Binding;
import com.hp.hpl.jena.query.function.FunctionEnv;
import com.hp.hpl.jena.query.Query ;
import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.query.util.IndentedWriter ;

/** A node that is a variable in an expression. */
 
public class NodeVar extends ExprNode
{
    Var varNode = null ;
    
    public NodeVar(String name) { varNode = Var.alloc(name) ; }
    public NodeVar(Node n)
    { 
        if ( ! n.isVariable() )
            throw new ARQInternalErrorException("Attempt to create a NodeVar from a non variable Node: "+n) ;
        varNode = Var.alloc(n) ;
    }
    
    public NodeVar(Var n)
    { 
        varNode = n ;
    }
    
    public NodeValue eval(Binding binding, FunctionEnv env)
    {
        if ( binding == null )
            throw new VariableNotBoundException("Not bound: (no binding): "+varNode) ;
        Node v = binding.get(varNode) ;
        if ( v == null )
            throw new VariableNotBoundException("Not bound: variable "+varNode) ;
        // Wrap as a NodeValue.
        return NodeValue.makeNode(v) ;
    }

    public Expr copySubstitute(Binding binding, boolean foldConstants)
    {
        if ( binding == null || !binding.contains(varNode) )
            return new NodeVar(varNode.getVarName()) ;
            
        try { return eval(binding, null) ; }
        catch (VariableNotBoundException ex)
        {
            LogFactory.getLog(NodeVar.class).warn("Failed to eval bound variable");
            throw ex ;
        }
    }
    
    public void visit(ExprVisitor visitor) { visitor.visit(this) ; }
    
    public void format(Query query, IndentedWriter out)
    {
        out.print('?') ;
        out.print(varNode.getName()) ;
    }
    
    public int hashCode() { return varNode.hashCode() ; }
    
    public boolean equals(Object other)
    {
        if ( this == other ) return true ;

        if ( ! ( other instanceof NodeVar ) )
            return false ;
        NodeVar nvar = (NodeVar)other ;
        return getVarName().equals(nvar.getVarName()) ;
    }
    
    public boolean isVariable() { return true ; }
    /** @return Returns the name. */
    public String getVarName()  { return varNode.getName() ; }
    public NodeVar getNodeVar() { return this ; }
    public Var asVar()          { return varNode ; }
    public Node getAsNode()     { return varNode ; }
    
    public String toString()
    {
        return "?"+varNode.getName() ;
    }
}

/*
 *  (c) Copyright 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 *  All rights reserved.
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
