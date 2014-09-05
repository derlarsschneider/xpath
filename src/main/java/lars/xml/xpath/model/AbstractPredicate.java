package lars.xml.xpath.model;

public abstract class AbstractPredicate implements Predicate {
	public abstract String getPredicate();

	public abstract String getName();

	public abstract Operator getOperator();

	public abstract String getValue();
}
