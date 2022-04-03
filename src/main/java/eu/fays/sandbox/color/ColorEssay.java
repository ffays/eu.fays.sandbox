package eu.fays.sandbox.color;

import static java.awt.Desktop.getDesktop;
import static java.lang.System.out;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ColorEssay {

	public static void main(String[] args) throws Exception {
		final File file = File.createTempFile(ColorEssay.class.getSimpleName() + "-", ".html");
		
		final String[] headers = {"REF", "HSVW", "RGBW", "RGB", "LAB", "LUV", "HSV" };
		
		// Color findClosestColorByRedGreenBlueDistance(final int rgb, Color[] values)
		final List<BiFunction<Integer, Color[], Color>> functions = new ArrayList<>();
		
		functions.add(ColorEssay::reference);
		functions.add(Color::findClosestColorByHueSaturationValueDistanceWeighted);
		functions.add(Color::findClosestColorByRedGreenBlueDistanceWeighted);
		functions.add(Color::findClosestColorByRedGreenBlueDistance);
		functions.add(Color::findClosestColorByLabDistance);
		functions.add(Color::findClosestColorByLuvDistance);
		functions.add(Color::findClosestColorByHueSaturationValueDistance);
		
//		{findClosestColorByRedGreenBlueDistance, findClosestColorByLabDistance,  findClosestColorByHueSaturationValueDistance};
		
		try (final PrintWriter writer = new PrintWriter(file)) {
			writer.println("<html>");
			writer.println("<head>");
			writer.println("<title>");
			writer.println(ColorEssay.class.getSimpleName());
			writer.println("</title>");
			writer.println("<style>");
			writer.println("table { border-width: 1px; border-color: black; border-style: solid; border-collapse: collapse; } ");
			writer.println("th,td { border-width: 1px; border-color: black; border-style: solid; border-collapse: collapse; font-family: Verdana; padding: 3px; text-align: left; font-size: 10pt }");
			writer.println("</style>");
			writer.println("</head>");
			writer.println("<body>");
			writer.println("<table>");
			writer.println("<tr>");
			writer.println("</tr>");
			for(final String header : headers) {
				writer.println("\t<th>NAME_"  + header +  "</th>");
			}
			for(final String header : headers) {
				writer.println("\t<th>COLOR_"  + header +  "</th>");
			}
			for(final Color referenceColor : Color.values()) {
				final Color[] otherColors = Arrays.stream(Color.values()).filter(c -> c.rgb != referenceColor.rgb).collect(Collectors.toList()).toArray(new Color[] {});
				final List<Color> matchingColors = functions.stream().map(f -> f.apply(referenceColor.rgb, otherColors)).collect(toList());
				writer.print("<tr>");
				for(final Color matchingColor : matchingColors) {
					writer.print("<td>");
					writer.print(matchingColor.name());
					writer.print("</td>");
				}
				for(final Color matchingColor : matchingColors) {
					writer.print("<td style=\"background-color: ");
					writer.print(matchingColor.name().toLowerCase());
					writer.print("\">&nbsp;</td>");
				}
				writer.println("</tr>");
			}
			writer.println("</table>");
			writer.println("</body>");
			writer.println("</html>");
		}
		
		out.println(file);
		getDesktop().open(file);
	}
	
	
	public static Color reference(final int rgb, Color[] values) {
		for(final Color color : Color.values()) {
			if(color.rgb == rgb) {
				return color;
			}
		}
		
		return Color.BLACK;
	}

}
