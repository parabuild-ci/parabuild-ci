package net.sf.hibernate.id;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.TransientObjectException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;

/**
 * <b>foreign</b><br>
 * <br>
 * An <tt>Identifier</tt> generator that uses the value of the id property of an
 * associated object<br>
 * <br>
 * One mapping parameter is required: property.
 * 
 * @author Gavin King
 */
public class ForeignGenerator implements IdentifierGenerator, Configurable {
	
	private String propertyName;
	
	/**
	 * @see net.sf.hibernate.id.IdentifierGenerator#generate(net.sf.hibernate.engine.SessionImplementor, java.lang.Object)
	 */
	public Serializable generate(SessionImplementor session, Object object)
		throws SQLException, HibernateException {
		
		Object associatedObject = session.getFactory()
			.getClassMetadata( object.getClass() )
			.getPropertyValue(object,  propertyName);
		if (associatedObject==null) throw new IdentifierGenerationException(
			"attempted to assign id from null one-to-one property: " + propertyName
		);
		
		Serializable id;
		try {
			id = session.getEntityIdentifierIfNotUnsaved(associatedObject);
		}
		catch (TransientObjectException toe) {
			id = session.save(associatedObject);
		}
		
		if ( session.contains(object) ) {
			//abort the save (the object is already saved by a circular cascade)
			return IdentifierGeneratorFactory.SHORT_CIRCUIT_INDICATOR; 
			//throw new IdentifierGenerationException("save associated object first, or disable cascade for inverse association");
		}
		return id;
	}

	/**
	 * @see net.sf.hibernate.id.Configurable#configure(net.sf.hibernate.type.Type, java.util.Properties, net.sf.hibernate.dialect.Dialect)
	 */
	public void configure(Type type, Properties params, Dialect d)
		throws MappingException {
		
		propertyName = params.getProperty("property");
		if (propertyName==null) throw new MappingException(
			"param named \"property\" is required for foreign id generation strategy"
		);
	}

}
