package benchmark;

import iguana.utils.input.GraphInput;
import org.iguana.parser.IguanaParser;
import org.iguana.parser.Pair;
import org.iguana.parser.ParseOptions;
import org.iguana.sppf.NonterminalNode;

import java.util.Map;
import java.util.stream.Stream;

public class BenchmarkProblemAllPaths extends BenchmarkProblem {

    private Map<Pair, NonterminalNode> parseResults = null;
    @Override
    public void runAlgo(IguanaParser parser, GraphInput input) {
        parseResults = parser.getSPPF(input);
    }

    @Override
    public long getResult() {
        return parseResults.size();
    }

    @Override
    public void clearResult() { parseResults = null; }

    @Override
    public String toString() {
        return "SPPF";
    }

}
