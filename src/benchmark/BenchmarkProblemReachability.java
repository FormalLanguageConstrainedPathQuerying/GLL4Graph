package benchmark;

import iguana.utils.input.GraphInput;
import org.iguana.parser.IguanaParser;
import org.iguana.parser.Pair;
import org.iguana.parser.ParseOptions;

import java.util.stream.Stream;

public class BenchmarkProblemReachability extends BenchmarkProblem {

    private Stream<Pair> parseResults = null;

    @Override
    public void runAlgo(IguanaParser parser, GraphInput input) {
        parseResults = parser.getReachabilities(input,
                new ParseOptions
                        .Builder()
                        .setAmbiguous(false)
                        .build());
    }

    @Override
    public long getResult() {
        return parseResults.count();
    }

    @Override
    public String toString() {
        return "REACHABILITY";
    }

}
