package org.iguana.parser.basic;

import static iguana.parsetrees.sppf.SPPFNodeFactory.createIntermediateNode;
import static iguana.parsetrees.sppf.SPPFNodeFactory.createNonterminalNode;
import static iguana.parsetrees.sppf.SPPFNodeFactory.createTerminalNode;
import static iguana.parsetrees.term.TermFactory.createNonterminalTerm;
import static iguana.parsetrees.term.TermFactory.createTerminalTerm;
import static iguana.utils.collections.CollectionsUtil.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.term.Term;
import iguana.regex.Character;
import iguana.utils.input.Input;

/**
 * E ::= E '*' E
 *     | E '+' E
 *     | 'a'
 */
public class Test19 {

    static Nonterminal E = Nonterminal.withName("E");
    static Terminal a = Terminal.from(Character.from('a'));
    static Terminal plus = Terminal.from(Character.from('+'));
    static Terminal star = Terminal.from(Character.from('*'));

    static Rule r1 = Rule.withHead(E).addSymbols(E, star, E).build();
    static Rule r2 = Rule.withHead(E).addSymbols(E, plus, E).build();
    static Rule r3 = Rule.withHead(E).addSymbols(a).build();

    public static Grammar grammar = Grammar.builder().addRules(r1, r2, r3).build();
    private static Nonterminal startSymbol = E;

    private static Input input1 = Input.fromString("a+a");
    private static Input input2 = Input.fromString("a+a*a");
    private static Input input3 = Input.fromString("a+a*a+a*a");

