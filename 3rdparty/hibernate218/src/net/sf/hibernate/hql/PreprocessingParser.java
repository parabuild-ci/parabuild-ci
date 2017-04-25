//$Id: PreprocessingParser.java,v 1.11 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.hql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.QueryException;
import net.sf.hibernate.util.StringHelper;

/**
 *
 */
public class PreprocessingParser implements Parser {
	
	private static final Set HQL_OPERATORS;
	private static final Map HQL_COLLECTION_PROPERTIES;
	static {
		HQL_OPERATORS = new HashSet();
		HQL_OPERATORS.add("<=");
		HQL_OPERATORS.add(">=");
		HQL_OPERATORS.add("=>");
		HQL_OPERATORS.add("=<");
		HQL_OPERATORS.add("!=");
		HQL_OPERATORS.add("<>");
		HQL_OPERATORS.add("!#");
		HQL_OPERATORS.add("!~");
		HQL_OPERATORS.add("!<");
		HQL_OPERATORS.add("!>");
		HQL_OPERATORS.add("is not");
		HQL_OPERATORS.add("not like");
		HQL_OPERATORS.add("not in");
		HQL_OPERATORS.add("not between");
		HQL_OPERATORS.add("not exists");
		
		HQL_COLLECTION_PROPERTIES = new HashMap();
		HQL_COLLECTION_PROPERTIES.put("elements", "elements");
		HQL_COLLECTION_PROPERTIES.put("indices", "indices");
		HQL_COLLECTION_PROPERTIES.put("size", "size");
		HQL_COLLECTION_PROPERTIES.put("maxindex", "maxIndex");
		HQL_COLLECTION_PROPERTIES.put("minindex", "minIndex");
		HQL_COLLECTION_PROPERTIES.put("maxelement", "maxElement");
		HQL_COLLECTION_PROPERTIES.put("minelement", "minElement");

		HQL_COLLECTION_PROPERTIES.put("index", "index");

	}
	
	private Map replacements;
	private boolean quoted;
	private StringBuffer quotedString;
	private ClauseParser parser = new ClauseParser();
	private String lastToken;
	private String currentCollectionProp;
	
	public PreprocessingParser(Map replacements) {
		this.replacements=replacements;
	}
	
	/**
	 * @see net.sf.hibernate.hql.Parser#token(String, QueryTranslator)
	 */
	public void token(String token, QueryTranslator q) throws QueryException {
		
		//handle quoted strings
		if (quoted) {
			quotedString.append(token);
		}
		if ( "'".equals(token) ) {
			if (quoted) {
				token = quotedString.toString();
			}
			else {
				quotedString = new StringBuffer(20).append(token);
			}
			quoted = !quoted;
		}
		if (quoted) return;
		
		//ignore whitespace
		if ( ParserHelper.isWhitespace(token) ) return;
		
		//do replacements
		String substoken = (String) replacements.get(token);
		token = (substoken==null) ? token : substoken;
		
		//handle HQL2 collection syntax
		if ( currentCollectionProp!=null ) {
			if ( StringHelper.OPEN_PAREN.equals(token) ) {
				return;
			}
			else if ( StringHelper.CLOSE_PAREN.equals(token) ) {
				currentCollectionProp=null;
				return;
			}
			else {
				token =StringHelper.qualify(token, currentCollectionProp);
			}
		}
		else {
			String prop = (String) HQL_COLLECTION_PROPERTIES.get( token.toLowerCase() );
			if ( prop!=null ) {
				currentCollectionProp = prop;
				return;
			}
		}
		
		
		//handle <=, >=, !=, is not, not between, not in
		if (lastToken==null) {
			lastToken=token;
		}
		else {
			String doubleToken = (token.length()>1) ?
				lastToken + ' ' + token :
				lastToken + token;
			if ( HQL_OPERATORS.contains( doubleToken.toLowerCase() ) ) {
				parser.token(doubleToken, q);
				lastToken=null;
			}
			else {
				parser.token(lastToken, q);
				lastToken=token;
			}
		}
		
	}
	
	/**
	 * @see net.sf.hibernate.hql.Parser#start(QueryTranslator)
	 */
	public void start(QueryTranslator q) throws QueryException {
		quoted = false;
		parser.start(q);
	}
	
	/**
	 * @see net.sf.hibernate.hql.Parser#end(QueryTranslator)
	 */
	public void end(QueryTranslator q) throws QueryException {
		if (lastToken!=null) parser.token(lastToken, q);
		parser.end(q);
		lastToken=null;
		currentCollectionProp=null;
	}
	
}






