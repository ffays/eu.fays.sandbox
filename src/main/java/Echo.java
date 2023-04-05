
public class Echo {
	public static void main(String[] args) {
		int i=0;
		for(final String arg : args) {
			if(i>0) {
				System.out.print(' ');
			}
			System.out.print(arg);
			i++;
		}
		if(i>0) {
			System.out.println();
			System.out.flush();
		}
	}
}