    @Test
    public void testParser1() {
        GrammarGraph graph = GrammarGraph.from(grammar, input1);
        ParseResult result = Iguana.parse(input1, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
    }

    @Test
    public void testParser2() {
        GrammarGraph graph = GrammarGraph.from(grammar, input2);
        ParseResult result = Iguana.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
    }

    @Test
    public void testParser3() {
        GrammarGraph graph = GrammarGraph.from(grammar, input3);
        ParseResult result = Iguana.parse(input3, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult3(graph), result);
    }

    private ParseSuccess getParseResult1(GrammarGraph registry) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(8)
                .setGSSNodesCount(2)
                .setGSSEdgesCount(5)
                .setNonterminalNodesCount(3)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(5)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF1(registry), statistics, input1);
    }

    private NonterminalNode expectedSPPF1(GrammarGraph registry) {
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

    private Term getTree1() {
        Term t0 = createTerminalTerm(a, 0, 1, input1);
        Term t1 = createNonterminalTerm(r3, list(t0), input1);
        Term t2 = createTerminalTerm(plus, 1, 2, input1);
        Term t3 = createTerminalTerm(a, 2, 3, input1);
        Term t4 = createNonterminalTerm(r3, list(t3), input1);
        Term t5 = createNonterminalTerm(r2, list(t1, t2, t4), input1);
        return t5;
    }

    private ParseSuccess getParseResult2(GrammarGraph registry) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(16)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(9)
                .setNonterminalNodesCount(6)
                .setTerminalNodesCount(5)
                .setIntermediateNodesCount(7)
                .setPackedNodesCount(14)
                .setAmbiguousNodesCount(1).build();
        return new ParseSuccess(expectedSPPF2(registry), statistics, input2);
    }

    private NonterminalNode expectedSPPF2(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input2);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node0, input2);
        TerminalNode node2 = createTerminalNode(registry.getSlot("+"), 1, 2, input2);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node1, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("a"), 2, 3, input2);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node4, input2);
        TerminalNode node6 = createTerminalNode(registry.getSlot("*"), 3, 4, input2);
        IntermediateNode node7 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node5, node6);
        TerminalNode node8 = createTerminalNode(registry.getSlot("a"), 4, 5, input2);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node8, input2);
        IntermediateNode node10 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node7, node9);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E * E ."), node10, input2);
        IntermediateNode node12 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node11);
        IntermediateNode node13 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node5);
        NonterminalNode node14 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node13, input2);
        IntermediateNode node15 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node14, node6);
        IntermediateNode node16 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node15, node9);
        NonterminalNode node17 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node12, input2);
        node17.addPackedNode(registry.getSlot("E ::= E * E ."), node16);
        return node17;
    }

    private ParseSuccess getParseResult3(GrammarGraph registry) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(41)
                .setGSSNodesCount(5)
                .setGSSEdgesCount(20)
                .setNonterminalNodesCount(15)
                .setTerminalNodesCount(9)
                .setIntermediateNodesCount(26)
                .setPackedNodesCount(51)
                .setAmbiguousNodesCount(10).build();
        return new ParseSuccess(expectedSPPF3(registry), statistics, input3);
    }

    private NonterminalNode expectedSPPF3(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input3);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node0, input3);
        TerminalNode node2 = createTerminalNode(registry.getSlot("+"), 1, 2, input3);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node1, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("a"), 2, 3, input3);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node4, input3);
        TerminalNode node6 = createTerminalNode(registry.getSlot("*"), 3, 4, input3);
        IntermediateNode node7 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node5, node6);
        TerminalNode node8 = createTerminalNode(registry.getSlot("a"), 4, 5, input3);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node8, input3);
        IntermediateNode node10 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node7, node9);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E * E ."), node10, input3);
        TerminalNode node12 = createTerminalNode(registry.getSlot("+"), 5, 6, input3);
        IntermediateNode node13 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node11, node12);
        TerminalNode node14 = createTerminalNode(registry.getSlot("a"), 6, 7, input3);
        NonterminalNode node15 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node14, input3);
        IntermediateNode node16 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node13, node15);
        IntermediateNode node17 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node9, node12);
        IntermediateNode node18 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node17, node15);
        NonterminalNode node19 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node18, input3);
        IntermediateNode node20 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node7, node19);
        NonterminalNode node21 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node16, input3);
        node21.addPackedNode(registry.getSlot("E ::= E * E ."), node20);
        TerminalNode node22 = createTerminalNode(registry.getSlot("*"), 7, 8, input3);
        IntermediateNode node23 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node21, node22);
        TerminalNode node24 = createTerminalNode(registry.getSlot("a"), 8, 9, input3);
        NonterminalNode node25 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node24, input3);
        IntermediateNode node26 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node15, node22);
        IntermediateNode node27 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node26, node25);
        NonterminalNode node28 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E * E ."), node27, input3);
        IntermediateNode node29 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node17, node28);
        IntermediateNode node30 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node19, node22);
        IntermediateNode node31 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node30, node25);
        NonterminalNode node32 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node29, input3);
        node32.addPackedNode(registry.getSlot("E ::= E * E ."), node31);
        IntermediateNode node33 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node23, node25);
        node33.addPackedNode(registry.getSlot("E ::= E * E ."), node7, node32);
        IntermediateNode node34 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node13, node28);
        NonterminalNode node35 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E * E ."), node33, input3);
        node35.addPackedNode(registry.getSlot("E ::= E + E ."), node34);
        IntermediateNode node36 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node11);
        IntermediateNode node37 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node5);
        NonterminalNode node38 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node37, input3);
        IntermediateNode node39 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node38, node6);
        IntermediateNode node40 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node39, node9);
        NonterminalNode node41 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node36, input3);
        node41.addPackedNode(registry.getSlot("E ::= E * E ."), node40);
        IntermediateNode node42 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node41, node12);
        IntermediateNode node43 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node35);
        node43.addPackedNode(registry.getSlot("E ::= E + E ."), node42, node28);
        IntermediateNode node44 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node21);
        node44.addPackedNode(registry.getSlot("E ::= E + E ."), node42, node15);
        IntermediateNode node45 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node39, node19);
        NonterminalNode node46 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node44, input3);
        node46.addPackedNode(registry.getSlot("E ::= E * E ."), node45);
        IntermediateNode node47 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node46, node22);
        IntermediateNode node48 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node47, node25);
        node48.addPackedNode(registry.getSlot("E ::= E * E ."), node39, node32);
        NonterminalNode node49 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node43, input3);
        node49.addPackedNode(registry.getSlot("E ::= E * E ."), node48);
        return node49;
    }

}
