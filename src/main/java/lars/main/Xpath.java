package lars.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import lars.xml.xpath.XpathAwareXMLStreamReaderDelegate;
import lars.xml.xpath.model.XpathExpression;

import org.apache.commons.lang3.StringUtils;

public class Xpath {
	private static final String COUNT_OPTION = "--count";
	private static final String COUNT_OPTION_SHORT = "-c";
	private static final String PRINT_OPTION = "--print";
	private static final List<String> PROGRAM_MODE = Arrays.asList(
			COUNT_OPTION, COUNT_OPTION_SHORT, PRINT_OPTION);
	private static ArrayList<String> options;

	public static void main(String[] args) {
		try {
			int i = 0;
			options = new ArrayList<String>();
			while (args[i].startsWith("-")) {
				parseArg(args[i]);
				i++;
			}
			String xpath = parseXpath(args[i]);
			if (options.size() == 0) {
				options.add(PRINT_OPTION);
			}
			i++;
			List<File> files = new ArrayList<File>();
			do {
				File file = new File(args[i]);
				files.add(file);
				try {
					XpathAwareXMLStreamReaderDelegate reader = new XpathAwareXMLStreamReaderDelegate(
							file);
					reader.setXpathExpression(new XpathExpression(xpath));
					if (options.contains(COUNT_OPTION)
							|| options.contains(COUNT_OPTION_SHORT)) {
						reader.reset();
						int count = reader.count();
						System.out.println("Found " + count + " match"
								+ (count == 1 ? "" : "es") + " for " + xpath
								+ ".");
					}
					if (options.contains(PRINT_OPTION)) {
						reader.reset();
						int match = 0;
						while (reader.nextMatch() != -1) {
							match++;
							System.out.print(match + ": ");
							reader.printSubtree();
							// System.out.println(reader.subtree());
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				} catch (FactoryConfigurationError e) {
					e.printStackTrace();
				}
				i++;
			} while (i < args.length);
		} catch (Exception e) {
			e.printStackTrace();
			printUsage();
		}
	}

	private static String parseXpath(String xpath) {
		if (xpath.startsWith(":")) {
			int startOfXpath = xpath.indexOf('/');
			String commands = xpath.substring(1, startOfXpath).toLowerCase();
			for (int i = 0; i < commands.length(); i++) {
				char c = commands.charAt(i);
				switch (c) {
				case 'c':
					options.add(COUNT_OPTION);
					break;
				case 'p':
					options.add(PRINT_OPTION);
					break;
				}
			}
			return xpath.substring(startOfXpath);
		} else {
			return xpath;
		}
	}

	private static void parseArg(String arg) {
		if (PROGRAM_MODE.contains(arg)) {
			options.add(arg);
		}
	}

	private static void printUsage() {
		String program = Xpath.class.getName();
		System.out.println("java -cp <jarfile> " + program + " ["
				+ StringUtils.join(PROGRAM_MODE, "|")
				+ "] [:cp]xpathExpression file1 [file2 [file3 [...]]]");

	}
}
