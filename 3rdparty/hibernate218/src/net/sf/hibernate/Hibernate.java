//$Id: Hibernate.java,v 1.21 2004/12/24 03:06:23 oneovthafew Exp $
package net.sf.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Iterator;

import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.engine.HibernateIterator;
import net.sf.hibernate.lob.BlobImpl;
import net.sf.hibernate.lob.ClobImpl;
import net.sf.hibernate.proxy.HibernateProxy;
import net.sf.hibernate.proxy.HibernateProxyHelper;
import net.sf.hibernate.type.BigDecimalType;
import net.sf.hibernate.type.BinaryType;
import net.sf.hibernate.type.BlobType;
import net.sf.hibernate.type.BooleanType;
import net.sf.hibernate.type.ByteType;
import net.sf.hibernate.type.CalendarDateType;
import net.sf.hibernate.type.CalendarType;
import net.sf.hibernate.type.CharacterType;
import net.sf.hibernate.type.ClassType;
import net.sf.hibernate.type.ClobType;
import net.sf.hibernate.type.CompositeCustomType;
import net.sf.hibernate.type.CurrencyType;
import net.sf.hibernate.type.CustomType;
import net.sf.hibernate.type.DateType;
import net.sf.hibernate.type.DoubleType;
import net.sf.hibernate.type.FloatType;
import net.sf.hibernate.type.IntegerType;
import net.sf.hibernate.type.LocaleType;
import net.sf.hibernate.type.LongType;
import net.sf.hibernate.type.ManyToOneType;
import net.sf.hibernate.type.NullableType;
import net.sf.hibernate.type.ObjectType;
import net.sf.hibernate.type.PersistentEnumType;
import net.sf.hibernate.type.SerializableType;
import net.sf.hibernate.type.ShortType;
import net.sf.hibernate.type.StringType;
import net.sf.hibernate.type.TextType;
import net.sf.hibernate.type.TimeType;
import net.sf.hibernate.type.TimeZoneType;
import net.sf.hibernate.type.TimestampType;
import net.sf.hibernate.type.TrueFalseType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.type.YesNoType;

/**
 * <ul>
 * <li>Provides access to the full range of Hibernate built-in types. <tt>Type</tt>
 * instances may be used to bind values to query parameters.
 * <li>A factory for new <tt>Blob</tt>s and <tt>Clob</tt>s.
 * <li>Defines static methods for manipulation of proxies.
 * </ul>
 * 
 * @see java.sql.Clob
 * @see java.sql.Blob
 * @see net.sf.hibernate.type.Type
 * @author Gavin King
 */

public final class Hibernate {
	
