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

package org.iguana.grammar.slot;

import iguana.utils.collections.IntHashMap;
import iguana.utils.collections.Keys;
import iguana.utils.collections.OpenAddressingHashMap;
import iguana.utils.collections.OpenAddressingIntHashMap;
import iguana.utils.collections.key.Key;
import iguana.utils.collections.rangemap.RangeMap;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.gss.DefaultGSSNode;
import org.iguana.gss.GSSEdge;
import org.iguana.gss.GSSNode;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.Result;
import org.iguana.util.Configuration.EnvironmentImpl;
import org.iguana.util.ParserLogger;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyList;


public class NonterminalGrammarSlot implements GrammarSlot {

    private final Nonterminal nonterminal;

    private final List<BodyGrammarSlot> firstSlots;

    private Map<Key, GSSNode> gssNodes;

    private IntHashMap<GSSNode> intGSSNodes;

    private RangeMap<BodyGrammarSlot> lookAheadTest;

    private FollowTest followTest;

    public NonterminalGrammarSlot(Nonterminal nonterminal) {
        this.nonterminal = nonterminal;
        this.firstSlots = new ArrayList<>();
    }

    public <T extends Result> void addStartGSSNode(GSSNode<T> gssNode, int index) {
        if (intGSSNodes == null)
            intGSSNodes = new OpenAddressingIntHashMap<>();
        intGSSNodes.put(index, gssNode);
    }

    public void addFirstSlot(BodyGrammarSlot slot) {
        firstSlots.add(slot);
    }

    public List<BodyGrammarSlot> getFirstSlots() {
        return firstSlots;
    }

    private List<BodyGrammarSlot> getFirstSlots(int v) {
        return lookAheadTest.get(v);
    }

    private List<BodyGrammarSlot> getFirstSlots(List<Integer> v) {
        List<BodyGrammarSlot> result = new ArrayList<>();
        v.forEach(t -> result.addAll(lookAheadTest.get(t)));
        return result;
    }

    private Stream<BodyGrammarSlot> getFirstSlots(Stream<Integer> v) {
        List<BodyGrammarSlot> result = new ArrayList<>();
        v.forEach(t -> {
            result.addAll(lookAheadTest.get(t));
        });
        return result.stream();
    }

    public void setLookAheadTest(RangeMap<BodyGrammarSlot> lookAheadTest) {
        this.lookAheadTest = lookAheadTest;
    }

    public void setFollowTest(FollowTest followTest) {
        this.followTest = followTest;
    }

    boolean testFollow(int v) {
        return followTest.test(v);
    }

    public Nonterminal getNonterminal() {
        return nonterminal;
    }

    public NonterminalNodeType getNodeType() {
        return nonterminal.getNodeType();
    }

    public String[] getParameters() {
        return nonterminal.getParameters();
    }

    public Expression[] getArguments() {
        return nonterminal.getArguments();
    }

    public int countGSSNodes() {
        if (gssNodes == null) {
            return 0;
        }
        return gssNodes.size();
    }

    @Override
    public String toString() {
        return nonterminal.toString();
    }

    public Iterable<GSSNode> getGSSNodes() {
        if (gssNodes == null) {
            return emptyList();
        }
        return gssNodes.values();
    }

    @Override
    public void reset() {
        gssNodes = null;
        intGSSNodes = null;
    }

    public <T extends Result> void create(Input input, BodyGrammarSlot returnSlot, GSSNode<T> u, T result, Expression[] arguments, Environment env, IguanaRuntime<T> runtime) {
        int i = result.isDummy() ? u.getInputIndex() : result.getIndex();

        Key key = null;
        Object[] data = null;

        if (arguments != null) {
            data = runtime.evaluate(arguments, env, input);
            key = Keys.from(i, data);
        }

        GSSNode gssNode = null;

        if (arguments == null) {
            if (intGSSNodes == null) {
                intGSSNodes = new OpenAddressingIntHashMap<>();
            } else {
                gssNode = intGSSNodes.get(i);
            }
        } else {
            if (gssNodes == null) {
                gssNodes = new OpenAddressingHashMap<>();
            } else {
                gssNode = gssNodes.get(key);
            }
        }

        if (gssNode == null) {
            Stream<BodyGrammarSlot> firstSlots = getFirstSlots(input.nextSymbols(i));
            Stream<BodyGrammarSlot> testFirstSlots = getFirstSlots(input.nextSymbols(i));
            if (testFirstSlots.findAny().isEmpty()) {
                return;
            }

            GSSEdge<T> gssEdge = runtime.createGSSEdge(returnSlot, result, u, env);
            gssNode = new DefaultGSSNode<>(gssEdge, i);

//            ParserLogger.getInstance().gssNodeAdded(gssNode, data);
//            ParserLogger.getInstance().gssEdgeAdded(gssEdge);

            Environment newEnv = runtime.getEnvironment();

            if (data != null) {
                if (runtime.getConfiguration().getEnvImpl() == EnvironmentImpl.ARRAY || runtime.getConfiguration().getEnvImpl() == EnvironmentImpl.INT_ARRAY)
                    newEnv = runtime.getEmptyEnvironment().declare(data);
                else
                    newEnv = runtime.getEmptyEnvironment().declare(nonterminal.getParameters(), data);
            }

            Environment finalNewEnv = newEnv;
            GSSNode finalGssNode = gssNode;
            firstSlots.forEach(slot -> {
                runtime.setEnvironment(finalNewEnv);

                if (slot.getLabel() != null)
                    runtime.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, slot.getLabel()), i);

                int inputIndex = result.isDummy() ? finalGssNode.getInputIndex() : result.getIndex();
                if (!slot.getConditions().execute(input, returnSlot, finalGssNode, inputIndex, runtime.getEvaluatorContext(), runtime))
                    runtime.scheduleDescriptor(slot, finalGssNode, runtime.getResultOps().dummy(), runtime.getEnvironment());
            });
            if (arguments == null) {
                intGSSNodes.put(i, gssNode);
            } else {
                gssNodes.put(key, gssNode);
            }
        } else {
            gssNode.addGSSEdge(input, returnSlot, i, u, result, env, runtime);
        }
    }

    static <T> Stream<T> wrapperStream(Stream<T> stream) {
        Iterator<T> iterator = stream.iterator();
        if (iterator.hasNext()) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
        } else {
            return null;
        }
    }
}
