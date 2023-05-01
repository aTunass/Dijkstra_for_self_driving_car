package com.anhtuan210501.myapplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private HashMap<Node, Double> distances;
    private HashMap<Node, Node> previousNodes;
    private PriorityQueue<Node> unvisitedNodes;

    public Dijkstra(Graph graph) {
        this.nodes = graph.getNodes();
        this.edges = graph.getEdges();
        distances = new HashMap<>();
        previousNodes = new HashMap<>();
        unvisitedNodes = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return Double.compare(distances.getOrDefault(o1, Double.POSITIVE_INFINITY),
                        distances.getOrDefault(o2, Double.POSITIVE_INFINITY));
            }
        });
    }

    public void execute(Node startNode) {
        // Khởi tạo khoảng cách và đỉnh trước của các đỉnh
        for (Node node : nodes) {
            distances.put(node, Double.POSITIVE_INFINITY);
            previousNodes.put(node, null);
            unvisitedNodes.offer(node);
        }

        // Đặt khoảng cách của đỉnh bắt đầu là 0
        distances.put(startNode, 0.0);

        while (!unvisitedNodes.isEmpty()) {
            Node currentNode = unvisitedNodes.poll();
            double currentDistance = distances.get(currentNode);

            // Duyệt qua các đỉnh kề với đỉnh hiện tại
            for (Edge edge : edges) {
                if (edge.getStartNode().equals(currentNode)) {
                    Node neighborNode = edge.getEndNode();
                    double edgeWeight = edge.getWeight();
                    double neighborDistance = distances.get(neighborNode);

                    // Nếu khoảng cách từ đỉnh hiện tại đến đỉnh kề nhỏ hơn khoảng cách hiện tại của đỉnh kề
                    if (currentDistance + edgeWeight < neighborDistance) {
                        distances.put(neighborNode, currentDistance + edgeWeight);
                        previousNodes.put(neighborNode, currentNode);
                        // Cực kỳ quan trọng
                        unvisitedNodes.remove(neighborNode);
                        unvisitedNodes.offer(neighborNode);
                    }
                }
            }
        }
    }

    public List<Node> getPath(Node endNode) {
        List<Node> path = new ArrayList<>();

        // Truy vết từ đỉnh kết thúc đến đỉnh bắt đầu
        Node currentNode = endNode;
        while (previousNodes.containsKey(currentNode)) {
            path.add(currentNode);
            currentNode = previousNodes.get(currentNode);
        }
        path.add(currentNode);
        Collections.reverse(path);

        return path;
    }
    public double getTotalDistance(Node endNode) {
        return distances.get(endNode);
    }
}
