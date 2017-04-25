//$Id: FromPathExpressionParser.java,v 1.5 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.hql;

import net.sf.hibernate.QueryException;
import net.sf.hibernate.collection.CollectionPropertyMapping;
import net.sf.hibernate.type.Type;

public class FromPathExpressionParser extends PathExpressionParser {

	public void end(QueryTranslator q) throws QueryException {
		if ( !isCollectionValued() ) {
			Type type = getPropertyType();
			if ( type.isEntityType() ) {
				// "finish off" the join
				token(".", q);
				token(null, q);
			}
			else if ( type.isPersistentCollectionType() ) {
				// default to element set if no elements() specified
				token(".", q);
				token(CollectionPropertyMapping.COLLECTION_ELEMENTS, q);
			}
		}
		super.end(q);
	}
	
	protected void setExpectingCollectionIndex() throws QueryException {
		throw new QueryException("expecting .elements or .indices after collection path expression in from");
	}
	

}
