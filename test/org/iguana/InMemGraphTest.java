package org.iguana;

import iguana.utils.input.Edge;
import iguana.utils.input.GraphInput;
import iguana.utils.input.InMemGraphInput;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.parser.IguanaParser;
import org.iguana.parser.Pair;
import org.iguana.parser.ParseOptions;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Stream;

public class InMemGraphTest {

    private static final String TERM_SUBCLASS = "subClassOf";
    private static final String TERM_SUBCLASS_R = "subClassOf_r";
    private static final Map<String, String> GRAMMARS = new HashMap<>();

    static {
        GRAMMARS.put("g1", "test/resources/grammars/graph/g1/grammar.json");
        GRAMMARS.put("g2", "test/resources/grammars/graph/g2/grammar.json");
        GRAMMARS.put("Test4", "test/resources/grammars/graph/Test4/grammar.json");
        GRAMMARS.put("Test5", "test/resources/grammars/graph/Test5/grammar.json");
    }

    @Test
    public void testGraphInput() {
        List<List<Edge>> edges = Arrays.asList(
                List.of(
                        new Edge(TERM_SUBCLASS, 1),
                        new Edge(TERM_SUBCLASS_R, 3)
                ),
                Collections.singletonList(
                        new Edge(TERM_SUBCLASS, 2)
                ),
                Collections.singletonList(
                        new Edge(TERM_SUBCLASS, 0)
                ),
                Collections.singletonList(
                        new Edge(TERM_SUBCLASS_R, 0)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1, 2, 3),
                List.of(0, 1, 2, 3)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load(GRAMMARS.get("g1"), "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        Map<Pair, ParseTreeNode> node = parser.getParserTree(input, new ParseOptions.Builder().setAmbiguous(true).build());

        Set<Pair> expectedReachableVertices = Set.of(
                new Pair(3, 2),
                new Pair(0, 0),
                new Pair(0, 1),
                new Pair(0, 2),
                new Pair(3, 0),
                new Pair(3, 1)
        );
        Set<Pair> actualReachableVertices = node.keySet();
        assertTrue(expectedReachableVertices.size() == actualReachableVertices.size()
                && expectedReachableVertices.containsAll(actualReachableVertices)
                && actualReachableVertices.containsAll(expectedReachableVertices));
    }


    @Test
    public void testGraphReachability1() {
        List<List<Edge>> edges = Arrays.asList(
                Collections.singletonList(
                        new Edge("a", 1)
                ),
                Collections.singletonList(
                        new Edge("a", 2)
                ),
                Collections.singletonList(
                        new Edge("a", 3)
                ),
                Collections.singletonList(
                        new Edge("a", 3)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1, 2, 3),
                List.of(0, 1, 2, 3)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load(GRAMMARS.get("Test4"), "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());

        assertNotNull(parseResults);
        Set<Pair> expectedReachableVertices = Set.of(
                new Pair(3, 3),
                new Pair(0, 1),
                new Pair(1, 2),
                new Pair(2, 3),
                new Pair(0, 2),
                new Pair(1, 3),
                new Pair(0, 3)
        );
        Set<Pair> actualReachableVertices = parseResults.keySet();
        assertTrue(expectedReachableVertices.size() == actualReachableVertices.size()
                && expectedReachableVertices.containsAll(actualReachableVertices)
                && actualReachableVertices.containsAll(expectedReachableVertices));
    }

    @Test
    public void testGraphReachability5() {
        List<List<Edge>> edges = Arrays.asList(
                Collections.singletonList(
                        new Edge("a", 1)
                ),
                Collections.singletonList(
                        new Edge("a", 1)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1),
                Arrays.asList(0, 1)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load(GRAMMARS.get("Test4"), "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());

        assertNotNull (parseResults);
        Set<Pair> expectedReachableVertices = Set.of(
                new Pair(1, 1),
                new Pair(0, 1)
        );
        Set<Pair> actualReachableVertices = parseResults.keySet();
        assertTrue(expectedReachableVertices.size() == actualReachableVertices.size()
                && expectedReachableVertices.containsAll(actualReachableVertices)
                && actualReachableVertices.containsAll(expectedReachableVertices));
    }

    @Test
    public void testGraphReachability6() {
        List<List<Edge>> edges = List.of(
                Arrays.asList(
                        new Edge(TERM_SUBCLASS, 0),
                        new Edge(TERM_SUBCLASS_R, 0)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0),
                List.of(0)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load(GRAMMARS.get("g1"), "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());
        
        assertNotNull (parseResults);
        Set<Pair> expectedReachableVertices = Set.of(
                new Pair(0, 0)
        );
        Set<Pair> actualReachableVertices = parseResults.keySet();
        assertTrue(expectedReachableVertices.size() == actualReachableVertices.size()
                && expectedReachableVertices.containsAll(actualReachableVertices)
                && actualReachableVertices.containsAll(expectedReachableVertices));
    }

    @Test
    public void testGraphReachability10() {
        List<List<Edge>> edges = List.of(
                List.of(
                        new Edge(TERM_SUBCLASS, 2)
                ),
                List.of(
                        new Edge(TERM_SUBCLASS_R, 1),
                        new Edge(TERM_SUBCLASS, 0)
                ),
                List.of(
                        new Edge(TERM_SUBCLASS, 1)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1, 2),
                List.of(0, 1, 2)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load(GRAMMARS.get("g1"), "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());

        assertNotNull (parseResults);
        Set<Pair> expectedReachableVertices = Set.of(
                new Pair(1, 0),
                new Pair(1, 1),
                new Pair(1, 2)
        );
        Set<Pair> actualReachableVertices = parseResults.keySet();
        assertTrue(expectedReachableVertices.size() == actualReachableVertices.size()
                && expectedReachableVertices.containsAll(actualReachableVertices)
                && actualReachableVertices.containsAll(expectedReachableVertices));
    }

    @Test
    public void testGraphReachability9() {
        List<List<Edge>> edges = List.of(
                List.of(
                        new Edge("a", 1)
                ),
                List.of(
                        new Edge("a", 2),
                        new Edge("a", 5),
                        new Edge("b", 8)
                ),
                List.of(
                        new Edge("b", 3)
                ),
                List.of(
                        new Edge("b", 4)
                ),
                List.of(),
                List.of(
                        new Edge("b", 6)
                ),
                List.of(
                        new Edge("a", 7)
                ),
                List.of(
                        new Edge("b", 3)
                ),
                List.of(
                        new Edge("a", 9)
                ),
                List.of(
                        new Edge("b", 10)
                ),
                List.of(
                        new Edge("a", 3)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load(GRAMMARS.get("Test5"), "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());

        assertNotNull (parseResults);
        Set<Pair> expectedReachableVertices = Set.of(
                new Pair(1, 3),
                new Pair(8, 10),
                new Pair(0, 4),
                new Pair(1, 6),
                new Pair(0, 8),
                new Pair(0, 10),
                new Pair(10, 4),
                new Pair(8, 4),
                new Pair(6, 3)
        );
        Set<Pair> actualReachableVertices = parseResults.keySet();
        assertTrue(expectedReachableVertices.size() == actualReachableVertices.size()
                && expectedReachableVertices.containsAll(actualReachableVertices)
                && actualReachableVertices.containsAll(expectedReachableVertices));
    }

    @Test
    public void testGraphReachability7() {
        List<List<Edge>> edges = Arrays.asList(
                List.of(
                        new Edge(TERM_SUBCLASS, 1)
                ),
                List.of(
                        new Edge(TERM_SUBCLASS, 2)
                ),
                Arrays.asList(
                        new Edge(TERM_SUBCLASS_R, 3),
                        new Edge(TERM_SUBCLASS_R, 3)
                ),
                List.of(
                        new Edge(TERM_SUBCLASS, 4)
                ),
                List.of()
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1, 2, 3, 4),
                List.of(0, 1, 2, 3, 4)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load(GRAMMARS.get("g1"), "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());

        assertNotNull (parseResults);
        Set<Pair> expectedReachableVertices = Set.of(
                new Pair(2, 4)
        );
        Set<Pair> actualReachableVertices = parseResults.keySet();
        assertTrue(expectedReachableVertices.size() == actualReachableVertices.size()
                && expectedReachableVertices.containsAll(actualReachableVertices)
                && actualReachableVertices.containsAll(expectedReachableVertices));
    }

    @Test
    public void testGraphReachability8() {
        List<List<Edge>> edges = Arrays.asList(
                List.of(
                        new Edge(TERM_SUBCLASS, 1)
                ),
                List.of(
                        new Edge(TERM_SUBCLASS, 2)
                ),
                List.of(
                        new Edge(TERM_SUBCLASS, 3)
                ),
                List.of(
                        new Edge(TERM_SUBCLASS_R, 4)
                ),
                List.of(
                        new Edge(TERM_SUBCLASS_R, 5)
                ),
                List.of(
                        new Edge(TERM_SUBCLASS, 6)
                ),
                List.of(
                        new Edge(TERM_SUBCLASS_R, 6)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1, 2, 3, 4, 5, 6),
                List.of(0, 1, 2, 3, 4, 5, 6)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load(GRAMMARS.get("g1"), "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());

        assertNotNull (parseResults);
        Set<Pair> expectedReachableVertices = Set.of(
                new Pair(4, 6)
        );
        Set<Pair> actualReachableVertices = parseResults.keySet();
        assertTrue(expectedReachableVertices.size() == actualReachableVertices.size()
                && expectedReachableVertices.containsAll(actualReachableVertices)
                && actualReachableVertices.containsAll(expectedReachableVertices));
    }

    @Test
    @Disabled()
    public void testGraphInput2() {
        List<List<Edge>> edges = Arrays.asList(
                Collections.singletonList(
                        new Edge(TERM_SUBCLASS, 1)
                ),
                Collections.singletonList(
                        new Edge(TERM_SUBCLASS, 1)
                )
        );
        Input input = new InMemGraphInput(
                edges,
                Stream.of(0),
                Collections.singletonList(1)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load(GRAMMARS.get("g2"), "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);
        ParseTreeNode parseTreeNode = parser.getParserTree(input, new ParseOptions.Builder().setAmbiguous(true).build());
    }

    @Test
    public void testGraphReachability3() {
        List<List<Edge>> edges = Arrays.asList(
                Collections.singletonList(
                        new Edge("a", 1)
                ),
                Collections.singletonList(
                        new Edge("b", 1)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1),
                Arrays.asList(0, 1)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load(GRAMMARS.get("Test5"), "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());

        assertNotNull (parseResults);
        Set<Pair> expectedReachableVertices = Set.of(
                new Pair(0, 1)
        );
        Set<Pair> actualReachableVertices = parseResults.keySet();
        assertTrue(expectedReachableVertices.size() == actualReachableVertices.size()
                && expectedReachableVertices.containsAll(actualReachableVertices)
                && actualReachableVertices.containsAll(expectedReachableVertices));
    }

    @Test
    public void testGraphReachability4() {
        List<List<Edge>> edges = Arrays.asList(
                Collections.singletonList(
                        new Edge("a", 1)
                ),
                Collections.emptyList()
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1),
                Arrays.asList(0, 1)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load(GRAMMARS.get("Test4"), "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());

        assertNotNull (parseResults);
        Set<Pair> expectedReachableVertices = Set.of(
                new Pair(0, 1)
        );
        Set<Pair> actualReachableVertices = parseResults.keySet();
        assertTrue(expectedReachableVertices.size() == actualReachableVertices.size()
                && expectedReachableVertices.containsAll(actualReachableVertices)
                && actualReachableVertices.containsAll(expectedReachableVertices));
    }
}
