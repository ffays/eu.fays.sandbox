package eu.fays.sandbox.optional;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Function;

public class OptionalEssay {
	// Article: http://illegalargumentexception.blogspot.be/2015/03/java-handling-null-in-getter-method.html
	public static void main(String[] args) {
		final Leaf leaf0 = new Leaf();
		final Leaf leaf1 = new Leaf();
		leaf1.setName("leaf1");
		
		final Branch branch0 = new Branch();
		final Branch branch1 = new Branch();
		final Branch branch2 = new Branch();
		branch1.setLeaf(leaf0);
		branch2.setLeaf(leaf1);
		
		final Trunk trunk0 = new Trunk();
		final Trunk trunk1 = new Trunk();
		final Trunk trunk2 = new Trunk();
		final Trunk trunk3 = new Trunk();
		trunk1.setBranch(branch0);
		trunk2.setBranch(branch1);
		trunk3.setBranch(branch2);
		
		final Root root0 = null;
		final Root root1 = new Root();
		final Root root2 = new Root();
		final Root root3 = new Root();
		final Root root4 = new Root();
		final Root root5 = new Root();
		root2.setTrunk(trunk0);
		root3.setTrunk(trunk1);
		root4.setTrunk(trunk2);
		root5.setTrunk(trunk3);
		
		final Root[] roots = { root0, root1, root2, root3, root4, root5 };
		for(int i=0; i<roots.length; i++) {
			final String leafName = Optional.ofNullable(roots[i])
	                .map(Root::getTrunk)
	                .map(Trunk::getBranch)
	                .map(Branch::getLeaf)
	                .map(Leaf::getName)
	                .orElse("∅");
			System.out.println(MessageFormat.format("root{0,number,0}: {1}", i, leafName));
		}
		
		final Branch[] branches = {branch0, branch1, branch2};
		final Function<Branch, Optional<Leaf>> optionalLeafFunction = ((Function<Branch,Leaf>)Branch::getLeaf).andThen(Optional::ofNullable);
//		 final Function<Branch,String> f = ((Function<Branch,Leaf>)Branch::getLeaf).andThen(Object::toString);
		for(int i=0; i<branches.length; i++) {
			final Optional<Leaf> optionalLeaf = optionalLeafFunction.apply(branches[i]);
			final String leafName = optionalLeaf.orElseGet(Leaf::new).toString();
			System.out.println(MessageFormat.format("branch{0,number,0}: {1}", i, leafName));
		}

		
	}
}
