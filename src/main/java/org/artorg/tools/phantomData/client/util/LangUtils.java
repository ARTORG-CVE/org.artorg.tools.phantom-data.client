package org.artorg.tools.phantomData.client.util;

public class LangUtils {
	
	/**
	 * Forces the initialization of the class pertaining to 
	 * the specified <tt>Class</tt> object. 
	 * This method does nothing if the class is already
	 * initialized prior to invocation.
	 *
	 * @param klass the class for which to force initialization
	 * @return <tt>klass</tt>

	 */
	public static <T> Class<T> forceInit(Class<T> klass) {
	    try {
	        Class.forName(klass.getName(), true, klass.getClassLoader());
	    } catch (ClassNotFoundException e) {
	        throw new AssertionError(e);  // Can't happen
	    }
	    return klass;
	} 

}
