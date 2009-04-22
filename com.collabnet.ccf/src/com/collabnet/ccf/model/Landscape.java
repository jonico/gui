package com.collabnet.ccf.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.osgi.service.prefs.Preferences;

public class Landscape implements IPropertySource {
	private String description;
	private String type1;
	private String type2;
	private String configurationFolder;
	
	private Preferences node;
	
	public static String P_ID_DESCRIPTION = "desc"; //$NON-NLS-1$
	public static String P_DESCRIPTION = "Description";
	public static String P_ID_TYPE1 = "type1"; //$NON-NLS-1$
	public static String P_TYPE1 = "System 1 type";
	public static String P_ID_TYPE2 = "type2"; //$NON-NLS-1$
	public static String P_TYPE2 = "System 2 type";
	public static String P_ID_FOLDER = "folder"; //$NON-NLS-1$
	public static String P_FOLDER = "Configuration folder";
	
	public static List<PropertyDescriptor> descriptors;
	static
	{	
		descriptors = new ArrayList<PropertyDescriptor>();
		descriptors.add(new PropertyDescriptor(P_ID_DESCRIPTION, P_DESCRIPTION));
		descriptors.add(new PropertyDescriptor(P_ID_TYPE1, P_TYPE1));
		descriptors.add(new PropertyDescriptor(P_ID_TYPE2, P_TYPE2));
		descriptors.add(new PropertyDescriptor(P_ID_FOLDER, P_FOLDER));
	}		

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType1() {
		return type1;
	}
	public void setType1(String type1) {
		this.type1 = type1;
	}
	public String getType2() {
		return type2;
	}
	public void setType2(String type2) {
		this.type2 = type2;
	}
	public String getConfigurationFolder() {
		return configurationFolder;
	}
	public void setConfigurationFolder(String configurationFolder) {
		this.configurationFolder = configurationFolder;
	}
	
	public Preferences getNode() {
		return node;
	}
	
	public void setNode(Preferences node) {
		this.node = node;
	}

	
	public Object getEditableValue() {
		return description;
	}
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return (IPropertyDescriptor[])getDescriptors().toArray(new IPropertyDescriptor[getDescriptors().size()]);
	}
	
	private static List<PropertyDescriptor> getDescriptors() {
		return descriptors;
	}	
	
	public Object getPropertyValue(Object id) {
		if (P_ID_DESCRIPTION.equals(id)) return description;
		if (P_ID_TYPE1.equals(id)) return type1;
		if (P_ID_TYPE2.equals(id)) return type2;
		if (P_ID_FOLDER.equals(id)) return configurationFolder;
		return null;
	}
	
	public boolean isPropertySet(Object id) {
		return false;
	}
	
	public void resetPropertyValue(Object id) {
	}
	
	public void setPropertyValue(Object id, Object value) {
	}
}
