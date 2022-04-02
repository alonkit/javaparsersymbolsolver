package com.yourorganization.maven_sample;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.github.javaparser.ast.Node;

public class SymbolTable{
    private LinkedList<Hashtable<String, Node>> table;

    public SymbolTable(){
        table = new LinkedList<Hashtable<String, Node>>();
    }

    public Node findDecleration(String name){
        Iterator<Hashtable<String, Node>> lit = table.descendingIterator();
        Node ret= null;
        while(lit.hasNext()){
            Hashtable<String, Node> hash = lit.next();
            if(hash.containsKey(name)){
                ret = hash.get(name);
                break;
            }
        }
        return ret;
    }

    public void pushScope(){
        table.add(new Hashtable<String, Node>());
    }

    public void popScope(){
        table.removeLast();
    }

}