//$Id: Formula.java,v 1.6 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.mapping;

import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.sql.Template;
import net.sf.hibernate.util.StringHelper;

/**
 * A formula is a derived column value
 * @author Gavin King
 */
public class Formula {
	private static int formulaUniqueInteger=0;
	
	private String formula;
	private int uniqueInteger;
	
	public Formula() {
		uniqueInteger = formulaUniqueInteger++;
	}
	
	public String getTemplate(Dialect dialect) {
		return Template.renderWhereStringTemplate(formula, dialect);
	}
	public String getAlias() {
		return "f" + Integer.toString(uniqueInteger) + StringHelper.UNDERSCORE;
	}
	public String getFormula() {
		return formula;
	}

	public void setFormula(String string) {
		formula = string;
	}

}
