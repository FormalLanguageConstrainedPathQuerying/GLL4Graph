package benchmark;

import iguana.utils.input.GraphInput;
import org.iguana.parser.IguanaParser;

public abstract class BenchmarkProblem {

    public static BenchmarkProblem createBenchmarkProblem(Problem problem) {
        return switch (problem) {
            case REACHABILITY -> new BenchmarkProblemReachability();
            case ALL_PATHS -> new BenchmarkProblemAllPaths();
        };
    }

    public abstract void runAlgo(IguanaParser parser, GraphInput input);

    public abstract long getResult();

    public abstract void clearResult();
}
