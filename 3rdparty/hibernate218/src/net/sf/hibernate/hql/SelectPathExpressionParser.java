//$Id: SelectPathExpressionParser.java,v 1.7 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.hql;

import net.sf.hibernate.QueryException;

public class SelectPathExpressionParser extends PathExpressionParser {
	
	public void end(QueryTranslator q) throws QueryException {
		if ( getCurrentProperty() != null && !q.isShallowQuery() ) {
			// "finish off" the join
			token(".", q);
			token(null, q);
		}
		super.end(q);
	}
	
	protected void setExpectingCollectionIndex() throws QueryException {
		throw new QueryException("expecting .elements or .indices after collection path expression in select");
	}
	
	public String getSelectName() {
		return getCurrentName();
	}
}







