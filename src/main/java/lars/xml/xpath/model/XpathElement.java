package lars.xml.xpath.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lars.string.StringMatcher;

public class XpathElement {
	private static final int GROUP_PATH = 1;
	private static final int GROUP_PREDICATE = 2;

	public static final String PATTERN = "([a-zA-Z0-9\\*]+)" + "("
			+ AttributePredicate.PATTERN + ")?";

	private String xpath;
	private List<Predicate> predicates;

	public XpathElement(String xpath) throws InvalidXpathException {
		parsePath(xpath);
	}

	private void parsePath(String xpath) throws InvalidXpathException {
		// predicates_legacy = new HashMap<String, String>();
		predicates = new ArrayList<Predicate>();
		Pattern pattern = Pattern.compile(XpathElement.PATTERN);
		Matcher m = pattern.matcher(xpath);
		if (m.matches()) {
			this.xpath = m.group(XpathElement.GROUP_PATH);
			String predicateString = m.group(XpathElement.GROUP_PREDICATE);
			if (predicateString != null) {
				this.addPredicate(new AttributePredicate(predicateString));
			}
		} else {
			throw new InvalidXpathException();
		}
	}

	public String getXpath() {
		return xpath;
	}

	/**
	 * Determines whether this predicates all matches a predicate in foreign
	 * xpath element.
	 * 
	 * @param xpathElement
	 * @return
	 */
	public boolean isMatchedBy(XpathElement wildcardXpathElement) {
		if (StringMatcher.wildcardStringMatch(this.getXpath(),
				wildcardXpathElement.getXpath())) {
			for (Predicate wildcardXpathElementPredicate : wildcardXpathElement.predicates) {
				boolean wildcardXpathElementPredicateMatched = false;
				for (Predicate predicate : this.predicates) {
					if (predicate.isMatchedBy(wildcardXpathElementPredicate)) {
						wildcardXpathElementPredicateMatched = true;
						continue;
					}
				}
				if (!wildcardXpathElementPredicateMatched) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean hasPredicates() {
		return this.predicates.size() > 0;
	}

	@Override
	public String toString() {
		String result = xpath;
		for (Predicate predicate : this.predicates) {
			String predicateName = predicate.getName();
			String predicateValue = predicate.getValue();
			result += (predicateName != null ? "[" + "@" + predicateName + "="
					+ predicateValue + "]" : "");
		}
		return result;
	}

	public Predicate getPredicate(String predicateName) {
		for (Predicate predicate : this.predicates) {
			if (predicate.getName().equals(predicateName)) {
				return predicate;
			}
		}
		return null;
	}

	public void addPredicate(AttributePredicate attributePredicate) {
		this.predicates.add(attributePredicate);
	}

}
