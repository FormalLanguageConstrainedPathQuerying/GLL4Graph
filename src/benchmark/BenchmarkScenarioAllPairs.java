package benchmark;

import org.eclipse.collections.impl.list.Interval;

import java.util.List;

public class BenchmarkScenarioAllPairs extends BenchmarkScenario {

    private final int nodesCount;

    public BenchmarkScenarioAllPairs(int nodesCount) {
        this.nodesCount = nodesCount;
    }

    @Override
    public String getCsvHeader() {
        return "prepare_time,run_time,answer";
    }

    @Override
    public String getCsvRecord(int chunkIndex, double stepPrepareTime, double stepRunTime, long result) {
        return stepPrepareTime + "," + stepRunTime + "," + result;
    }

    @Override
    public List<List<Integer>> getStartVerticesChunks() {
        return List.of(Interval.zeroTo(nodesCount - 1));
    }

    @Override
    public String toString() {
        return "AP";
    }
}
