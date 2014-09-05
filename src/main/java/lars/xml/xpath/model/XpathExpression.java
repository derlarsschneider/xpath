package lars.xml.xpath.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class XpathExpression {
	private String xpathExpression;
	private List<XpathElement> elements;
	private int trailingSlashes = 0;

	public XpathExpression(String xpathExpression) throws InvalidXpathException {
		this.xpathExpression = xpathExpression;
		parse();
	}

	private void parse() throws InvalidXpathException {
		trailingSlashes = 0;
		while (trailingSlashes < this.xpathExpression.length()
				&& this.xpathExpression.charAt(trailingSlashes) == '/') {
			trailingSlashes++;
		}
		elements = new ArrayList<XpathElement>();
		if (trailingSlashes < this.xpathExpression.length()) {
			String[] split = this.xpathExpression.substring(trailingSlashes)
					.split("/");
			for (int i = 0; i < split.length; i++) {
				elements.add(new XpathElement(split[i]));
			}
		}
	}

	public void push(XpathElement element) {
		elements.add(element);
	}

	public XpathElement pop() {
		return elements.remove(elements.size() - 1);
	}

	public int size() {
		return elements.size();
	}

	public XpathElement get(int i) {
		return elements.get(i);
	}

	public boolean isMatchedBy(XpathExpression wildcardXpath) {
		if (this.size() != wildcardXpath.size()) {
			return false;
		}
		for (int i = 0; i < this.size(); i++) {
			XpathElement pathElement = this.get(i);
			XpathElement wildcardXpathElement = wildcardXpath.get(i);
			if (!pathElement.isMatchedBy(wildcardXpathElement)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return StringUtils.join(elements, '/');
	}

	public XpathElement getLeafElement() {
		return elements.get(elements.size() - 1);
	}

}
