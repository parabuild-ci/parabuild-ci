//$Id: QuerySelect.java,v 1.12 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.sql;

import java.util.HashSet;
import java.util.Iterator;

import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.util.StringHelper;

/**
 * A translated HQL query
 * @author Gavin King
 */
public class QuerySelect {
	private JoinFragment joins;
	private StringBuffer select = new StringBuffer();
	private StringBuffer where = new StringBuffer();
	private StringBuffer groupBy = new StringBuffer();
	private StringBuffer orderBy = new StringBuffer();
	private StringBuffer having = new StringBuffer();
	private boolean distinct=false;
	
	private static final HashSet DONT_SPACE_TOKENS = new HashSet();
	static {
		//dontSpace.add("'");
		DONT_SPACE_TOKENS.add(".");
		DONT_SPACE_TOKENS.add("+");
		DONT_SPACE_TOKENS.add("-");
		DONT_SPACE_TOKENS.add("/");
		DONT_SPACE_TOKENS.add("*");
		DONT_SPACE_TOKENS.add("<");
		DONT_SPACE_TOKENS.add(">");
		DONT_SPACE_TOKENS.add("=");
		DONT_SPACE_TOKENS.add("#");
		DONT_SPACE_TOKENS.add("~");
		DONT_SPACE_TOKENS.add("|");
		DONT_SPACE_TOKENS.add("&");
		DONT_SPACE_TOKENS.add("<=");
		DONT_SPACE_TOKENS.add(">=");
		DONT_SPACE_TOKENS.add("=>");
		DONT_SPACE_TOKENS.add("=<");
		DONT_SPACE_TOKENS.add("!=");
		DONT_SPACE_TOKENS.add("<>");
		DONT_SPACE_TOKENS.add("!#");
		DONT_SPACE_TOKENS.add("!~");
		DONT_SPACE_TOKENS.add("!<");
		DONT_SPACE_TOKENS.add("!>");
		DONT_SPACE_TOKENS.add(StringHelper.OPEN_PAREN); //for MySQL
		DONT_SPACE_TOKENS.add(StringHelper.CLOSE_PAREN);
	}
	
	public QuerySelect(Dialect dialect) {
		joins = new QueryJoinFragment(dialect, false);
	}
	
	public JoinFragment getJoinFragment() {
		return joins;
	}
	
	public void addSelectFragmentString(String fragment) {
		if ( fragment.length()>0 && fragment.charAt(0)==',' ) fragment = fragment.substring(1);
		fragment = fragment.trim();
		if ( fragment.length()>0 ) {
			if ( select.length()>0 ) select.append(StringHelper.COMMA_SPACE);
			select.append(fragment);
		}
	}
	
	public void addSelectColumn(String columnName, String alias) {
		addSelectFragmentString(columnName + ' ' + alias);
	}
	
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	
	public void setWhereTokens(Iterator tokens) {
		//if ( conjunctiveWhere.length()>0 ) conjunctiveWhere.append(" and ");
		appendTokens(where, tokens);
	}
		
	public void setGroupByTokens(Iterator tokens) {
		//if ( groupBy.length()>0 ) groupBy.append(" and ");
		appendTokens(groupBy, tokens);
	}
		
	public void setOrderByTokens(Iterator tokens) {
		//if ( orderBy.length()>0 ) orderBy.append(" and ");
		appendTokens(orderBy, tokens);
	}
		
	public void setHavingTokens(Iterator tokens) {
		//if ( having.length()>0 ) having.append(" and ");
		appendTokens(having, tokens);
	}
	
	public void addOrderBy(String orderByString) {
		if ( orderBy.length() > 0 ) orderBy.append(StringHelper.COMMA_SPACE); 
		orderBy.append(orderByString);
	}
		
	public String toQueryString() {
		StringBuffer buf = new StringBuffer(50)
			.append("select ");
		if (distinct) buf.append("distinct ");
		String from = joins.toFromFragmentString();
		if ( from.startsWith(",") ) {
			from = from.substring(1);
		}
		else if ( from.startsWith(" inner join") ){
			from = from.substring(11);
		}
		buf.append( select.toString() )
			.append(" from")
			.append(from);
		String part1 = joins.toWhereFragmentString().trim();
		String part2 = where.toString().trim();
		boolean hasPart1 = part1.length() > 0;
		boolean hasPart2 = part2.length() > 0;
		if (hasPart1 || hasPart2) buf.append(" where ");
		if (hasPart1) buf.append( part1.substring(4) );
		if (hasPart2) {
			if (hasPart1) buf.append(" and (");
			buf.append(part2);
			if (hasPart1) buf.append(")");
		}
		if ( groupBy.length() > 0 ) buf.append(" group by ").append( groupBy.toString() );
		if ( having.length() > 0 ) buf.append(" having ").append( having.toString() );
		if ( orderBy.length() > 0 ) buf.append(" order by ").append( orderBy.toString() );
		return buf.toString();
	}

	private static void appendTokens(StringBuffer buf, Iterator iter) {
		boolean lastSpaceable=true;
		boolean lastQuoted=false;
		while ( iter.hasNext() ) {
			String token = (String) iter.next();
			boolean spaceable = !DONT_SPACE_TOKENS.contains(token);
			boolean quoted = token.startsWith("'");
			if (spaceable && lastSpaceable) {
				if ( !quoted || !lastQuoted ) buf.append(' ');
			}
			lastSpaceable = spaceable;
			buf.append(token);
			lastQuoted = token.endsWith("'");
		}
	}
	
}
