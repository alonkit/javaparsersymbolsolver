package com.yourorganization.maven_sample;

import java.util.IdentityHashMap;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.NodeList;


public class VoidVisitorVars extends VoidVisitorOrdered{
    private IdentityHashMap<Node, Node> connections;

    public VoidVisitorVars(SymbolTable stack, IdentityHashMap<Node, Node> connections){
        super(stack);
        this.connections = connections;

    }

    @Override
    public void visit(VariableDeclarator n, final Void arg){
        stack.insertDeclaration(n.getNameAsString(), n);
        super.visit(n, arg);
    }

    @Override
    public void visit(NameExpr n, final Void arg){
        Node decl = stack.findDecleration(n.getName().getIdentifier());
        if(decl != null){
            connections.put(n, decl);
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, final Void arg){
        stack.pre_scope(n);
        NodeList<BodyDeclaration<?>> members = n.getMembers();
        for (BodyDeclaration<?> bodyDeclaration : members) {
            if(bodyDeclaration.isFieldDeclaration()){
                for (VariableDeclarator var  : bodyDeclaration.asFieldDeclaration().getVariables()) {
                    stack.insertDeclaration(var.getNameAsString(), var);
                }
            }
            // else if(bodyDeclaration)
        }
        super.visit(n, arg);
        stack.post_scope(n);
    }
}
