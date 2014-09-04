package lars.xml.xpath.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lars.string.StringMatcher;

public class XpathElement {
	// private static final Pattern PREDICATE_REGEX = Pattern.compile("\\w+");

	public static final String word = "([a-zA-Z0-9\\*]+)";
	// public static final String number = "[1-9][0-9]*";
	// public static final String predicate_position = number;
	// public static final String predicate_last = "last\\(\\)";
	// public static final String predicate_last_but_x =
	// "last\\(\\)-"+number;
	// public static final String predicate_position =
	// "position\\(\\)[<>]"+number;
	public static final String predicate_attribute_present = "@" + word;
	public static final String predicate_attribute_equals = predicate_attribute_present
			+ "='([^']*)'";

	public static final String PREDICATE_REGEX = appendPredicate(predicate_attribute_present)
			+ "|" + appendPredicate(predicate_attribute_equals);
	public static final String PATH_REGEX = word + "(" + PREDICATE_REGEX + ")"
			+ "?";

	public static final String appendPredicate(String predicate) {
		return "\\[" + predicate + "\\]";
	}

	private String xpath;
	private Map<String, String> predicates;

	public XpathElement(String xpath) throws InvalidXpathException {
		parsePath(xpath);
	}

	private void parsePath(String xpath) throws InvalidXpathException {
		predicates = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(XpathElement.PATH_REGEX);
		Matcher m = pattern.matcher(xpath);
		if (m.matches()) {
			int group = 1;
			while (group <= m.groupCount() - 1 && m.group(group) == null) {
				group++;
			}
			this.xpath = m.group(group);
			parsePredicate(m.group(group + 1));
		} else {
			throw new InvalidXpathException();
		}
	}

	private void parsePredicate(String predicate) throws InvalidXpathException {
		if (predicate != null) {
			Pattern pattern = Pattern.compile(XpathElement.PREDICATE_REGEX);
			Matcher m = pattern.matcher(predicate);
			if (m.matches()) {
				int group = 1;
				while (group <= m.groupCount() - 1 && m.group(group) == null) {
					group++;
				}
				this.predicates.put(m.group(group), m.group(group + 1));
			} else {
				throw new InvalidXpathException();
			}
		}
	}

	public String getXpath() {
		return xpath;
	}

	public boolean matches(XpathElement xpathElement) {
		if (StringMatcher.wildcardStringMatch(xpathElement.getXpath(),
				this.xpath)) {
			for (String predicateName : this.predicates.keySet()) {
				String predicateValue = this.predicates.get(predicateName);
				if (!xpathElement.matchesAnyPredicate(predicateName,
						predicateValue)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean matchesAnyPredicate(String wildcardAllowedName,
			String wildcardAllowedValue) {
		for (String predicateName : this.predicates.keySet()) {
			if (StringMatcher.wildcardStringMatch(predicateName,
					wildcardAllowedName)) {
				String predicateValue = predicates.get(predicateName);
				return wildcardAllowedValue == null
						|| StringMatcher.wildcardStringMatch(predicateValue,
								wildcardAllowedValue);
			}
		}
		return false;
	}

	public boolean hasPredicates() {
		return this.predicates.keySet().size() > 0;
	}

	@Override
	public String toString() {
		String result = xpath;
		for (String predicateName : this.predicates.keySet()) {
			String predicateValue = predicates.get(predicateName);
			result += (predicateName != null ? "[" + "@" + predicateName + "="
					+ predicateValue + "]" : "");
		}
		return result;
	}

	public void addPredicate(String predicateName, String predicateValue) {
		this.predicates.put(predicateName, predicateValue);
	}

	public String getPredicate(String predicateName) {
		return this.predicates.get(predicateName);
	}

}
