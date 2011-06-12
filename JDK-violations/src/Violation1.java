import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class Violation1 {
	public int addElementsReturnSize(Collection<Integer> collection) {
		Random rand = new Random();
		for (int i = 0 ; i < 100 ; i++)
			collection.add(rand.nextInt() % 5);
		return collection.size();
	}
	
	public static void main(String[] args) {
		System.out.println("Instance #1: " + new Violation1().addElementsReturnSize(new LinkedList<Integer>()));
		System.out.println("Instance #2: " + new Violation1().addElementsReturnSize(new HashSet<Integer>()));
	}
}
