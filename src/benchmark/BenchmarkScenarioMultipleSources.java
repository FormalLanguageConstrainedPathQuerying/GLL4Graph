package benchmark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BenchmarkScenarioMultipleSources extends BenchmarkScenario {

    private final List<List<Integer>> startVerticesChunks = new ArrayList<>();

    public BenchmarkScenarioMultipleSources(String path) {
        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNextLine()) {
                startVerticesChunks.add(Arrays
                        .stream(scanner.nextLine().split(" "))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()));
            }
        }
    }

    @Override
    public String getCsvHeader() {
        return "chunk_index,prepare_time,run_time,answer";
    }

    @Override
    public String getCsvRecord(int chunkIndex, double stepPrepareTime, double stepRunTime, long result) {
        return chunkIndex + "," + stepPrepareTime + "," + stepRunTime + "," + result;
    }

    @Override
    public List<List<Integer>> getStartVerticesChunks() {
        return startVerticesChunks;
    }

    @Override
    public String toString() {
        return "MS";
    }
}
