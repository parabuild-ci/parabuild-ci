//$Id: GroupByParser.java,v 1.10 2004/06/04 01:27:39 steveebersole Exp $
package net.sf.hibernate.hql;

import net.sf.hibernate.QueryException;
import net.sf.hibernate.util.StringHelper;

/**
 * Parses the GROUP BY clause of an aggregate query
 */
public class GroupByParser implements Parser {
	
	//this is basically a copy/paste of OrderByParser ... might be worth refactoring
	
	// This uses a PathExpressionParser but notice that compound paths are not valid,
	// only bare names and simple paths:
	
	// SELECT p FROM p IN CLASS eg.Person GROUP BY p.Name, p.Address, p
	
	// The reason for this is SQL doesn't let you sort by an expression you are
	// not returning in the result set.
	
	private final PathExpressionParser pathExpressionParser; 
	{
		pathExpressionParser = new PathExpressionParser();
		pathExpressionParser.setUseThetaStyleJoin(true); //TODO: would be nice to use false, but issues with MS SQL
	}
	
	public void token(String token, QueryTranslator q) throws QueryException {
		
		if ( q.isName( StringHelper.root(token) ) ) {
			ParserHelper.parse(pathExpressionParser, q.unalias(token), ParserHelper.PATH_SEPARATORS, q);
			q.appendGroupByToken( pathExpressionParser.getWhereColumn() );
			pathExpressionParser.addAssociation(q);
		}
		else {
			q.appendGroupByToken(token);
		}
	}
	
	public void start(QueryTranslator q) throws QueryException {
	}
	
	public void end(QueryTranslator q) throws QueryException {
	}
	
	
}





