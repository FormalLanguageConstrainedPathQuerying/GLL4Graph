package org.iguana.parser.basic;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.term.Term;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Terminal;
import iguana.regex.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.term.TermFactory.*;
import static iguana.utils.collections.CollectionsUtil.*;
import static org.junit.Assert.*;

/**
 * E ::= E '+' E
 *     | 'a'
 */
public class Test18 {

    static Nonterminal E = Nonterminal.withName("E");
    static Terminal a = Terminal.from(Character.from('a'));
    static Terminal plus = Terminal.from(Character.from('+'));

    static Rule r1 = Rule.withHead(E).addSymbols(E, plus, E).build();
    static Rule r2 = Rule.withHead(E).addSymbols(a).build();

    public static Grammar grammar = Grammar.builder().addRules(r1, r2).build();
    private static Nonterminal startSymbol = E;

    private static Input input1 = Input.fromString("a+a");
    private static Input input2 = Input.fromString("a+a+a");
    private static Input input3 = Input.fromString("a+a+a+a+a");


    @Test
    public void testParser1() {
        GrammarGraph graph = GrammarGraph.from(grammar, input1);
        ParseResult result = Iguana.parse(input1, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
    }

    @Test
    public void testParser2() {
        GrammarGraph graph = GrammarGraph.from(grammar, input2, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
    }

    @Test
    public void testParser3() {
        GrammarGraph graph = GrammarGraph.from(grammar, input3, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input3, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult3(graph), result);
    }

    private static ParseSuccess getParseResult1(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(6)
                .setGSSNodesCount(2)
                .setGSSEdgesCount(3)
                .setNonterminalNodesCount(3)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(5)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF1(graph), statistics, input1);
    }

    private static NonterminalNode expectedSPPF1(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node0, input1);
        TerminalNode node2 = createTerminalNode(registry.getSlot("+"), 1, 2, input1);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node1, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("a"), 2, 3, input1);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node4, input1);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node5);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node6, input1);
        return node7;
    }

    private static ParseSuccess getParseResult2(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(12)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(6)
                .setNonterminalNodesCount(6)
                .setTerminalNodesCount(5)
                .setIntermediateNodesCount(6)
                .setPackedNodesCount(13)
                .setAmbiguousNodesCount(1).build();
        return new ParseSuccess(expectedSPPF2(graph), statistics, input2);
    }

    private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input2);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node0, input2);
        TerminalNode node2 = createTerminalNode(registry.getSlot("+"), 1, 2, input2);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node1, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("a"), 2, 3, input2);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node4, input2);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node5);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node6, input2);
        TerminalNode node8 = createTerminalNode(registry.getSlot("+"), 3, 4, input2);
        IntermediateNode node9 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node7, node8);
        TerminalNode node10 = createTerminalNode(registry.getSlot("a"), 4, 5, input2);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node10, input2);
        IntermediateNode node12 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node5, node8);
        IntermediateNode node13 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node12, node11);
        NonterminalNode node14 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node13, input2);
        IntermediateNode node15 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node9, node11);
        node15.addPackedNode(registry.getSlot("E ::= E + E ."), node3, node14);
        NonterminalNode node16 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node15, input2);
        return node16;
    }

    private static ParseSuccess getParseResult3(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(30)
                .setGSSNodesCount(5)
                .setGSSEdgesCount(15)
                .setNonterminalNodesCount(15)
                .setTerminalNodesCount(9)
                .setIntermediateNodesCount(20)
                .setPackedNodesCount(45)
                .setAmbiguousNodesCount(6).build();
        return new ParseSuccess(expectedSPPF3(graph), statistics, input3);
    }

    private static NonterminalNode expectedSPPF3(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input3);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node0, input3);
        TerminalNode node2 = createTerminalNode(registry.getSlot("+"), 1, 2, input3);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node1, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("a"), 2, 3, input3);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node4, input3);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node5);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node6, input3);
        TerminalNode node8 = createTerminalNode(registry.getSlot("+"), 3, 4, input3);
        IntermediateNode node9 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node7, node8);
        TerminalNode node10 = createTerminalNode(registry.getSlot("a"), 4, 5, input3);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node10, input3);
        IntermediateNode node12 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node5, node8);
        IntermediateNode node13 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node12, node11);
        NonterminalNode node14 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node13, input3);
        IntermediateNode node15 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node9, node11);
        node15.addPackedNode(registry.getSlot("E ::= E + E ."), node3, node14);
        NonterminalNode node16 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node15, input3);
        TerminalNode node17 = createTerminalNode(registry.getSlot("+"), 5, 6, input3);
        IntermediateNode node18 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node16, node17);
        TerminalNode node19 = createTerminalNode(registry.getSlot("a"), 6, 7, input3);
        NonterminalNode node20 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node19, input3);
        IntermediateNode node21 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node11, node17);
        IntermediateNode node22 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node21, node20);
        NonterminalNode node23 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node22, input3);
        IntermediateNode node24 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node14, node17);
        IntermediateNode node25 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node12, node23);
        node25.addPackedNode(registry.getSlot("E ::= E + E ."), node24, node20);
        NonterminalNode node26 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node25, input3);
        IntermediateNode node27 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node18, node20);
        node27.addPackedNode(registry.getSlot("E ::= E + E ."), node9, node23);
        node27.addPackedNode(registry.getSlot("E ::= E + E ."), node3, node26);
        NonterminalNode node28 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node27, input3);
        TerminalNode node29 = createTerminalNode(registry.getSlot("+"), 7, 8, input3);
        IntermediateNode node30 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node28, node29);
        TerminalNode node31 = createTerminalNode(registry.getSlot("a"), 8, 9, input3);
        NonterminalNode node32 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node31, input3);
        IntermediateNode node33 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node20, node29);
        IntermediateNode node34 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node33, node32);
        NonterminalNode node35 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node34, input3);
        IntermediateNode node36 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node23, node29);
        IntermediateNode node37 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node21, node35);
        node37.addPackedNode(registry.getSlot("E ::= E + E ."), node36, node32);
        NonterminalNode node38 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node37, input3);
        IntermediateNode node39 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node26, node29);
        IntermediateNode node40 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node12, node38);
        node40.addPackedNode(registry.getSlot("E ::= E + E ."), node39, node32);
        node40.addPackedNode(registry.getSlot("E ::= E + E ."), node24, node35);
        NonterminalNode node41 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node40, input3);
        IntermediateNode node42 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node30, node32);
        node42.addPackedNode(registry.getSlot("E ::= E + E ."), node18, node35);
        node42.addPackedNode(registry.getSlot("E ::= E + E ."), node9, node38);
        node42.addPackedNode(registry.getSlot("E ::= E + E ."), node3, node41);
        NonterminalNode node43 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node42, input3);
        return node43;
    }

}
