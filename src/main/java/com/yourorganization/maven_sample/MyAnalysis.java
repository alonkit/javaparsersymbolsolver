package com.yourorganization.maven_sample;
import java.util.*;
import java.util.stream.Collectors;

import javax.print.attribute.standard.MediaSize.NA;
import javax.swing.text.AbstractDocument.Content;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.resolution.declarations.AssociableToAST;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.ast.*;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFactory;
import com.github.javaparser.symbolsolver.javaparsermodel.contexts.ClassOrInterfaceDeclarationContext;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserVariableDeclaration;
import com.github.javaparser.symbolsolver.resolution.MethodResolutionLogic;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import com.google.common.cache.Cache;


/**
 * Some code that uses JavaSymbolSolver.
 *//*
public class MyAnalysis {

    public static void main(String[] args) {
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        // Parse some code
        CompilationUnit cu = StaticJavaParser.parse("class X { int x() { return 1 + 1.0 - 5; } }");

        // Find all the calculations with two sides:
        cu.findAll(BinaryExpr.class).forEach(be -> {
            // Find out what type it has:
            ResolvedType resolvedType = be.calculateResolvedType();

            // Show that it's "double" in every case:
            System.out.println(be.toString() + " is a: " + resolvedType);
        });
    }
}*/

public class MyAnalysis {


    private static final String BASE_PATH = "testcase/base.java";
    private static final String DERIVED_PATH = "testcase/derived.java";
    private static CombinedTypeSolver typeSolver;
    public static void main(String[] args) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, IOException {
        typeSolver = new CombinedTypeSolver();
        typeSolver.add( new ReflectionTypeSolver());
        JavaParserTypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File("testcase"));
        typeSolver.add(javaParserTypeSolver  );

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        CompilationUnit cu = StaticJavaParser.parse(new File(BASE_PATH));
        
        HashMap<node_keys, node_keys> connections = new HashMap<node_keys, node_keys>();


        connectionFinder(cu, connections);
        cu = StaticJavaParser.parse(new File(DERIVED_PATH));
        connectionFinder(cu, connections);
        
        // JavaParserFieldDeclaration
        // JavaParserVariableDeclaration

        Field parsedFilesField = JavaParserTypeSolver.class.getDeclaredField("parsedFiles");
        parsedFilesField.setAccessible(true);
        Cache<Path, Optional<CompilationUnit>>files =
        (Cache<Path, Optional<CompilationUnit>>) parsedFilesField.get(javaParserTypeSolver);

        List<CompilationUnit> lst = files.asMap().values().stream()
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());

        ConnectedDotPrinter printer = new ConnectedDotPrinter(true, connections);

        FileWriter myWriter = new FileWriter("graph.dot");
        myWriter.write(printer.output(lst));
        myWriter.close();
        
        // IdentityHashMap<Node, Node> connections = new IdentityHashMap<Node, Node>();

        // VoidVisitorVars visitor = new VoidVisitorVars(new SymbolTable(), connections);
        
        // cu.accept(visitor, null);
        // ConnectedDotPrinter printer =new ConnectedDotPrinter(true, connections) ;

        // try {
        //     FileWriter myWriter = new FileWriter("graph.dot");
        //     myWriter.write(printer.output(cu));
        //     myWriter.close();
        //     System.out.println("Successfully wrote to the file.");
        // } catch (IOException e) {
        //     System.out.println("An error occurred.");
        //     e.printStackTrace();
        // }
        

        // System.out.println(printer.output(cu));
        
    }


    public static node_keys createNodeKeys(Node n){
        return new node_keys(n);
    }

    // JavaParserTypeDeclarationAdapter
    public static void connectionFinder(CompilationUnit cu, HashMap<node_keys, node_keys> connections){
        
        cu.findAll(MethodDeclaration.class).forEach(mce -> {
            try{
                ClassOrInterfaceDeclaration typeDeclaration =(ClassOrInterfaceDeclaration) mce.getParentNode().get();

                MethodDeclaration res = findOverriden(mce, typeDeclaration).toAst().get();
                connections.put(createNodeKeys(mce) , createNodeKeys(res) );
                
                System.out.println(res.getName());
            }catch(Exception e){}
            
        });

        // new javaParserTypeDeclarationAdapter();

        cu.findAll(MethodCallExpr.class).forEach(mce -> {
            try{
                
                MethodDeclaration res = mce.resolve().toAst().get(); 
                connections.put(createNodeKeys(mce) , createNodeKeys(res) );
                
                System.out.println(res.getName());
            }catch(Exception e){}
            
        });

        cu.findAll(NameExpr.class).forEach(ne -> {
            try{
                
                ResolvedValueDeclaration res = ne.resolve(); 
                if(res instanceof AssociableToAST<?>){
                    
                    connections.put(createNodeKeys(ne), createNodeKeys(((AssociableToAST<?>) res).toAst().get()) );
                }
                // connections.put(createNodeKeys(mce) , createNodeKeys(res) );
                
                // System.out.println(res.getName());
            }catch(Exception e){}
            
        });
    }
    
    public static ResolvedMethodDeclaration  findOverriden(MethodDeclaration mce, ClassOrInterfaceDeclaration typeDeclaration){
    
        List<ResolvedMethodDeclaration> candidateMethods = new LinkedList<ResolvedMethodDeclaration>();
        List<ResolvedType> argumentsTypes = new LinkedList<>();
        for( Parameter param: mce.getParameters()){
            argumentsTypes.add(param.getType().resolve());
        }
        Boolean staticOnly = false;
        
        for (ResolvedReferenceType ancestor : typeDeclaration.resolve().getAncestors(true)) {
            Optional<ResolvedReferenceTypeDeclaration> ancestorTypeDeclaration = ancestor.getTypeDeclaration();
            
            // Avoid recursion on self
            if (ancestor.getTypeDeclaration().isPresent() && typeDeclaration != ancestorTypeDeclaration.get()) {
                // Consider methods declared on self
                candidateMethods.addAll(ancestor.getAllMethodsVisibleToInheritors()
                        .stream()
                        .filter(m -> m.getName().equals(mce.getName()))
                        .collect(Collectors.toList()));

                // consider methods from superclasses and only default methods from interfaces :
                // not true, we should keep abstract as a valid candidate
                // abstract are removed in MethodResolutionLogic.isApplicable is necessary
                SymbolReference<ResolvedMethodDeclaration> res = MethodResolutionLogic.solveMethodInType(ancestorTypeDeclaration.get(), mce.getName().getId(), argumentsTypes, staticOnly);
                if (res.isSolved()) {
                    candidateMethods.add(res.getCorrespondingDeclaration());
                }
            }
        }
        // Node parent = typeDeclaration.getParentNode().get();
        // if (candidateMethods.isEmpty() && parent instanceof ClassOrInterfaceDeclaration) {
        //     SymbolReference<ResolvedMethodDeclaration> parentSolution =((ClassOrInterfaceDeclaration) parent).resolve()
        //             .solveMethod(mce.getNameAsString(), argumentsTypes, staticOnly);
        //     if (parentSolution.isSolved()) {
        //         candidateMethods.add(parentSolution.getCorrespondingDeclaration());
        //     }
        // }

        return MethodResolutionLogic.findMostApplicable(candidateMethods, mce.getNameAsString(), argumentsTypes, typeSolver).getCorrespondingDeclaration();
    
    }
}
    
 