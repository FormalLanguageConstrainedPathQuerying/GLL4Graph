//package org.iguana.grammar.iggy
//
//import iguana.parsetrees.iggy.TermTraversal
//import iguana.parsetrees.slot.NonterminalNodeType
//import iguana.parsetrees.term.Term
//import org.iguana.grammar.symbol.{Start, Nonterminal, Rule}
//import org.iguana.grammar.{GrammarGraph, Grammar}
//import org.iguana.grammar.transformation.{DesugarAlignAndOffside, LayoutWeaver, EBNFToBNF, DesugarPrecedenceAndAssociativity}
//import org.iguana.parser.Iguana
//import iguana.utils.input.Input
//import scala.collection.JavaConverters._
//
//
//object IggyParser {
//
//  def getGrammar(s: String): Grammar = getGrammar(Input.fromString(s))
//
//  def getGrammar(input: Input): Grammar = {
//
//    val result = Iguana.parse(input, GrammarGraph.from(iggyGrammar, input), start)
//
//    if (result.isParseError)
//      throw new RuntimeException(result.asParseError.toString)
//
//    getGrammar(result.asParseSuccess.getTerm, input)
//  }
//
//  private val start = Start.from("Definition")
//
//  private def getGrammar(term: Term, input: Input): Grammar = {
//    val builder: GrammarBuilder = new GrammarBuilder
//    val rules = TermTraversal.build(term, builder).asInstanceOf[java.util.List[Rule]]
//
//    val b = Grammar.builder()
//    var layout: Nonterminal = null
//    for (rule <- rules.asScala) {
//      if (rule.getAttributes.containsKey("@Layout")) {
//        layout = rule.getHead
//        b.addRule(rule.copyBuilderButWithHead(rule.getHead().copyBuilder().setType(NonterminalNodeType.Layout).build()).build())
//      } else {
//        b.addRule(rule)
//      }
//    }
//
//    b.setLayout(layout)
//    transform(b.build())
//  }
//
//  private def transform(g: Grammar): Grammar = {
//    var grammar = new InlineRegex().transform(g)
//    val alignAndOffside: DesugarAlignAndOffside = new DesugarAlignAndOffside
//    val precedenceAndAssociativity: DesugarPrecedenceAndAssociativity = new DesugarPrecedenceAndAssociativity
//    alignAndOffside.doAlign
//    precedenceAndAssociativity.setOP2
//    grammar = alignAndOffside.transform(grammar)
//    grammar = new EBNFToBNF().transform(grammar)
//    alignAndOffside.doOffside
//    grammar = alignAndOffside.transform(grammar)
//    grammar = precedenceAndAssociativity.transform(grammar)
//    grammar = new LayoutWeaver().transform(grammar)
//    grammar
//  }
//
//  private lazy val iggyGrammar = {
//    var g = Grammar.load(getClass.getResourceAsStream("/IggyGrammar"))
//    val precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity
//    precedenceAndAssociativity.setOP2
//
//    g = new EBNFToBNF().transform(g)
//    g = precedenceAndAssociativity.transform(g)
//    g = new LayoutWeaver().transform(g)
//    g.getStartSymbol(Nonterminal.withName("Definition"))
//    g
//  }
//
//}
