//$Id: Parser.java,v 1.6 2004/06/04 01:27:39 steveebersole Exp $
package net.sf.hibernate.hql;

import net.sf.hibernate.QueryException;

/**
 * A parser is a state machine that accepts a string of tokens,
 * bounded by start() and end() and modifies a QueryTranslator. Parsers
 * are NOT intended to be threadsafe. They SHOULD be reuseable
 * for more than one token stream.
 */

public interface Parser {
	public void token(String token, QueryTranslator q) throws QueryException;
	public void start(QueryTranslator q) throws QueryException;
	public void end(QueryTranslator q) throws QueryException;
}







