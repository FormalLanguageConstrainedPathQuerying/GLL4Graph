package benchmark;

import iguana.utils.input.Edge;
import iguana.utils.input.GraphInput;
import iguana.utils.input.InMemGraphInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class BenchmarkGraphInMemory extends BenchmarkGraphStorage {

    private List<Integer> finalVertices = null;
    private final List<List<Edge>> adjacencyList = new ArrayList<>();

    @Override
    public void loadGraph(String path) throws IOException {
        try (var lines = Files.lines(Paths.get(path + File.separator + "nodes.csv"))) {
            finalVertices = lines.skip(1).map(Integer::parseInt).collect(Collectors.toList());
        }
        try (var lines = Files.lines(Paths.get(path + File.separator + "edges.csv"))) {
            var graph = lines
                    .skip(1)
                    .map(it -> it.split(" "))
                    .flatMap(it -> Stream.of(it, new String[]{it[2], it[1] + "_r", it[0]}))
                    .collect(
                            groupingBy(e -> Integer.parseInt(e[0]),
                                    mapping(e -> new Edge(e[1], Integer.parseInt(e[2])), toList()))
                    );
            int nodesCount = finalVertices.size();
            for (var i = 0; i < nodesCount; i++) {
                adjacencyList.add(graph.getOrDefault(i, new ArrayList<>()));
            }
        }
    }

    @Override
    public GraphInput getGraphInput(Stream<Integer> startVertices) {
        return new InMemGraphInput(adjacencyList, startVertices, finalVertices);
    }

    @Override
    protected void close() {
    }

    @Override
    public String toString() {
        return "INMEM";
    }
}
