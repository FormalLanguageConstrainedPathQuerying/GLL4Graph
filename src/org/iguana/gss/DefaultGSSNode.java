/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.gss;

import iguana.utils.collections.IntHashMap;
import iguana.utils.collections.Keys;
import iguana.utils.collections.OpenAddressingHashMap;
import iguana.utils.collections.OpenAddressingIntHashMap;
import iguana.utils.collections.key.Key;
import iguana.utils.input.Input;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.EnvironmentPool;
import org.iguana.grammar.slot.*;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.Result;
import org.iguana.result.ResultOps;
import org.iguana.util.ParserLogger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * If there is a cyclic GSSEdge, it's always the first one. If there is a cyclic GSS edge, there is always
 * a second GSS edge which is stored in restGSSEdges.
 */
public class DefaultGSSNode<T extends Result> implements GSSNode<T> {

	private final int inputIndex;
	private final NonterminalGrammarSlot slot;

	private GSSEdge<T> firstGSSEdge;

	private List<GSSEdge<T>> restGSSEdges;

	private T firstPoppedElement;

	private Map<Key, T> restPoppedElements;

	private final IntHashMap<T> poppedElements;

	public DefaultGSSNode(GSSEdge<T> firstGSSEdge, int inputIndex) {
	    this.firstGSSEdge = firstGSSEdge;
	    this.inputIndex = inputIndex;
		poppedElements = new OpenAddressingIntHashMap<>();
		slot = null;
	}

	public DefaultGSSNode(NonterminalGrammarSlot slot, int inputIndex) {
		this.slot = slot;
		this.inputIndex = inputIndex;
		poppedElements = new OpenAddressingIntHashMap<>();
	}

    @Override
	public void addGSSEdge(Input input, BodyGrammarSlot returnSlot, int i, GSSNode<T> destination, T w, Environment env, IguanaRuntime<T> runtime) {
		if (this == destination && w.isDummy()) {
			if (!(firstGSSEdge instanceof CyclicDummyGSSEdges<?>)) {
				addGSSEdge(firstGSSEdge);
				firstGSSEdge = runtime.createGSSEdge(returnSlot, w, null, env);
			}
			// ParserLogger.getInstance().gssEdgeAdded(firstGSSEdge);
			((CyclicDummyGSSEdges<T>) firstGSSEdge).addReturnSlot(returnSlot);
			iterateOverPoppedElements(firstGSSEdge, returnSlot, destination, input, env, runtime);
		} else {
			GSSEdge<T> edge = runtime.createGSSEdge(returnSlot, w, destination, env);
			// ParserLogger.getInstance().gssEdgeAdded(edge);
			addGSSEdge(edge);
			iterateOverPoppedElements(edge, returnSlot, destination, input, env, runtime);
		}
	}

    private void addGSSEdge(GSSEdge<T> edge) {
		if (restGSSEdges == null) {
			restGSSEdges = new ArrayList<>(4);
		}
		if (edge != null) {
			restGSSEdges.add(edge);
		}
	}

    private void iterateOverPoppedElements(GSSEdge<T> edge, BodyGrammarSlot returnSlot, GSSNode<T> destination, Input input, Environment env, IguanaRuntime<T> runtime) {
        if (firstPoppedElement != null)
            processPoppedElement(firstPoppedElement, edge, returnSlot, destination, input, env, runtime);

        if (restPoppedElements != null) {
            for (T poppedElement: restPoppedElements.values()) {
                processPoppedElement(poppedElement, edge, returnSlot, destination, input, env, runtime);
            }
        }
    }

	public boolean pop(Input input, EndGrammarSlot slot, T child, IguanaRuntime<T> runtime) {
		return pop(input, slot, child, null, runtime);
	}

	public boolean pop(Input input, EndGrammarSlot slot, T result, Object value, IguanaRuntime<T> runtime) {
		// ParserLogger.getInstance().pop(this, result.getLeftExtent(), result, value);
		T node = addPoppedElements(slot, result, value, runtime.getResultOps());
		if (node != null)
			iterateOverEdges(input, node, runtime);

//		int index = result.getIndex();
//		if (slot != null) {
//			T poppedElement = poppedElements.get(index);
//
//			if (poppedElement == null) {
//				poppedElement = runtime.getResultOps().convert(null, result, slot, value);
//				poppedElements.put(index, poppedElement);
////			return true;
//			} else {
//				runtime.getResultOps().convert(poppedElement, result, slot, value);
////			return false;
//			}
//		}

		return node != null;
	}

	public boolean hasResult(int i) {
		return poppedElements.containsKey(i);
	}

	public T getResult(int i) {
		return poppedElements.get(i);
	}

	/**
	 * Returns the newly created popped element, or null if the node already exists
	 */
	private T addPoppedElements(EndGrammarSlot slot, T child, Object value, ResultOps<T> ops) {
		// No node added yet
		if (firstPoppedElement == null) {
			firstPoppedElement = ops.convert(null, child, slot, value);
			poppedElements.put(child.getIndex(), firstPoppedElement);
			return firstPoppedElement;
		} else {
			int rightIndex = child.getIndex();

			// Only one node is added and there is an ambiguity
			if (rightIndex == firstPoppedElement.getIndex() && Objects.equals(value, firstPoppedElement.getValue())) {
				ops.convert(firstPoppedElement, child, slot, value);
				return null;
			} else {
				Key key = value == null ? Keys.from(rightIndex) : Keys.from(rightIndex, value);

				if (restPoppedElements == null) {
					restPoppedElements = new OpenAddressingHashMap<>();
					T poppedElement = ops.convert(null, child, slot, value);
					restPoppedElements.put(key, poppedElement);
					poppedElements.put(rightIndex, poppedElement);
					return poppedElement;
				}

				T poppedElement = restPoppedElements.get(key);
				if (poppedElement == null) {
					poppedElement = ops.convert(null, child, slot, value);
					poppedElements.put(rightIndex, poppedElement);
					restPoppedElements.put(key, poppedElement);
					return poppedElement;
				}

				ops.convert(poppedElement, child, slot, value);
				return null;
			}
		}
	}

