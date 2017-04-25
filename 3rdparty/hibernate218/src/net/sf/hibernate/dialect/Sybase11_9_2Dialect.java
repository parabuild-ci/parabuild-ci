//$Id: Sybase11_9_2Dialect.java,v 1.2 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.dialect;

import net.sf.hibernate.sql.JoinFragment;
import net.sf.hibernate.sql.Sybase11_9_2JoinFragment;

/**
 * A SQL dialect suitable for use with Sybase 11.9.2 (specifically: avoids ANSI JOIN syntax)
 * @author Colm O' Flaherty
 */
public class Sybase11_9_2Dialect extends SybaseDialect  {
	public Sybase11_9_2Dialect() {
		super();
	}

	public JoinFragment createOuterJoinFragment() {
		return new Sybase11_9_2JoinFragment();
	}

}
