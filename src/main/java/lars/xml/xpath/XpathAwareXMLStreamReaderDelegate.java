package lars.xml.xpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import lars.xml.xpath.model.AttributePredicate;
import lars.xml.xpath.model.InvalidXpathException;
import lars.xml.xpath.model.XpathElement;
import lars.xml.xpath.model.XpathExpression;

public class XpathAwareXMLStreamReaderDelegate implements XMLStreamReader {
	protected XMLStreamReader readerDelegate;
	private XpathExpression xpathStack;
	private File file;
	private XpathExpression xpathExpression;

	public XpathAwareXMLStreamReaderDelegate(File file)
			throws XMLStreamException, FactoryConfigurationError,
			FileNotFoundException {
		super();
		this.file = file;

		reset();
	}

	public void reset() throws FileNotFoundException, XMLStreamException,
			FactoryConfigurationError {
		this.readerDelegate = XMLInputFactory.newInstance()
				.createXMLStreamReader(
						new FileInputStream(this.file.getAbsoluteFile()));
		try {
			this.xpathStack = new XpathExpression("/");
		} catch (InvalidXpathException e) {
			e.printStackTrace();
		}
	}

	private void updateXpath() {
		if (this.isStartElement()) {
			try {
				XpathElement element = new XpathElement(this.getLocalName());
				for (int i = 0; i < this.getAttributeCount(); i++) {
					element.addPredicate(AttributePredicate
							.createEqualsAttributePredicate(this
									.getAttributeName(i).getLocalPart(), this
									.getAttributeValue(i)));
				}
				xpathStack.push(element);
			} catch (InvalidXpathException e) {
				e.printStackTrace();
			}
		} else if (this.isEndElement()) {
			String poppedPathElement = xpathStack.pop().getXpath();
			assert (this.getLocalName().equals(poppedPathElement));
		}
	}

	public int nextMatch() throws XMLStreamException {
		while (this.hasNext()) {
			int nodeType = this.next();
			if (xpathMatches()) {
				return nodeType;
			}
		}
		return -1;
	}

	public void setXpathExpression(XpathExpression xpath) {
		this.xpathExpression = xpath;
	}

	private boolean xpathMatches() {
		// return this.isStartElement() && xpathExpression.matches(xpathStack);
		return this.isStartElement() && xpathStack.isMatchedBy(xpathExpression);

	}

	public int count() throws XMLStreamException {
		int count = 0;
		while (nextMatch() != -1) {
			count++;
		}
		return count;
	}

