package lars.xml.xpath.model;

public interface Predicate {

	public abstract String getName();

	public abstract Operator getOperator();

	public abstract String getValue();

	public abstract boolean isMatchedBy(Predicate wildcardXpathElementPredicate);

	public abstract boolean anyValue();

}
