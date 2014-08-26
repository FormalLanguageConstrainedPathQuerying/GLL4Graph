package org.jgll.parser;


import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.OriginalGSSEdgeImpl;
import org.jgll.parser.lookup.factory.DescriptorLookupFactory;
import org.jgll.parser.lookup.factory.GSSLookupFactory;
import org.jgll.parser.lookup.factory.SPPFLookupFactory;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.BenchmarkUtil;
import org.jgll.util.ParseStatistics;

/**
 *
 * @author Ali Afroozeh
 * 
 */
public class OriginalGLLParserImpl extends AbstractGLLParserImpl {
	
	protected static final GSSNode u0 = new GSSNode(L0.getInstance(), 0);
		
	public OriginalGLLParserImpl(GSSLookupFactory gssLookupFactory, 
						 SPPFLookupFactory sppfLookupFactory, 
						 DescriptorLookupFactory descriptorLookupFactory) {
		super(gssLookupFactory, sppfLookupFactory, descriptorLookupFactory);
	}
	
	@Override
	public ParseResult parse(GLLLexer lexer, GrammarGraph grammar, String startSymbolName) {
		HeadGrammarSlot startSymbol = grammar.getHeadGrammarSlot(startSymbolName);
		
		if(startSymbol == null) {
			throw new RuntimeException("No nonterminal named " + startSymbolName + " found");
		}
		
		u0.clearDescriptors();
		
		this.grammar = grammar;
		
		this.lexer = lexer;
		
		this.input = lexer.getInput();
		
		initParserState();
		initLookups(grammar, input);
	
		log.info("Parsing %s:", input.getURI());

		long start = System.nanoTime();
		long startUserTime = BenchmarkUtil.getUserTime();
		long startSystemTime = BenchmarkUtil.getSystemTime();
		
		NonterminalNode root;
		
		L0.getInstance().parse(this, lexer, startSymbol);			
		root = sppfLookup.getStartSymbol(startSymbol, input.length());

		ParseResult parseResult;
		
		long end = System.nanoTime();
		long endUserTime = BenchmarkUtil.getUserTime();
		long endSystemTime = BenchmarkUtil.getSystemTime();
		
		if (root == null) {
			parseResult = new ParseError(errorSlot, this.input, errorIndex, errorGSSNode);
			log.info("Parse error:\n %s", parseResult);
		} else {
			ParseStatistics parseStatistics = new ParseStatistics(input, end - start,
					  endUserTime - startUserTime,
					  endSystemTime - startSystemTime, 
					  BenchmarkUtil.getMemoryUsed(),
					  descriptorsCount, 
					  gssLookup.getGSSNodesCount(), 
					  gssLookup.getGSSEdgesCount(), 
					  sppfLookup.getNonterminalNodesCount(),
					  sppfLookup.getTokenNodesCount(),
					  sppfLookup.getIntermediateNodesCount(), 
					  sppfLookup.getPackedNodesCount(), 
					  sppfLookup.getAmbiguousNodesCount());

			parseResult = new ParseSuccess(root, parseStatistics);
			log.info("Parsing finished successfully.");			
			log.info(parseStatistics.toString());
		}
		
		return parseResult;
	}
	
	private void initParserState() {
		cu = u0;
		cn = DummyNode.getInstance();
		ci = 0;
		errorSlot = null;
		errorIndex = 0;
		errorGSSNode = null;
	}
	