	public String subtree() {
		try {
			String result = "";
			int depth = 0;
			do {
				if (this.isStartElement()) {
					depth++;
					result += "<" + this.getLocalName();
					for (int i = 0; i < this.getAttributeCount(); i++) {
						result += " " + this.getAttributeLocalName(i);
						result += "=\"" + this.getAttributeValue(i) + "\"";
					}
					result += ">";
				}
				if (this.isCharacters()) {
					result += this.getText();
				}
				if (this.isEndElement()) {
					depth--;
					result += "</" + this.getLocalName() + ">";
				}
				this.next();
			} while (depth > 0);
			return result;
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void printSubtree() {
		try {
			int depth = 0;
			do {
				if (this.isStartElement()) {
					depth++;
					System.out.print("<" + this.getLocalName());
					for (int i = 0; i < this.getAttributeCount(); i++) {
						System.out.print(" " + this.getAttributeLocalName(i));
						System.out.print("=\"" + this.getAttributeValue(i)
								+ "\"");
					}
					System.out.print(">");
				}
				if (this.isCharacters()) {
					System.out.print(this.getText());
				}
				if (this.isEndElement()) {
					depth--;
					System.out.print("</" + this.getLocalName() + ">");
				}
				this.next();
			} while (depth > 0);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		if (this.isStartElement()) {
			return "StartElement: " + this.xpathStack;
		} else if (this.isEndElement()) {
			return "EndElement: " + this.xpathStack;
		} else if (this.isCharacters()) {
			return "Characters: " + this.xpathStack + "; " + this.getText();
		} else {
			return super.toString();
		}
	}

	public void close() throws XMLStreamException {
		readerDelegate.close();
	}

	public int getAttributeCount() {
		return readerDelegate.getAttributeCount();
	}

	public String getAttributeLocalName(int arg0) {
		return readerDelegate.getAttributeLocalName(arg0);
	}

	public QName getAttributeName(int arg0) {
		return readerDelegate.getAttributeName(arg0);
	}

	public String getAttributeNamespace(int arg0) {
		return readerDelegate.getAttributeNamespace(arg0);
	}

	public String getAttributePrefix(int arg0) {
		return readerDelegate.getAttributePrefix(arg0);
	}

	public String getAttributeType(int arg0) {
		return readerDelegate.getAttributeType(arg0);
	}

	public String getAttributeValue(int arg0) {
		return readerDelegate.getAttributeValue(arg0);
	}

	public String getAttributeValue(String arg0, String arg1) {
		return readerDelegate.getAttributeValue(arg0, arg1);
	}

	public String getCharacterEncodingScheme() {
		return readerDelegate.getCharacterEncodingScheme();
	}

	public String getElementText() throws XMLStreamException {
		return readerDelegate.getElementText();
	}

	public String getEncoding() {
		return readerDelegate.getEncoding();
	}

	public int getEventType() {
		return readerDelegate.getEventType();
	}

	public String getLocalName() {
		return readerDelegate.getLocalName();
	}

	public Location getLocation() {
		return readerDelegate.getLocation();
	}

	public QName getName() {
		return readerDelegate.getName();
	}

	public NamespaceContext getNamespaceContext() {
		return readerDelegate.getNamespaceContext();
	}

	public int getNamespaceCount() {
		return readerDelegate.getNamespaceCount();
	}

	public String getNamespacePrefix(int arg0) {
		return readerDelegate.getNamespacePrefix(arg0);
	}

	public String getNamespaceURI() {
		return readerDelegate.getNamespaceURI();
	}

	public String getNamespaceURI(String arg0) {
		return readerDelegate.getNamespaceURI(arg0);
	}

	public String getNamespaceURI(int arg0) {
		return readerDelegate.getNamespaceURI(arg0);
	}

	public String getPIData() {
		return readerDelegate.getPIData();
	}

	public String getPITarget() {
		return readerDelegate.getPITarget();
	}

	public String getPrefix() {
		return readerDelegate.getPrefix();
	}

	public Object getProperty(String arg0) throws IllegalArgumentException {
		return readerDelegate.getProperty(arg0);
	}

	public String getText() {
		return readerDelegate.getText();
	}

	public char[] getTextCharacters() {
		return readerDelegate.getTextCharacters();
	}

	public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3)
			throws XMLStreamException {
		return readerDelegate.getTextCharacters(arg0, arg1, arg2, arg3);
	}

	public int getTextLength() {
		return readerDelegate.getTextLength();
	}

	public int getTextStart() {
		return readerDelegate.getTextStart();
	}

	public String getVersion() {
		return readerDelegate.getVersion();
	}

	public boolean hasName() {
		return readerDelegate.hasName();
	}

	public boolean hasNext() throws XMLStreamException {
		return readerDelegate.hasNext();
	}

	public boolean hasText() {
		return readerDelegate.hasText();
	}

	public boolean isAttributeSpecified(int arg0) {
		return readerDelegate.isAttributeSpecified(arg0);
	}

	public boolean isCharacters() {
		return readerDelegate.isCharacters();
	}

	public boolean isEndElement() {
		return readerDelegate.isEndElement();
	}

	public boolean isStandalone() {
		return readerDelegate.isStandalone();
	}

	public boolean isStartElement() {
		return readerDelegate.isStartElement();
	}

	public boolean isWhiteSpace() {
		return readerDelegate.isWhiteSpace();
	}

	public int next() throws XMLStreamException {
		int next = readerDelegate.next();
		updateXpath();
		return next;
	}

	public int nextTag() throws XMLStreamException {
		int nextTag = readerDelegate.nextTag();
		updateXpath();
		return nextTag;
	}

	public void require(int arg0, String arg1, String arg2)
			throws XMLStreamException {
		readerDelegate.require(arg0, arg1, arg2);

	}

	public boolean standaloneSet() {
		return readerDelegate.standaloneSet();
	}

}
