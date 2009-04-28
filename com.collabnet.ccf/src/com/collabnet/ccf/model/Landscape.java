package com.collabnet.ccf.model;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

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
	
	private Properties properties1;
	private Properties properties2;
	
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
		properties1 = getProperties1();
		if (properties1 != null) {
			String defaultId = null;
			if (type1.equals(TYPE_QC)) {
				defaultId = "Quality Center";
			}
			else if (type1.equals(TYPE_TF)) {
				defaultId = "TeamForge";
			}
			else if (type1.equals(TYPE_PT)) {
				defaultId = "Project Tracker";
			}
			id = properties1.getProperty(Activator.PROPERTIES_SYSTEM_ID, defaultId);
		}
		return id;
	}
	
	public String getId2() {
		String id = null;
		properties2 = getProperties2();
		if (properties2 != null) {
			String defaultId = null;
			if (type2.equals(TYPE_QC)) {
				defaultId = "Quality Center";
			}
			else if (type2.equals(TYPE_TF)) {
				defaultId = "TeamForge";
			}
			else if (type2.equals(TYPE_PT)) {
				defaultId = "Project Tracker";
			}
			id = properties2.getProperty(Activator.PROPERTIES_SYSTEM_ID, defaultId);
		}
		return id;
	}
	
	public String getTimezone1() {
		String timezone = null;
		properties1 = getProperties1();
		if (properties1 != null) {
			timezone = properties1.getProperty(Activator.PROPERTIES_SYSTEM_TIMEZONE, TimeZone.getDefault().getID());
		}
		return timezone;
	}
	
	public String getTimezone2() {
		String timezone = null;
		properties2 = getProperties1();
		if (properties2 != null) {
			timezone = properties2.getProperty(Activator.PROPERTIES_SYSTEM_TIMEZONE, TimeZone.getDefault().getID());
		}
		return timezone;
	}
	
	public String getEncoding1() {
		String encoding = null;
		properties1 = getProperties1();
		if (properties1 != null) {
			encoding = properties1.getProperty(Activator.PROPERTIES_SYSTEM_ENCODING);
		}
		return encoding;
	}
	
	public String getEncoding2() {
		String encoding = null;
		properties2 = getProperties1();
		if (properties2 != null) {
			encoding = properties2.getProperty(Activator.PROPERTIES_SYSTEM_ENCODING);
		}
		return encoding;
	}
	
	public Properties getProperties1() {
		if (properties1 == null) {
			properties1 = getProperties(configurationFolder1, type1);
		}
		return properties1;
	}
	
	public Properties getProperties2() {
		if (properties2 == null) {
			properties2 = getProperties(configurationFolder2, type2);
		}
		return properties2;
	}
	
	private Properties getProperties(String configurationFolder, String type) {
		if (configurationFolder == null) return null;
		Properties properties = null;
		File folder = new File(configurationFolder);
		String propertyFile = null;
		if (type.equals(TYPE_QC)) {
			propertyFile = "qc.properties";
		}
		else if (type.equals(TYPE_TF)) {
			propertyFile = "sfee.properties";
		}
		else if (type.equals(TYPE_PT)) {
			propertyFile = "cee.properties";
		}
		if (propertyFile != null) {
			File propertiesFile = new File(folder, propertyFile);
			if (propertiesFile.exists()) {
				try {
					FileInputStream inputStream = new FileInputStream(propertiesFile);
					properties = new Properties();
					properties.load(inputStream);
					inputStream.close();
				} catch (Exception e) { 
					Activator.handleError(e);
				}
			}
		}	
		return properties;
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
