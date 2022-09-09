package benchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import java.util.Properties;

public class BenchmarkSettings {

    public static BenchmarkSettings parseCli(CommandLine cmd) throws ParseException {
        Properties scenarioProperties = cmd.getOptionProperties(CliParser.SCENARIO_OPT);
        return new BenchmarkSettings(
                GraphStorage.valueOf(cmd.getOptionValue(CliParser.GRAPH_STORAGE_OPT)),
                Problem.valueOf(cmd.getOptionValue(CliParser.PROBLEM_OPT)),
                new ScenarioSettings(
                        Scenario.valueOf(scenarioProperties.getProperty(CliParser.SCENARIO_TYPE_PROP)),
                        scenarioProperties.getProperty(CliParser.SCENARIO_ARG_PROP)),
                cmd.getOptionValue(CliParser.GRAMMAR_OPT),
                cmd.getOptionValue(CliParser.GRAPH_OPT),
                cmd.getOptionValue(CliParser.DATASET_OPT),
                Integer.parseInt(cmd.getOptionValue(CliParser.WARMUP_ITERATIONS_OPT)),
                Integer.parseInt(cmd.getOptionValue(CliParser.MEASUREMENT_ITERATIONS_OPT)));

    }

    private final GraphStorage storageType;
    private final Problem problem;
    private final ScenarioSettings scenario;
    private final String datasetName;
    private final String grammarPath;
    private final String graphPath;
    private final int warmupIterations;
    private final int measurementIterations;

    private BenchmarkSettings(GraphStorage storageType,
                              Problem problem,
                              ScenarioSettings scenario,
                              String grammarPath,
                              String graphPath,
                              String datasetName,
                              int warmupIterations,
                              int measurementIterations) {
        this.storageType = storageType;
        this.problem = problem;
        this.scenario = scenario;
        this.datasetName = datasetName;
        this.grammarPath = grammarPath;
        this.graphPath = graphPath;
        this.warmupIterations = warmupIterations;
        this.measurementIterations = measurementIterations;
    }

    public GraphStorage getStorageType() {
        return storageType;
    }

    public Problem getProblem() {
        return problem;
    }

    public ScenarioSettings getScenario() {
        return scenario;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getGrammarPath() {
        return grammarPath;
    }

    public String getGraphPath() {
        return graphPath;
    }

    public int getWarmupIterations() {
        return warmupIterations;
    }

    public int getMeasurementIterations() {
        return measurementIterations;
    }

    public static class ScenarioSettings {

        private final Scenario scenario;
        private final int nodesCount;
        private final String path;

        public ScenarioSettings(Scenario scenario, String optionArgument) {
            this.scenario = scenario;
            this.path = switch (scenario) {
                case ALL_PAIRS -> null;
                case MULTIPLE_SOURCES -> optionArgument;
            };
            this.nodesCount = switch (scenario) {
                case ALL_PAIRS -> Integer.parseInt(optionArgument);
                case MULTIPLE_SOURCES -> 0;
            };
        }

        public Scenario getScenario() {
            return scenario;
        }

        public int getNodesCount() {
            return nodesCount;
        }

        public String getPath() {
            return path;
        }
    }

}
