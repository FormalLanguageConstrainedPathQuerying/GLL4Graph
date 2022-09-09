package benchmark;

import java.util.List;

public abstract class BenchmarkScenario {

    public static BenchmarkScenario createBenchmarkScenario(BenchmarkSettings.ScenarioSettings scenario) {
        return switch (scenario.getScenario()) {
            case ALL_PAIRS -> new BenchmarkScenarioAllPairs(scenario.getNodesCount());
            case MULTIPLE_SOURCES -> new BenchmarkScenarioMultipleSources(scenario.getPath());
        };
    }

    public abstract String getCsvHeader();
    public abstract String getCsvRecord(int chunkIndex, double stepPrepareTime, double stepRunTime, long result);
    public abstract List<List<Integer>> getStartVerticesChunks();

}
