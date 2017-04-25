//$Id: HavingParser.java,v 1.6 2004/06/04 01:27:39 steveebersole Exp $
package net.sf.hibernate.hql;

/**
 * Parses the having clause of a hibernate query and translates it to an
 * SQL having clause.
 */
public class HavingParser extends WhereParser {
	
	void appendToken(QueryTranslator q, String token) {
		q.appendHavingToken(token);
	}
	
}