	/**
	 * Hibernate <tt>long</tt> type.
	 */
	public static final NullableType LONG = new LongType();
	/**
	 * Hibernate <tt>short</tt> type.
	 */
	public static final NullableType SHORT = new ShortType();
	/**
	 * Hibernate <tt>integer</tt> type.
	 */
	public static final NullableType INTEGER = new IntegerType();
	/**
	 * Hibernate <tt>byte</tt> type.
	 */
	public static final NullableType BYTE = new ByteType();
	/**
	 * Hibernate <tt>float</tt> type.
	 */
	public static final NullableType FLOAT = new FloatType();
	/**
	 * Hibernate <tt>double</tt> type.
	 */
	public static final NullableType DOUBLE = new DoubleType();
	/**
	 * Hibernate <tt>character</tt> type.
	 */
	public static final NullableType CHARACTER = new CharacterType();
	/**
	 * Hibernate <tt>string</tt> type.
	 */
	public static final NullableType STRING = new StringType();
	/**
	 * Hibernate <tt>time</tt> type.
	 */
	public static final NullableType TIME = new TimeType();
	/**
	 * Hibernate <tt>date</tt> type.
	 */
	public static final NullableType DATE = new DateType();
	/**
	 * Hibernate <tt>timestamp</tt> type.
	 */
	public static final NullableType TIMESTAMP = new TimestampType();
	/**
	 * Hibernate <tt>boolean</tt> type.
	 */
	public static final NullableType BOOLEAN = new BooleanType();
	/**
	 * Hibernate <tt>true_false</tt> type.
	 */
	public static final NullableType TRUE_FALSE = new TrueFalseType();
	/**
	 * Hibernate <tt>yes_no</tt> type.
	 */
	public static final NullableType YES_NO = new YesNoType();
	/**
	 * Hibernate <tt>big_decimal</tt> type.
	 */
	public static final NullableType BIG_DECIMAL = new BigDecimalType();
	/**
	 * Hibernate <tt>binary</tt> type.
	 */
	public static final NullableType BINARY = new BinaryType();
	/**
	 * Hibernate <tt>text</tt> type.
	 */
	public static final NullableType TEXT = new TextType();
	/**
	 * Hibernate <tt>blob</tt> type.
	 */
	public static final NullableType BLOB = new BlobType();
	/**
	 * Hibernate <tt>clob</tt> type.
	 */
	public static final NullableType CLOB = new ClobType();
	/**
	 * Hibernate <tt>calendar</tt> type.
	 */
	public static final NullableType CALENDAR = new CalendarType();
	/**
	 * Hibernate <tt>calendar_date</tt> type.
	 */
	public static final NullableType CALENDAR_DATE = new CalendarDateType();
	/**
	 * Hibernate <tt>locale</tt> type.
	 */
	public static final NullableType LOCALE = new LocaleType();
	/**
	 * Hibernate <tt>currency</tt> type.
	 */
	public static final NullableType CURRENCY = new CurrencyType();
	/**
	 * Hibernate <tt>timezone</tt> type.
	 */
	public static final NullableType TIMEZONE = new TimeZoneType();
	/**
	 * Hibernate <tt>class</tt> type.
	 */
	public static final NullableType CLASS = new ClassType();
	/**
	 * Hibernate <tt>serializable</tt> type.
	 */
	public static final NullableType SERIALIZABLE = new SerializableType(Serializable.class);
	/**
	 * Hibernate <tt>object</tt> type.
	 */
	public static final Type OBJECT = new ObjectType();
	
	
	/**
	 * Cannot be instantiated.
	 */
	private Hibernate() {
		throw new UnsupportedOperationException();
	}
	/**
	 * A Hibernate persistent enum type.
     * @deprecated Support for PersistentEnums will be removed in 2.2
	 */
	public static Type enum(Class enumClass) throws MappingException {
		return new PersistentEnumType(enumClass);
	}
	/**
	 * A Hibernate <tt>serializable</tt> type.
	 */
	public static Type serializable(Class serializableClass) {
		return new SerializableType(serializableClass);
	}
	/**
	 * A Hibernate <tt>any</tt> type.
	 *
	 * @param metaType a type mapping <tt>java.lang.Class</tt> to a single column
	 * @param identifierType the entity identifier type
	 * @return the Type
	 */
	public static Type any(Type metaType, Type identifierType) {
		return new ObjectType(metaType, identifierType);
	}
	/**
	 * A Hibernate persistent object (entity) type.
	 *
	 * @deprecated use <tt>Hibernate.entity()</tt>
	 * @param persistentClass a mapped entity class
	 * @return the Type
	 */
	public static Type association(Class persistentClass) {
		// not really a many-to-one association *necessarily*
		return new ManyToOneType(persistentClass);
	}
	/**
	 * A Hibernate persistent object (entity) type.
	 *
	 * @param persistentClass a mapped entity class
	 */
	public static Type entity(Class persistentClass) {
		// not really a many-to-one association *necessarily*
		return new ManyToOneType(persistentClass);
	}
	/**
	 * A Hibernate custom type.
	 *
	 * @param userTypeClass a class that implements <tt>UserType</tt>
	 */
	public static Type custom(Class userTypeClass) throws HibernateException {
		if ( CompositeUserType.class.isAssignableFrom(userTypeClass) ) {
			return new CompositeCustomType(userTypeClass);
		}
		else {
			return new CustomType(userTypeClass);
		}
	}
	
