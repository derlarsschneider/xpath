package lars.xml.xpath.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lars.string.StringMatcher;

public class AttributePredicate implements Predicate {
	private static final String SINGLE_PREDICATE = "@([a-zA-Z0-9\\*]+)(("
			+ Operator.EQUALS.getValue() + "|" + Operator.NOT_EQUALS.getValue()
			+ ")'([a-zA-Z0-9\\*]+)')?";
	public static final String PATTERN = "\\["
			+ AttributePredicate.SINGLE_PREDICATE + "\\]";
	private static final int GROUP_NAME = 1;
	private static final int GROUP_OPERATOR = 3;
	private static final int GROUP_VALUE = 4;

	private String predicate;
	private String name;
	private Operator operator;
	private String value;

	private AttributePredicate() {
		super();
	}

	public AttributePredicate(String predicate) throws InvalidXpathException {
		super();
		this.predicate = predicate;
		parsePredicate(predicate);
	}

	public static AttributePredicate createEqualsAttributePredicate(
			String name, String value) {
		AttributePredicate p = new AttributePredicate();
		p.name = name;
		p.operator = Operator.EQUALS;
		p.value = value;
		return p;
	}

	private void parsePredicate(String predicate) throws InvalidXpathException {
		if (predicate != null) {
			Pattern pattern = Pattern.compile(AttributePredicate.PATTERN);
			Matcher m = pattern.matcher(predicate);
			if (m.matches()) {
				name = m.group(GROUP_NAME);
				String operatorString = m.group(GROUP_OPERATOR);
				operator = Operator.fromString(operatorString);
				value = m.group(GROUP_VALUE);
			} else {
				throw new InvalidXpathException();
			}
		}
	}

	public String getPredicate() {
		return predicate;
	}

	public String getName() {
		return name;
	}

	public Operator getOperator() {
		return operator;
	}

	public String getValue() {
		return value;
	}

	public boolean anyValue() {
		return operator == null || value == null;
	}

	public boolean isMatchedBy(Predicate wildcardXpathElementPredicate) {
		if (isNameMatchedBy(wildcardXpathElementPredicate)) {
			if (wildcardXpathElementPredicate.anyValue()) {
				return true;
			} else {
				switch (wildcardXpathElementPredicate.getOperator()) {
				case EQUALS:
					return StringMatcher.wildcardStringMatch(this.getValue(),
							wildcardXpathElementPredicate.getValue());
				case NOT_EQUALS:
					return !StringMatcher.wildcardStringMatch(this.getValue(),
							wildcardXpathElementPredicate.getValue());
				default:
					return false;
				}
			}
		} else {
			return false;
		}
	}

	private boolean isNameMatchedBy(Predicate wildcardXpathElementPredicate) {
		return StringMatcher.wildcardStringMatch(this.getName(),
				wildcardXpathElementPredicate.getName());
	}
}
