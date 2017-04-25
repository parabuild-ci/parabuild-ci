//$Id: ParserHelper.java,v 1.10 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.hql;

import java.util.StringTokenizer;

import net.sf.hibernate.QueryException;

public final class ParserHelper {
	
	public static final String HQL_VARIABLE_PREFIX = ":";
	
	public static final String HQL_SEPARATORS = " \n\r\f\t,()=<>&|+-=/*'^![]#~\\";
	//NOTICE: no " or . since they are part of (compound) identifiers
	public static final String PATH_SEPARATORS = ".";
	
	public static final String WHITESPACE = " \n\r\f\t";
	
	public static boolean isWhitespace(String str) {
		return WHITESPACE.indexOf(str) > -1;
	}
	
	private ParserHelper() {
		//cannot instantiate
	}
	
	public static void parse(Parser p, String text, String seperators, QueryTranslator q) throws QueryException {
		StringTokenizer tokens = new StringTokenizer(text, seperators, true);
		p.start(q);
		while ( tokens.hasMoreElements() ) p.token( tokens.nextToken(), q );
		p.end(q);
	}
	
}