	/**
	 * Force initialization of a proxy or persistent collection.
	 *
	 * @param proxy a persistable object, proxy, persistent collection or <tt>null</tt>
	 * @throws HibernateException if we can't initialize the proxy at this time, eg. the <tt>Session</tt> was closed
	 */
	public static void initialize(Object proxy) throws HibernateException {
		if (proxy==null) {
			return;
		}
		else if ( proxy instanceof HibernateProxy ) {
			HibernateProxyHelper.getLazyInitializer( (HibernateProxy) proxy ).initialize();
		}
		else if ( proxy instanceof PersistentCollection ) {
			( (PersistentCollection) proxy ).forceInitialization();
		}
	}
	
	/**
	 * Chekc if the proxy or persistent collection is initialized.
	 *
	 * @param proxy a persistable object, proxy, persistent collection or <tt>null</tt>
	 * @return true if the argument is already initialized, or is not a proxy or collection
	 */
	public static boolean isInitialized(Object proxy) {
		if ( proxy instanceof HibernateProxy ) {
			return !HibernateProxyHelper.getLazyInitializer( (HibernateProxy) proxy ).isUninitialized();
		}
		else if ( proxy instanceof PersistentCollection ) {
			return ( (PersistentCollection) proxy ).wasInitialized();
		}
		else {
			return true;
		}
	}
	
	/**
	 * Get the true, underlying class of a proxied persistent class. This operation
	 * will initialize a proxy by side-effect.
	 *
	 * @param proxy a persistable object or proxy
	 * @return the true class of the instance
	 * @throws HibernateException
	 */
	public static Class getClass(Object proxy) {
		if (proxy instanceof HibernateProxy) {
			return HibernateProxyHelper.getLazyInitializer( (HibernateProxy) proxy ).getImplementation().getClass();
		}
		else {
			return proxy.getClass();
		}
	}
	
	/**
	 * Create a new <tt>Blob</tt>. The returned object will be initially immutable.
	 *
	 * @param bytes a byte array
	 * @return the Blob
	 */
	public static Blob createBlob(byte[] bytes) {
		return new BlobImpl(bytes);
	}
	
	/**
	 * Create a new <tt>Blob</tt>. The returned object will be initially immutable.
	 *
	 * @param stream a binary stream
	 * @param length the number of bytes in the stream
	 * @return the Blob
	 */
	public static Blob createBlob(InputStream stream, int length) {
		return new BlobImpl(stream, length);
	}
	
	/**
	 * Create a new <tt>Blob</tt>. The returned object will be initially immutable.
	 *
	 * @param stream a binary stream
	 * @return the Blob
	 * @throws IOException
	 */
	public static Blob createBlob(InputStream stream) throws IOException {
		return new BlobImpl( stream, stream.available() );
	}
	
	/**
	 * Create a new <tt>Clob</tt>. The returned object will be initially immutable.
	 *
	 * @param string a <tt>String</tt>
	 */
	public static Clob createClob(String string) {
		return new ClobImpl(string);
	}

	/**
	 * Create a new <tt>Clob</tt>. The returned object will be initially immutable.
	 *
	 * @param reader a character stream
	 * @param length the number of characters in the stream
	 */
	public static Clob createClob(Reader reader, int length) {
		return new ClobImpl(reader, length);
	}
	
	/**
	 * Close an <tt>Iterator</tt> created by <tt>iterate()</tt> immediately,
	 * instead of waiting until the session is closed or disconnected.
	 * 
	 * @see Session#iterate(java.lang.String)
	 * @see Query#iterate()
	 * @param iterator an <tt>Iterator</tt> created by <tt>iterate()</tt>
	 * @throws HibernateException
	 */
	public static void close(Iterator iterator) throws HibernateException {
		if (iterator instanceof HibernateIterator) {
			( (HibernateIterator) iterator ).close();
		}
		else {
			throw new IllegalArgumentException("not a Hibernate iterator");
		}
	}

}