	@Override
	public final GrammarSlot pop(GSSNode gssNode, int inputIndex, NonPackedNode node) {
		
		if (gssNode != u0) {

			log.debug("Pop %s, %d, %s", gssNode, inputIndex, node);
			
			gssLookup.addToPoppedElements(gssNode, node);

			BodyGrammarSlot returnSlot = (BodyGrammarSlot) gssNode.getGrammarSlot();

			if (returnSlot.getPopConditions().execute(this, lexer, gssNode, inputIndex)) {
				return null; 
			}
			
			// Optimization for the case when only one GSS Edge is available.
			// No scheduling of descriptors, rather direct jump to the slot
			// to be processed.
			if (gssNode.countGSSEdges() == 1) {
				GSSEdge edge = gssNode.getGSSEdges().iterator().next();
		
				SPPFNode sppfNode = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, edge.getNode(), node);
				Descriptor descriptor = new Descriptor(returnSlot, edge.getDestination(), inputIndex, sppfNode);
				
				if (!hasDescriptor(descriptor)) {
					cn = sppfNode;
					cu = edge.getDestination();
					ci = inputIndex;
					log.trace("Processing %s", descriptor);
					descriptorsCount++;
					return returnSlot;
				}
				return null;
			}
			
			for (GSSEdge edge : gssNode.getGSSEdges()) {
				SPPFNode y = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, edge.getNode(), node);
				Descriptor descriptor = new Descriptor(returnSlot, edge.getDestination(), inputIndex, y);
				if (!hasDescriptor(descriptor)) {
					scheduleDescriptor(new Descriptor(returnSlot, edge.getDestination(), inputIndex, y));
				}
			}
		}
		
		return null;
	}
	

	/**
	 * 
	 * create(L, u, w) {
     *	 let w be the value of cn
	 *	 if there is not already a GSS node labelled (L,A ::= alpha . beta, ci) create one
	 * 	 let v be the GSS node labelled (L,A ::= alpha . beta, ci)
	 *   if there is not an edge from v to cu labelled w {
	 * 		create an edge from v to cu labelled w
	 * 		for all ((v, z) in P) {
	 * 			let x be the node returned by getNodeP(A ::= alpha . beta, w, z)
	 * 			add(L, cu, h, x)) where h is the right extent of z
	 * 		}
	 * 	 }
	 * 	 return v
	 * }
	 * 
	 * @param returnSlot the grammar label
	 * 
	 * @param nonterminalIndex the index of the nonterminal appearing as the head of the rule
	 *                         where this position refers to. 
	 * 
	 * @param alternateIndex the index of the alternate of the rule where this position refers to.
	 * 
	 * @param position the position in the body of the rule where this position refers to
	 *
	 * @return 
     *
	 */
	@Override
	public final GSSNode createGSSNode(BodyGrammarSlot returnSlot, HeadGrammarSlot head) {
		cu = gssLookup.getGSSNode(returnSlot, ci);
		log.trace("GSSNode created: %s",  cu);
		return cu;
	}
	
	@Override
	public final GSSNode hasGSSNode(BodyGrammarSlot slot, HeadGrammarSlot head) {
		return gssLookup.hasGSSNode(slot, ci);
	}
	
	@Override
	public GrammarSlot createGSSEdge(BodyGrammarSlot slot, GSSNode destination, SPPFNode w, GSSNode source) {
		
		GSSEdge edge = new OriginalGSSEdgeImpl(w, destination);
		
		if(source.getGSSEdge(edge)) {
			
			BodyGrammarSlot returnSlot = (BodyGrammarSlot) source.getGrammarSlot();
			
			log.trace("GSS Edge created from %s to %s", source, destination);
			
			// Optimization for the case when only one element is in the popped elements.
			// No scheduling of descriptors, rather direct jump to the slot
			// to be processed.
			if (source.countPoppedElements() == 1) {
				SPPFNode z = source.getPoppedElements().iterator().next();
				if(returnSlot.getPopConditions().execute(this, lexer, destination, z.getRightExtent())) {
					return null;
				}
				
				SPPFNode x = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, w, z);
				Descriptor descriptor = new Descriptor(returnSlot, destination, z.getRightExtent(), x);
				
				if (!hasDescriptor(descriptor)) {
					cn = x;
					cu = destination;
					ci = z.getRightExtent();
					log.trace("Processing %s", descriptor);
					descriptorsCount++;
					return returnSlot;
				}
				return null;
			}
			
			label:
			for (SPPFNode z : source.getPoppedElements()) {
				
				// Execute pop actions for continuations, when the GSS node already
				// exits. The input index will be the right extend of the node
				// stored in the popped elements.
				if(returnSlot.getPopConditions().execute(this, lexer, destination, z.getRightExtent())) {
					continue label;
				}
				
				SPPFNode x = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, w, z); 
				Descriptor descriptor = new Descriptor(returnSlot, destination, z.getRightExtent(), x);
				if (!hasDescriptor(descriptor)) {
					scheduleDescriptor(descriptor);
				}
			}
		}
		
		return null;
	}

}