package benchmark;

import java.io.File;

public class CsvGenerator {

    private final String header;
    private final BenchmarkScenario scenario;
    private final String filename;

    public CsvGenerator(BenchmarkScenario scenario,
                        String resultDir,
                        String datasetName,
                        String problem,
                        String graphStorage) {
        this.scenario = scenario;
        this.header = scenario.getCsvHeader();

        this.filename = resultDir +
                File.separator +
                datasetName +
                '_' +
                scenario +
                '_' +
                problem +
                '_' +
                graphStorage;
    }

    protected String getFilenamePart() {
        return filename;
    }

    public String getFilename() {
        return filename + ".csv";
    }

    public String getHeader() {
        return header;
    }

    public String getRecord(int chunkIndex, double stepPrepareTime, double stepRunTime, long result) {
        return scenario.getCsvRecord(chunkIndex, stepPrepareTime, stepRunTime, result);
    }
}
