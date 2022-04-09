package com.yourorganization.maven_sample;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

public class node_keys{
    public final Class<?> c;
    public final Range r;
    public final String s;
    public static int i=0;
    public node_keys(Class<?>c,Range r, String s){
        this.c=c;
        this.r=r;
        this.s=s;
    }

    public node_keys(Node n){
        this.c=n.getClass();
        this.r=n.getRange().get();
        this.s=nodeFile(n);
    }
    
    public static String nodeFile(Node n){
        if( n instanceof CompilationUnit){
            
            return ((CompilationUnit) n).getStorage().get().getFileName();
        }
        // System.out.println(i++);
         return nodeFile(n.getParentNode().get());
        
    }

    @Override
    public int hashCode(){
        return (s.hashCode()+c.hashCode()+r.hashCode())/3;
    }


    @Override
    public boolean equals(Object obj){
        // This refers to current instance itself
        if (this == obj)
            return true;
  
        if (obj == null)
            return false;
  
        if (getClass() != obj.getClass())
            return false;
  
        node_keys other = (node_keys)obj;
  
        if (s == null) {
            if (other.s != null)
                return false;
        }
  
        else if (!s.equals(other.s))
            return false;
  
        if (r == null) {
            if (other.r != null)
                return false;
        }
  
        else if (!r.equals(other.r))
            return false;
  
        if (c == null) {
            if (other.c != null)
                return false;
        }
  
        else if (!c.equals(other.c))
            return false;
  
        return true;
    }


}
