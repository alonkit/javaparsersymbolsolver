package com.yourorganization.maven_sample;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import com.github.javaparser.ast.Node;

public class SymbolTable implements ScopeFuncInterface<Node>{
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

    public void insertDeclaration(String name, Node n){
        table.getFirst().put(name, n);
    }


    public void pushScope(){
        table.add(new Hashtable<String, Node>());
    }

    public void popScope(){
        table.removeLast();
    }



    @Override
    public void pre_scope(Node n) {
        pushScope();
        
    }

    @Override
    public void post_scope(Node n) {
        popScope();
        
    }

    @Override
    public void mid_scope(Node n) {
       //fill?
        
    }

    
}