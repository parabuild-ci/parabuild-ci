//$Id: MessageHelper.java,v 1.9 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.impl;

import java.io.Serializable;

import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.persister.ClassPersister;

/**
 * Helper methods for rendering log messages and exception
 * messages.
 * @author Max Andersen, Gavin King
 */
public final class MessageHelper {
	
	//TODO: in the long run, this should all be changed to use 
	//      Type.toString() for printing ids
	
	private MessageHelper() {}
	
	public static String infoString(Class clazz, Serializable id) {
		StringBuffer s = new StringBuffer();
		s.append('[');
		if(clazz==null) {
			s.append("<null Class>");
		}
		else {
			s.append( clazz.getName() );
		}
		s.append('#');
		
		if (id==null) {
			s.append("<null>");
		}
		else {
			s.append(id);
		}
		s.append(']');
		
		return s.toString();
	}

	/**
	 * Generate small message that can be used in traces and exception
	 * messages.
	 * @param persister The persister for the class in question
	 * @param id The id
	 * @return String on the form [FooBar#id]
	 */
	public static String infoString(ClassPersister persister, Serializable id) {
		StringBuffer s = new StringBuffer();
		s.append('[');
		if(persister==null) {
			s.append("<null ClassPersister>");
		}
		else {
			s.append( persister.getClassName() );
		}
		s.append('#');
		
		if (id==null) {
			s.append("<null>");
		}
		else {
			s.append(id);
		}
		s.append(']');
		
		return s.toString();
		
	}

	public static String infoString(ClassPersister persister) {
		StringBuffer s = new StringBuffer();
		s.append('[');
		if (persister == null) {
			s.append("<null ClassPersister>");
		}
		else {
			s.append( persister.getClassName() );
		}
		s.append(']');
		return s.toString();
	}

	public static String infoString(CollectionPersister persister, Serializable id) {
		StringBuffer s = new StringBuffer();
		s.append('[');
		if(persister==null) {
			s.append("<unreferenced>");
		}
		else {
			s.append( persister.getRole() );
			s.append('#');
			
			if (id==null) {
				s.append("<null>");
			}
			else {
				s.append(id);
			}
		}
		s.append(']');
		
		return s.toString();
		
	}
}
