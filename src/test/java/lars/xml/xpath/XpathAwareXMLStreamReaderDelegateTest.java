package lars.xml.xpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import lars.string.StringMatcher;
import lars.xml.xpath.model.AttributePredicate;
import lars.xml.xpath.model.InvalidXpathException;
import lars.xml.xpath.model.XpathElement;
import lars.xml.xpath.model.XpathExpression;

import org.junit.AfterClass;
import org.junit.Test;

public class XpathAwareXMLStreamReaderDelegateTest {
	@AfterClass
	public static void afterClass() {
		System.out.println(StringMatcher.usage);
		System.out.println(StringMatcher.calls);

	}

	@Test
	public void testXpathExpression() {
		printMethodHeader("testXpathExpression");
		try {
			XpathAwareXMLStreamReaderDelegate reader = new XpathAwareXMLStreamReaderDelegate(
					new File("src/test/resources/testAttributes.xml"));

			checkAndPrint(reader, "/bookstore/book/title", 3);
			checkAndPrint(reader, "/bookstore/book/title[@lang]", 3);
			checkAndPrint(reader, "/bookstore/book/title[@lang='de']", 1);
			checkAndPrint(reader, "/bookstore/book[@cover]/title[@lang]", 2);
			checkAndPrint(reader, "/bookstore/book[@cover]/title[@lang='en']",
					1);
			checkAndPrint(reader, "/bookstore/book/title[@lang='*e*']", 3);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private void checkAndPrint(XpathAwareXMLStreamReaderDelegate reader,
			String xpath, int expectedElementCount)
			throws InvalidXpathException, XMLStreamException,
			FileNotFoundException, FactoryConfigurationError {
		reader.reset();
		System.out.println(xpath);
		reader.setXpathExpression(new XpathExpression(xpath));
		while (reader.nextMatch() != -1) {
			System.out.print("\t");
			reader.printSubtree();
		}
		reader.reset();
		assertEquals(xpath, expectedElementCount, reader.count());
	}

	@Test
	public void testXpathElement() {
		printMethodHeader("testXpathElement");
		try {
			// XpathAwareXMLStreamReaderDelegate reader = new
			// XpathAwareXMLStreamReaderDelegate(
			// new File("src/test/resources/testAttributes.xml"));
			System.out.println(AttributePredicate.PATTERN);
			assertTrue("[@lang='en']".matches(AttributePredicate.PATTERN));
			assertFalse("[@lang=en']".matches(AttributePredicate.PATTERN));
			assertFalse("[@lang='en]".matches(AttributePredicate.PATTERN));
			assertFalse("[@lang=en]".matches(AttributePredicate.PATTERN));
			assertTrue("[@lang]".matches(AttributePredicate.PATTERN));
			XpathElement xpathElement;

			xpathElement = new XpathElement("title[@lang]");
			assertEquals("title", xpathElement.getXpath());
			assertEquals(null, xpathElement.getPredicate("lang").getValue());

			xpathElement = new XpathElement("title[@lang='en']");
			assertEquals("title", xpathElement.getXpath());
			assertEquals("en", xpathElement.getPredicate("lang").getValue());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCount() {
		printMethodHeader("testCount");
		try {
			XpathAwareXMLStreamReaderDelegate reader = new XpathAwareXMLStreamReaderDelegate(
					new File("src/test/resources/pom.xml"));
			XpathExpression xpath = new XpathExpression("/project");
			reader.setXpathExpression(xpath);
			assertEquals(xpath.toString(), 1, reader.count());
			reader.reset();
			xpath = new XpathExpression("/project/*");
			reader.setXpathExpression(xpath);
			assertEquals(xpath.toString(), 2, reader.count());
			reader.reset();
			xpath = new XpathExpression("/project/dependencies");
			reader.setXpathExpression(xpath);
			assertEquals(xpath.toString(), 1, reader.count());
			reader.reset();
			xpath = new XpathExpression("/project/dependencies/dependency");
			reader.setXpathExpression(xpath);
			assertEquals(xpath.toString(), 4, reader.count());
			reader.reset();
			xpath = new XpathExpression(
					"/project/dependencies/dependency/version");
			reader.setXpathExpression(xpath);
			assertEquals(xpath.toString(), 4, reader.count());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testNextMatch() {
		printMethodHeader("testNextMatch");
		try {
			XpathAwareXMLStreamReaderDelegate reader = new XpathAwareXMLStreamReaderDelegate(
					new File("src/test/resources/pom.xml"));
			XpathExpression xpath = new XpathExpression(
					"/project/dependencies/dependency/groupId");
			reader.setXpathExpression(xpath);
			int i = 0;
			System.out.println(xpath);
			while (reader.nextMatch() != -1) {
				i++;
				System.out.print(i + ": ");
				System.out.println(reader.subtree());
			}
			assertEquals(4, i);
			reader.reset();
			xpath = new XpathExpression("/project/dependencies/dependency");
			reader.setXpathExpression(xpath);
			i = 0;
			System.out.println(xpath);
			while (reader.nextMatch() != -1) {
				i++;
				System.out.print(i + ": ");
				reader.printSubtree();
				// System.out.println(reader.subtree());
			}
			assertEquals(4, i);
			reader.reset();
			xpath = new XpathExpression("/project/dependencies");
			reader.setXpathExpression(xpath);
			i = 0;
			System.out.println(xpath);
			while (reader.nextMatch() != -1) {
				i++;
				System.out.print(i + ": ");
				System.out.println(reader.subtree());
			}
			assertEquals(1, i);
			reader.reset();
			xpath = new XpathExpression("/project/dependencies/*/*Id");
			reader.setXpathExpression(xpath);
			i = 0;
			System.out.println(xpath);
			while (reader.nextMatch() != -1) {
				i++;
				System.out.print(i + ": ");
				System.out.println(reader.subtree());
			}
			assertEquals(8, i);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private void printMethodHeader(String method) {
		System.out.println();
		for (int i = 0; i < method.length() + 4; i++) {
			System.out.print("#");
		}
		System.out.println();
		System.out.println("# " + method + " #");
		for (int i = 0; i < method.length() + 4; i++) {
			System.out.print("#");
		}
		System.out.println();
	}

}
