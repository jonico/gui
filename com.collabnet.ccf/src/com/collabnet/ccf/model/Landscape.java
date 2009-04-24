package com.collabnet.ccf.model;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.osgi.service.prefs.Preferences;

import com.collabnet.ccf.Activator;

public class Landscape implements IPropertySource {
	private String description;
	private String type1;
	private String type2;
	private String configurationFolder1;
	private String configurationFolder2;
	private String contributorId;
	private Preferences node;
	
	public final static String TYPE_QC = "QC";
	public final static String TYPE_TF = "TF";
	public final static String TYPE_PT = "PT";
	
	public static String P_ID_DESCRIPTION = "desc"; //$NON-NLS-1$
	public static String P_DESCRIPTION = "Description";
	public static String P_ID_TYPE1 = "type1"; //$NON-NLS-1$
	public static String P_TYPE1 = "System 1 type";
	public static String P_ID_TYPE2 = "type2"; //$NON-NLS-1$
	public static String P_TYPE2 = "System 2 type";
	public static String P_ID_FOLDER1 = "folder1"; //$NON-NLS-1$
	public static String P_FOLDER1 = "Configuration folder 1";
	public static String P_ID_FOLDER2 = "folder2"; //$NON-NLS-1$
	public static String P_FOLDER2 = "Configuration folder 2";
	
	public static List<PropertyDescriptor> descriptors;
	static
	{	
		descriptors = new ArrayList<PropertyDescriptor>();
		descriptors.add(new PropertyDescriptor(P_ID_DESCRIPTION, P_DESCRIPTION));
		descriptors.add(new PropertyDescriptor(P_ID_TYPE1, P_TYPE1));
		descriptors.add(new PropertyDescriptor(P_ID_TYPE2, P_TYPE2));
		descriptors.add(new PropertyDescriptor(P_ID_FOLDER1, P_FOLDER1));
		descriptors.add(new PropertyDescriptor(P_ID_FOLDER2, P_FOLDER2));
	}		

	public String getDescription() {
		return description.replaceAll("%slash%", "/");
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
		if (configurationFolder1 == null) return configurationFolder2;
		else return configurationFolder1;
	}
	public String getConfigurationFolder1() {
		return configurationFolder1;
	}
	public void setConfigurationFolder1(String configurationFolder1) {
		this.configurationFolder1 = configurationFolder1;
	}
	public String getConfigurationFolder2() {
		return configurationFolder2;
	}
	public void setConfigurationFolder2(String configurationFolder2) {
		this.configurationFolder2 = configurationFolder2;
	}	
	public Preferences getNode() {
		return node;
	}
	
	public void setNode(Preferences node) {
		this.node = node;
	}

	public String getContributorId() {
		return contributorId;
	}
	
	public void setContributorId(String contributorId) {
		this.contributorId = contributorId;
	}
	
	public String getId1() {
		String id = null;
		if (configurationFolder1 == null) return null;
		File folder = new File(configurationFolder1);
		String propertyFile = null;
		String defaultId = null;
		if (type1.equals(TYPE_QC)) {
			propertyFile = "qc.properties";
			defaultId = "QC";
		}
		else if (type1.equals(TYPE_TF)) {
			propertyFile = "sfee.properties";
			defaultId = "TeamForge";
		}
		else if (type1.equals(TYPE_PT)) {
			propertyFile = "cee.properties";
			defaultId = "CEE";
		}
		if (propertyFile != null) {
			File propertiesFile = new File(folder, propertyFile);
			if (propertiesFile.exists()) {
				try {
					FileInputStream inputStream = new FileInputStream(propertiesFile);
					Properties properties = new Properties();
					properties.load(inputStream);
					inputStream.close();
					id = properties.getProperty(Activator.PROPERTIES_SYSTEM_ID, defaultId);
				} catch (Exception e) { 
					Activator.handleError(e);
				}
			}
		}
		return id;
	}
	
	public String getId2() {
		String id = null;
		if (configurationFolder2 == null) return null;
		File folder = new File(configurationFolder2);
		String propertyFile = null;
		String defaultId = null;
		if (type2.equals(TYPE_QC)) {
			propertyFile = "qc.properties";
			defaultId = "QC";
		}
		else if (type2.equals(TYPE_TF)) {
			propertyFile = "sfee.properties";
			defaultId = "TeamForge";
		}
		else if (type2.equals(TYPE_PT)) {
			propertyFile = "cee.properties";
			defaultId = "CEE";
		}
		if (propertyFile != null) {
			File propertiesFile = new File(folder, propertyFile);
			if (propertiesFile.exists()) {
				try {
					FileInputStream inputStream = new FileInputStream(propertiesFile);
					Properties properties = new Properties();
					properties.load(inputStream);
					inputStream.close();
					id = properties.getProperty(Activator.PROPERTIES_SYSTEM_ID, defaultId);
				} catch (Exception e) { 
					Activator.handleError(e);
				}
			}
		}
		return id;
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
		if (P_ID_DESCRIPTION.equals(id)) return getDescription();
		if (P_ID_TYPE1.equals(id)) return type1;
		if (P_ID_TYPE2.equals(id)) return type2;
		if (P_ID_FOLDER1.equals(id)) return configurationFolder1;
		if (P_ID_FOLDER2.equals(id)) return configurationFolder2;
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
