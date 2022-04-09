package com.yourorganization.maven_sample;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.CompactConstructorDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class VoidVisitorOrdered extends VoidVisitorAdapter<Void> {
    SymbolTable stack;

    public VoidVisitorOrdered(SymbolTable stack){
        this.stack = stack;
    }

    @Override
    public void visit(final ClassOrInterfaceDeclaration n, final Void arg) {
        stack.pre_scope(n);
        n.getExtendedTypes().forEach(p -> p.accept(this, arg));
        n.getImplementedTypes().forEach(p -> p.accept(this, arg));
        n.getTypeParameters().forEach(p -> p.accept(this, arg));
        n.getMembers().forEach(p -> p.accept(this, arg));
        n.getModifiers().forEach(p -> p.accept(this, arg));
        n.getName().accept(this, arg);
        n.getAnnotations().forEach(p -> p.accept(this, arg));
        n.getComment().ifPresent(l -> l.accept(this, arg));
        stack.post_scope(n);
    }

    @Override
    public void visit(LocalClassDeclarationStmt n, final Void arg){
        super.visit(n, arg);
    }
    @Override
    public void visit(LocalRecordDeclarationStmt n, final Void arg){
        super.visit(n, arg);
    }
    @Override
    public void visit(final BlockStmt n, final Void arg) {
        stack.pre_scope(n);
        super.visit(n, arg);
        stack.post_scope(n);
    }

    @Override
    public void visit(IfStmt n, final Void arg){
        n.getCondition().accept(this, arg);
        stack.pre_scope(n);
        n.getElseStmt().ifPresent(l -> l.accept(this, arg));
        stack.pre_scope(n);
        n.getThenStmt().accept(this, arg);
        stack.post_scope(n);
        n.getComment().ifPresent(l -> l.accept(this, arg));
    }
    @Override
    public void visit(CatchClause n, final Void arg){
        stack.pre_scope(n);
        n.getParameter().accept(this, arg);
        n.getComment().ifPresent(l -> l.accept(this, arg));
        n.getBody().accept(this, arg);
        stack.post_scope(n);
    }
    @Override
    public void visit(ForEachStmt n, final Void arg){
        stack.pre_scope(n);
        n.getIterable().accept(this, arg);
        n.getVariable().accept(this, arg);
        n.getComment().ifPresent(l -> l.accept(this, arg));
        n.getBody().accept(this, arg);
        stack.post_scope(n);
    }
    @Override
    public void visit(ForStmt n, final Void arg){
        stack.pre_scope(n);
        n.getInitialization().forEach(p -> p.accept(this, arg));
        n.getCompare().ifPresent(l -> l.accept(this, arg));
        n.getUpdate().forEach(p -> p.accept(this, arg));
        n.getComment().ifPresent(l -> l.accept(this, arg));
        n.getBody().accept(this, arg);
        stack.post_scope(n);
    }
    @Override
    public void visit(ConditionalExpr n, final Void arg){
        n.getCondition().accept(this, arg);
        stack.pre_scope(n);
        n.getThenExpr().accept(this, arg);
        stack.post_scope(n);
        stack.pre_scope(n);
        n.getElseExpr().accept(this, arg);
        stack.post_scope(n);
        n.getComment().ifPresent(l -> l.accept(this, arg));
    }
    @Override
    public void visit(ConstructorDeclaration n, final Void arg){
        stack.pre_scope(n);
        n.getModifiers().forEach(p -> p.accept(this, arg));
        n.getName().accept(this, arg);
        n.getParameters().forEach(p -> p.accept(this, arg));
        n.getReceiverParameter().ifPresent(l -> l.accept(this, arg));
        n.getThrownExceptions().forEach(p -> p.accept(this, arg));
        n.getTypeParameters().forEach(p -> p.accept(this, arg));
        n.getAnnotations().forEach(p -> p.accept(this, arg));
        n.getComment().ifPresent(l -> l.accept(this, arg));
        n.getBody().accept(this, arg);
        stack.post_scope(n);
    }
    @Override
    public void visit(MethodDeclaration n, final Void arg){
        stack.pre_scope(n);
        n.getReceiverParameter().ifPresent(l -> l.accept(this, arg));
        n.getParameters().forEach(p -> p.accept(this, arg));
        n.getBody().ifPresent(l -> l.accept(this, arg));
        n.getType().accept(this, arg);
        n.getModifiers().forEach(p -> p.accept(this, arg));
        n.getName().accept(this, arg);
        n.getThrownExceptions().forEach(p -> p.accept(this, arg));
        n.getTypeParameters().forEach(p -> p.accept(this, arg));
        n.getAnnotations().forEach(p -> p.accept(this, arg));
        n.getComment().ifPresent(l -> l.accept(this, arg));
        stack.post_scope(n);
    }
    @Override
    public void visit(LambdaExpr n, final Void arg){
        stack.pre_scope(n);
        n.getParameters().forEach(p -> p.accept(this, arg));
        n.getBody().accept(this, arg);
        n.getComment().ifPresent(l -> l.accept(this, arg));
        stack.post_scope(n);
    }
    @Override
    public void visit(CompactConstructorDeclaration n, final Void arg){
        stack.pre_scope(n);
        n.getModifiers().forEach(p -> p.accept(this, arg));
        n.getName().accept(this, arg);
        n.getThrownExceptions().forEach(p -> p.accept(this, arg));
        n.getTypeParameters().forEach(p -> p.accept(this, arg));
        n.getAnnotations().forEach(p -> p.accept(this, arg));
        n.getComment().ifPresent(l -> l.accept(this, arg));
        n.getBody().accept(this, arg);
        stack.post_scope(n);
    }



    
}
