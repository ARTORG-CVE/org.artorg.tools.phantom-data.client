package org.artorg.tools.phantomData.client.beans;

import java.beans.PropertyDescriptor;

public class PersistentPropertyDescriptor {
	private final PropertyDescriptor descriptor;
	private final int index;

	public PersistentPropertyDescriptor(PropertyDescriptor descriptor, int index) {
		this.descriptor = descriptor;
		this.index = index;
	}
	
	public boolean isEntity() {
		return descriptor.getPropertyType().isAnnotationPresent(javax.persistence.Entity.class);
	}
	
	public Object getValue(Object bean) {
		return descriptor.createPropertyEditor(bean).getValue();
	}
	
	public void setValue(Object bean, Object value) {
		descriptor.createPropertyEditor(bean).setValue(value);
	}
	
	public Class<?> getType() {
		return descriptor.getPropertyType();
	}
	
	public PropertyDescriptor getDescriptor() {
		return descriptor;
	}

	public int getIndex() {
		return index;
	}
	

}
