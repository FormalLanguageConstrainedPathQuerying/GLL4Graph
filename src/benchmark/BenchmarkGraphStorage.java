package benchmark;

import iguana.utils.input.GraphInput;

import java.io.IOException;
import java.util.stream.Stream;

public abstract class BenchmarkGraphStorage {

    public static BenchmarkGraphStorage createBenchmarkStorage(GraphStorage storageType) {
        return switch (storageType) {
            case NEO4J -> new BenchmarkGraphNeo4j();
            case IN_MEMORY -> new BenchmarkGraphInMemory();
        };
    }

    public abstract void loadGraph(String path) throws IOException;

    public abstract GraphInput getGraphInput(Stream<Integer> startVertices);

    protected void onIterationStart() {
    }

    protected void onIterationFinish() {
    }

    protected abstract void close();

}
