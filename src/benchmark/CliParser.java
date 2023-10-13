package benchmark;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CliParser {

    public static final String GRAPH_STORAGE_OPT = "gs";
    public static final String PROBLEM_OPT = "p";
    public static final String SCENARIO_OPT = "S";
    public static final String SCENARIO_TYPE_PROP = "s";
    public static final String SCENARIO_ARG_PROP = "a";
    public static final String DATASET_OPT = "d";
    public static final String GRAMMAR_OPT = "gm";
    public static final String GRAPH_OPT = "gp";
    public static final String WARMUP_ITERATIONS_OPT = "w";
    public static final String MEASUREMENT_ITERATIONS_OPT = "m";
    public static final String MEMORY_USAGE = "mem";
    private final Option helpOption;
    private final Options allOptions;

    public CliParser() {
        helpOption = Option.builder("h")
                .desc("Print help message")
                .longOpt("help")
                .required(false)
                .build();
        Iterable<String> storageValues = Arrays.stream(GraphStorage.values()).map(Enum::toString).collect(Collectors.toList());
        Iterable<String> problemValues = Arrays.stream(Problem.values()).map(Enum::toString).collect(Collectors.toList());
        Iterable<String> scenarioValues = Arrays.stream(Scenario.values()).map(Enum::toString).collect(Collectors.toList());

        allOptions = new Options();
        allOptions.addOption(helpOption);
        allOptions.addOption(Option.builder(GRAPH_STORAGE_OPT)
                .longOpt("graph_storage")
                .argName("storage type")
                .hasArg()
                .desc("Graph storage type, allowed values: " + String.join(", ", storageValues))
                .optionalArg(false)
                .required(true)
                .build());
        allOptions.addOption(Option.builder(PROBLEM_OPT)
                .longOpt("problem")
                .argName("problem type")
                .hasArg(true)
                .desc("Benchmarking algorithm, allowed values: " + String.join(", ", problemValues))
                .optionalArg(false)
                .required(true)
                .build());
        allOptions.addOption(Option.builder(SCENARIO_OPT)
                .longOpt("scenario")
                .argName(SCENARIO_TYPE_PROP + "=value1 " + SCENARIO_ARG_PROP + "=value2")
                .hasArgs()
                .numberOfArgs(2)
                .valueSeparator('=')
                .desc("Benchmarking scenario and its argument, '"
                        + SCENARIO_TYPE_PROP + "' property allowed values: "
                        + String.join(", ", scenarioValues)
                        + ", '" + SCENARIO_ARG_PROP + "' property contains number of nodes if '" +
                        SCENARIO_TYPE_PROP + "' equals " + Scenario.ALL_PAIRS
                        + " or path to file with vertices chunks if '" +
                        SCENARIO_TYPE_PROP + "' equals " + Scenario.MULTIPLE_SOURCES
                )
                .optionalArg(false)
                .required(true)
                .build());
        allOptions.addOption(Option.builder(DATASET_OPT)
                .longOpt("dataset")
                .argName("dataset name")
                .hasArg(true)
                .desc("The name of the dataset, an important component of the file name with the results")
                .optionalArg(false)
                .required(true)
                .build());
        allOptions.addOption(Option.builder(GRAMMAR_OPT)
                .longOpt("grammar")
                .argName("path")
                .hasArg(true)
                .desc("Path to JSON file contains context-free grammar")
                .optionalArg(false)
                .required(true)
                .build());
        allOptions.addOption(Option.builder(GRAPH_OPT)
                .longOpt("graph")
                .argName("path")
                .hasArg(true)
                .desc("Path to directory contains files nodes.csv and edges.csv")
                .optionalArg(false)
                .required(true)
                .build());
        allOptions.addOption(Option.builder(WARMUP_ITERATIONS_OPT)
                .longOpt("warmup_iterations")
                .argName("number")
                .hasArg(true)
                .desc("Number of warm-up iterations")
                .optionalArg(false)
                .required(true)
                .build());
        allOptions.addOption(Option.builder(MEASUREMENT_ITERATIONS_OPT)
                .longOpt("measurement_iterations")
                .argName("number")
                .hasArg(true)
                .desc("Number of measurement iterations")
                .optionalArg(false)
                .required(true)
                .build());
        allOptions.addOption(Option.builder(MEMORY_USAGE)
                .longOpt("memory_usage")
                .hasArg(false)
                .desc("Whether to measure memory consumption")
                .required(false)
                .build());
    }

    public boolean hasHelp(String[] args) {
        final Options options = new Options();
        options.addOption(helpOption);
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd;
        try {
            cmd = parser.parse(options, args, true);
        } catch (ParseException e) {
            throw new RuntimeException("Cannot check arguments contain help option");
        }
        return cmd.hasOption(helpOption.getOpt());
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(150, "GraphBenchmark", "", allOptions, "", true);
    }

    public CommandLine parseArgs(String[] args) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        return parser.parse(allOptions, args);
    }
}
