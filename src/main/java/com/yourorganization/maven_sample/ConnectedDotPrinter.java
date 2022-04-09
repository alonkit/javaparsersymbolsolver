package com.yourorganization.maven_sample;

import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.metamodel.NodeMetaModel;
import com.github.javaparser.metamodel.PropertyMetaModel;

public class ConnectedDotPrinter {
    private HashMap<node_keys,node_keys> connections;
    private HashMap<node_keys,String> nodeToName;
    private int nodeCount;
    private final boolean outputNodeType;

    public ConnectedDotPrinter(boolean outputNodeType,  HashMap<node_keys,node_keys> connections){
        this.outputNodeType = outputNodeType;
        nodeCount =0;
        this.nodeToName = new HashMap<node_keys,String>();
        this.connections = connections;
    }

    public String output(List<? extends Node> nodes){
        StringBuilder output = new StringBuilder();
        StringBuilder heads = new StringBuilder("\n{rank = same; ");
        output.append("digraph {");
        for (Node node : nodes) {
            heads.append(nextNodeName()+"; ");
            nodeCount--;
            output(node, null, "root", output);
        }
        connections.forEach((k,v)->{
            String k_name = nodeToName.get(k);
            String v_name = nodeToName.get(v);

            if(k_name != null && v_name != null){
                System.out.println('x');

                output.append("\n" + k_name + " -> "+ v_name+ " [color = \"red\"]");
            }
        });
        heads.append("}");
        output.append(heads.toString());
        output.append("\n" + "}");
        return output.toString();

    } 

    public String output(Node node) {
        nodeCount = 0;
        StringBuilder output = new StringBuilder();
        output.append("digraph {");
        output(node, null, "root", output);
        connections.forEach((k,v)->{
            String k_name = nodeToName.get(k);
            String v_name = nodeToName.get(v);

            if(k_name != null && v_name != null){
                output.append("\n" + k_name + " -> "+ v_name+ " [color = \"red\"]");
                System.out.println('x');
            }
        });
        output.append("\n" + "}");
        return output.toString();
    }

    public void output(Node node, String parentNodeName, String name, StringBuilder builder) {
        NodeMetaModel metaModel = node.getMetaModel();
        List<PropertyMetaModel> allPropertyMetaModels = metaModel.getAllPropertyMetaModels();
        List<PropertyMetaModel> attributes = allPropertyMetaModels.stream().filter(PropertyMetaModel::isAttribute)
                .filter(PropertyMetaModel::isSingular).collect(toList());
        List<PropertyMetaModel> subNodes = allPropertyMetaModels.stream().filter(PropertyMetaModel::isNode)
                .filter(PropertyMetaModel::isSingular).collect(toList());
        List<PropertyMetaModel> subLists = allPropertyMetaModels.stream().filter(PropertyMetaModel::isNodeList)
                .collect(toList());

        String ndName = nextNodeName();
        if(! (node instanceof LineComment)){
            nodeToName.put( new node_keys(node), ndName);

        }

        if (outputNodeType)
            builder.append("\n" + ndName + " [label=\"" + escape(name) + " (" + metaModel.getTypeName()
                    + ")\"];");
        else
            builder.append("\n" + ndName + " [label=\"" + escape(name) + "\"];");

        if (parentNodeName != null)
            builder.append("\n" + parentNodeName + " -> " + ndName + ";");

        for (PropertyMetaModel a : attributes) {
            String attrName = nextNodeName();
            builder.append("\n" + attrName + " [label=\"" + escape(a.getName()) + "='"
                    + escape(a.getValue(node).toString()) + "'\"];");
            builder.append("\n" + ndName + " -> " + attrName + ";");

        }

        for (PropertyMetaModel sn : subNodes) {
            Node nd = (Node) sn.getValue(node);
            if (nd != null)
                output(nd, ndName, sn.getName(), builder);
        }

        for (PropertyMetaModel sl : subLists) {
            NodeList<? extends Node> nl = (NodeList<? extends Node>) sl.getValue(node);
            if (nl != null && nl.isNonEmpty()) {
                String ndLstName = nextNodeName();
                builder.append("\n" + ndLstName + " [label=\"" + escape(sl.getName()) + "\"];");
                builder.append("\n" + ndName + " -> " + ndLstName + ";");
                String slName = sl.getName().substring(0, sl.getName().length() - 1);
                for (Node nd : nl)
                    output(nd, ndLstName, slName, builder);
            }
        }
    }

    private String nextNodeName() {
        return "n" + (nodeCount++);
    }

    private static String escape(String value) {
        return value.replace("\"", "\\\"");
    }
    
}
