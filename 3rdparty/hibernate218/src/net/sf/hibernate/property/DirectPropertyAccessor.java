//$Id: DirectPropertyAccessor.java,v 1.6 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.property;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.PropertyAccessException;
import net.sf.hibernate.PropertyNotFoundException;
import net.sf.hibernate.util.ReflectHelper;

/**
 * Accesses fields directly.
 * @author Gavin King
 */
public class DirectPropertyAccessor implements PropertyAccessor {
	
	public static final class DirectGetter implements Getter {
		private final Field field;
		private final Class clazz;
		private final String name;
		DirectGetter(Field field, Class clazz, String name) {
			this.field = field;
			this.clazz = clazz;
			this.name = name;
		}
		public Object get(Object target) throws HibernateException {
			try {
				return field.get(target);
			}
			catch (Exception e) {
				throw new PropertyAccessException(e, "could not get a field value by reflection", false, clazz, name);
			}
		}
		public Method getMethod() {
			return null;
		}
		public String getMethodName() {
			return null;
		}
		public Class getReturnType() {
			return field.getType();
		}

	}

	public static final class DirectSetter implements Setter {
		private final Field field;
		private final Class clazz;
		private final String name;
		DirectSetter(Field field, Class clazz, String name) {
			this.field = field;
			this.clazz = clazz;
			this.name = name;
		}
		public Method getMethod() {
			return null;
		}
		public String getMethodName() {
			return null;
		}
		public void set(Object target, Object value) throws HibernateException {
			try {
				field.set(target, value);
			}
			catch (Exception e) {
				throw new PropertyAccessException(e, "could not set a field value by reflection", true, clazz, name);
			}
		}

	}
	
	private static Field getField(Class clazz, String name) throws PropertyNotFoundException {
		if ( clazz==null || clazz==Object.class ) throw new PropertyNotFoundException("field not found: " + name);
		Field field;
		try {
			field = clazz.getDeclaredField(name);
		}
		catch (NoSuchFieldException nsfe) {
			field = getField( clazz.getSuperclass(), name );
		}
		if ( !ReflectHelper.isPublic(clazz, field) ) field.setAccessible(true);
		return field;
	}

	public Getter getGetter(Class theClass, String propertyName)
		throws PropertyNotFoundException {
		return new DirectGetter( getField(theClass, propertyName), theClass, propertyName );
	}

	public Setter getSetter(Class theClass, String propertyName)
		throws PropertyNotFoundException {
		return new DirectSetter( getField(theClass, propertyName), theClass, propertyName );
	}

}
