package org.artorg.tools.phantomData.client.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PersistentIntrospector {
	private final Class<?> beanClass;
	private final List<PersistentPropertyDescriptor> allDescriptors;
	private final List<PersistentPropertyDescriptor> entityDescriptors;
	private final List<PersistentPropertyDescriptor> baseDescriptors;
	
	public PersistentIntrospector(Class<?> beanClass) {
		this.beanClass = beanClass;
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(beanClass);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		allDescriptors = new ArrayList<PersistentPropertyDescriptor>();
		for (int i=0; i<descriptors.length; i++)
			allDescriptors.add(new PersistentPropertyDescriptor(descriptors[i], i));
		entityDescriptors = allDescriptors.stream().filter(d -> d.isEntity()).collect(Collectors.toList());
		baseDescriptors = allDescriptors.stream().filter(d -> !d.isEntity()).collect(Collectors.toList());
	}

	public List<PersistentPropertyDescriptor> getAllDescriptors() {
		return allDescriptors;
	}

	public List<PersistentPropertyDescriptor> getEntityDescriptors() {
		return entityDescriptors;
	}

	public List<PersistentPropertyDescriptor> getBaseDescriptors() {
		return baseDescriptors;
	}
	
	public Class<?> getBeanClass() {
		return beanClass;
	}
	
	public Object getValue(Object bean, int index) {
		return allDescriptors.get(index).getValue(bean);
	}
	
	public void setValue(Object bean, Object value, int index) {
		allDescriptors.get(index).setValue(bean, value);
	}

}
