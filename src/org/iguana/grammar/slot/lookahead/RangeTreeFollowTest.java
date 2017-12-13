package org.iguana.grammar.slot.lookahead;

import iguana.regex.CharacterRange;
import iguana.utils.collections.rangemap.AVLIntRangeTree;
import iguana.utils.collections.rangemap.ArrayIntRangeTree;
import iguana.utils.collections.rangemap.IntRangeTree;

import java.util.Set;

public class RangeTreeFollowTest implements FollowTest {

	private IntRangeTree rangeTree;
	
	public RangeTreeFollowTest(Set<CharacterRange> set) {
		rangeTree = new AVLIntRangeTree();
		set.forEach(r -> rangeTree.insert(r, 1));
		rangeTree = new ArrayIntRangeTree(rangeTree);
	}
	
	@Override
	public boolean test(int v) {
		return rangeTree.get(v) == 1;
	}

}
