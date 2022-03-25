package eu.fays.sandbox.color;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ColorEssay {

	public static void main(String[] args) {
		System.out.println("<html>");
		System.out.println("<body>");
		System.out.println("<table>");
		System.out.println("<tr><th>NAME_REF</th><th>NAME_RGB</th><th>NAME_HSV</th><th>COLOR_REF</th><th>COLOR_RGB</th><th>COLOR_HSV</th></tr>");
		for(final Color color : Color.values()) {
			final Color[] colors = Arrays.stream(Color.values()).filter(c -> c.rgb != color.rgb).collect(Collectors.toList()).toArray(new Color[] {});
			final Color closestColorByHSV = Color.findClosestColorByHueSaturationValueDistance(color.getHSV(), colors);	
			final Color closestColorByRGB = Color.findClosestColorByRedGreenBlueDistance(color.getRGB(), colors);
			final Color[] colors3 = { color, closestColorByRGB, closestColorByHSV};
			System.out.print("<tr>");
			for(final Color color3 : colors3) {
				System.out.print("<td>");
				System.out.print(color3.name());
				System.out.print("</td>");
			}
			for(final Color color3 : colors3) {
				System.out.print("<td style=\"background-color: ");
				System.out.print(color3.name().toLowerCase());
				System.out.print("\">&nbsp;</td>");
			}
			System.out.println("</tr>");
		}
		System.out.println("</table>");
		System.out.println("</body>");
		System.out.println("</html>");

	}

}
