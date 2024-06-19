package eu.fays.sandbox.streams;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class LineageEssay {

	public static void main(String[] args) {
		System.out.println(getLineage(TreeSet.class));
	}
	
	/**
	 * Returns the lineage of the given class, i.e. all its ancestors including itself, starting with the highest parent class down to itself.
	 * @param clazz the clazz
	 * @return the lineage : all ancestors and this type
	 */
	public static List<Class<?>> getLineage(final Class<?> clazz) {
		final LinkedList<Class<?>> result = new LinkedList<>();
		for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
			result.addFirst(c); // navigate in class inheritance upwards
		}
		
		return result;
	}
}
