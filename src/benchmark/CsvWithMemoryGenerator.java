package benchmark;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryType;

public class CsvWithMemoryGenerator extends CsvGenerator {
    public CsvWithMemoryGenerator(BenchmarkScenario scenario, String resultDir, String datasetName, String problem, String graphStorage) {
        super(scenario, resultDir, datasetName, problem, graphStorage);
    }

    @Override
    public String getFilename() {
        return super.getFilenamePart() + "_MEMORY.csv";
    }

    @Override
    public String getHeader() {
        return super.getHeader() + ",heap_mem,non_heap_mem";
    }

    @Override
    public String getRecord(int chunkIndex, double stepPrepareTime, double stepRunTime, long result) {
        var heapMem = 0L;
        var nonHeapMem = 0L;
        var pools = ManagementFactory.getMemoryPoolMXBeans();
        for (var memoryPoolMXBean: pools) {
            var peakUsed = memoryPoolMXBean.getPeakUsage().getUsed();
            if (memoryPoolMXBean.getType() == MemoryType.HEAP) {
                heapMem += peakUsed;
            } else {
                nonHeapMem += peakUsed;
            }
        }
        return super.getRecord(chunkIndex, stepPrepareTime, stepRunTime, result) + "," + heapMem + "," + nonHeapMem;
    }
}
