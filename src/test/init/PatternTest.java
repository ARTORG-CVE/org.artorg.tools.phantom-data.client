package init;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {
	
	
	public static void main(String[] args) {
		String line = "2018-12-29, 10:24:43, 479ms, WARN : Can't create Phantomina: Phantomina with product id 21-A-J-N exist already     org.artorg.tools.phantomData.client.editor.ItemEditor.lambda$1(ItemEditor.java:93)";
		
		Pattern pattern = Pattern.compile("(\\d+-\\d+-\\d+)\\W*(\\d+.?\\d+.?\\d+)\\W+(\\d+)ms\\W*(\\w*)\\W*(.*)(org.artorg.*)");
		Matcher matcher = pattern.matcher(line);
		
		String date;
		String time;
		String millis;
		String logLevel;
		String message = "";
		
		while(matcher.find()) {
			for (int i=0; i<matcher.groupCount()+1; i++) {
				System.out.println("Group " +i +": '" +line.substring(matcher.start(i), matcher.end(i)) +"'");
			}
			System.out.println();
			
			date = matcher.group(1);
			time = matcher.group(2);
			millis = matcher.group(3);
			logLevel = matcher.group(4);
			message = matcher.group(5);
					
			
		}
		
		System.out.println("Message: " +message);
		
		Pattern pattern2 = Pattern.compile("(.*)(\\s*org.artorg)");
		Matcher matcher2 = pattern2.matcher(message);
		
		while(matcher2.find()) {
			for (int i=0; i<matcher2.groupCount()+1; i++) {
				System.out.println("Group " +i +": '" +message.substring(matcher2.start(i), matcher2.end(i)) +"'");
			}
			System.out.println();
			
//			date = matcher2.group(1);
//			time = matcher2.group(2);
//			millis = matcher2.group(3);
//			logLevel = matcher2.group(4);
//			message = matcher2.group(5);
					
			
		}
		
	}

}