	private void processPoppedElement(T poppedElement, GSSEdge<T> edge, BodyGrammarSlot returnSlot,
									  GSSNode<T> destination, Input input, Environment env, IguanaRuntime<T> runtime) {
		//boolean anyMatchTestFollow = input.nextSymbols(poppedElement.getIndex())
		//		.anyMatch(returnSlot::testFollow);
		//if (anyMatchTestFollow) {
			T result = addDescriptor(input, this, poppedElement, edge, returnSlot, runtime);
			if (result != null) {
				runtime.scheduleDescriptor(returnSlot, destination, result, env);
			}
		//}
	}

	private void iterateOverEdges(Input input, T result, IguanaRuntime<T> runtime) {
		if (firstGSSEdge instanceof CyclicDummyGSSEdges<?>) {
            List<BodyGrammarSlot> returnSlots = ((CyclicDummyGSSEdges<?>) firstGSSEdge).getReturnSlots();
			for (BodyGrammarSlot returnSlot : returnSlots) {
				processEdge(input, result, firstGSSEdge, returnSlot, runtime);
			}
		} else if (firstGSSEdge != null){
			processEdge(input, result, firstGSSEdge, firstGSSEdge.getReturnSlot(), runtime);
		}

		if (restGSSEdges != null) {
			for (GSSEdge<T> edge : restGSSEdges) {
				processEdge(input, result, edge, edge.getReturnSlot(), runtime);
			}
		}
	}

	private void processEdge(Input input, T node, GSSEdge<T> edge, BodyGrammarSlot returnSlot, IguanaRuntime<T> runtime) {
		//boolean anyMatchTestFollow = input.nextSymbols(node.getIndex())
		//		.anyMatch(returnSlot::testFollow);
		//if (!anyMatchTestFollow) return;

		T result = addDescriptor(input, this, node, edge, returnSlot, runtime);
		if (result != null) {
			Environment env = runtime.getEnvironment();
			runtime.scheduleDescriptor(returnSlot, edge.getDestination() != null? edge.getDestination() : this, result, env);
		}
	}

    /*
     *
     * Does the following:
     * (1) checks conditions associated with the return slot
     * (2) checks whether the descriptor to be created has been already created (and scheduled) before
     * (2.1) if yes, returns null
     * (2.2) if no, creates one and returns it
     *
     */
    private T addDescriptor(Input input, GSSNode<T> source, T result, GSSEdge<T> edge, BodyGrammarSlot returnSlot, IguanaRuntime<T> runtime) {
        int inputIndex = result.isDummy() ? source.getInputIndex() : result.getIndex();
        Environment env = edge.getEnv() == null ? runtime.getEmptyEnvironment() : edge.getEnv();
        GSSNode<T> destination = edge.getDestination() != null ? edge.getDestination() : source;

        if (returnSlot.requiresBinding())
            env = returnSlot.doBinding(result, env);

        runtime.setEnvironment(env);

        if (returnSlot.getConditions().execute(input, returnSlot, source, inputIndex, runtime.getEvaluatorContext(), runtime)) {
            EnvironmentPool.returnToPool(env);
            return null;
        }

        env = runtime.getEnvironment();

        return returnSlot.getIntermediateNode(edge.getResult(), destination.getInputIndex(), result, env, runtime);
    }

	public NonterminalGrammarSlot getGrammarSlot() {
    	if (slot != null) {
    		return slot;
		}
        NonterminalTransition transition;
        if (firstGSSEdge instanceof CyclicDummyGSSEdges) {
            transition = (NonterminalTransition) restGSSEdges.get(0).getReturnSlot().getInTransition();
        } else {
            transition = (NonterminalTransition) firstGSSEdge.getReturnSlot().getInTransition();
        }
        return transition.getSlot();
	}

	public int getInputIndex() {
        return inputIndex;
	}

	// TODO: find a way to evaluate the environment and the passed arguments
	public Object[] getData() {
		return null;
	}

	public int countGSSEdges() {
		int count = 0;
		count += firstGSSEdge == null ? 0 : 1;
		count += restGSSEdges == null ? 0 : restGSSEdges.size();
		return count;
	}

	public int countPoppedElements() {
		int count = 0;
		if (firstPoppedElement != null) count++;
		if (restPoppedElements != null) count += restPoppedElements.size();
		return count;
	}

	public Iterable<GSSEdge<T>> getGSSEdges() {
		return restGSSEdges;
	}

	public boolean equals(Object obj) {
		if(this == obj) return true;

		if (!(obj instanceof GSSNode)) return false;

		GSSNode<?> other = (GSSNode<?>) obj;

		return  getGrammarSlot() == other.getGrammarSlot() &&
				getInputIndex() == other.getInputIndex() &&
				Arrays.equals(getData(), other.getData());
	}
	public int hashCode() {
		return Objects.hash(getGrammarSlot().hashCode(), getInputIndex(), getData());
	}

	public Iterable<T> getPoppedElements() {
		Stream.Builder<T> poppedElements = Stream.builder();
		if (firstPoppedElement != null) poppedElements.add(firstPoppedElement);
		if (restPoppedElements != null)
			restPoppedElements.values().forEach(poppedElements::add);

		return poppedElements.build()::iterator;
	}

	public String toString() {
		String s = String.format("(%s, %d)", getGrammarSlot(), getInputIndex());
		if (getData() != null) {
			s += String.format("(%s)", getData());
		}
		return s;
	}

}
