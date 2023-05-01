package com.anhtuan210501.myapplication;

import java.util.ArrayList;
import java.util.Iterator;

public class Graph {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    public Graph(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
    public void setEdgeWeight(int index, double weight) {
        edges.get(index).setWeight(weight);
    }
    public void removeEdges(Node startNode, Node endNode) {
        Iterator<Edge> iter = edges.iterator();
        while (iter.hasNext()) {
            Edge edge = iter.next();
            if (edge.getStartNode().equals(startNode) && edge.getEndNode().equals(endNode) ||
                    edge.getStartNode().equals(endNode) && edge.getEndNode().equals(startNode)) {
                iter.remove();
            }
        }
    }
}
