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

package org.iguana.sppf;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.traversal.SPPFVisitor;

public class IntermediateNode extends NonPackedNode {

    private final NonPackedNode leftChild;

    private final NonPackedNode rightChild;

    private final BodyGrammarSlot slot;

    private boolean ambiguous;

    public IntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
        this.slot = slot;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    @Override
    public <R> R accept(SPPFVisitor<R> visitAction) {
        return visitAction.visit(this);
    }

    @Override
    public SPPFNode getChildAt(int index) {
        if (index == 0) {
            return leftChild;
        }
        if (index == 1) {
            return rightChild;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public int childrenCount() {
        return 2;
    }

    @Override
    public BodyGrammarSlot getGrammarSlot() {
        return slot;
    }

    @Override
    public int getLeftExtent() {
        return leftChild.getLeftExtent();
    }

    @Override
    public int getIndex() {
        return rightChild.getRightExtent();
    }

    @Override
    public void setAmbiguous(boolean ambiguous) {
        this.ambiguous = ambiguous;
    }

    @Override
    public boolean isAmbiguous() {
        return ambiguous;
    }

    @Override
    public PackedNode getFirstPackedNode() {
        return new PackedNode(slot, leftChild, rightChild);
    }
}
