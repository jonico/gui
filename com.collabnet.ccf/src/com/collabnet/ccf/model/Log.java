package com.collabnet.ccf.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

@SuppressWarnings("unchecked")
public class Log implements IPropertySource, Comparable {
	private File logFile;
	private Logs logs;
	
	public static String P_ID_PATH = "path"; //$NON-NLS-1$
	public static String P_PATH = "Log file";
	
	public static List<PropertyDescriptor> descriptors;
	static
	{	
		descriptors = new ArrayList<PropertyDescriptor>();
		descriptors.add(new PropertyDescriptor(P_ID_PATH, P_PATH));
	}
		
	public Log(Logs logs, File logFile) {
		this.logs = logs;
		this.logFile = logFile;
	}
	
	public File getLogFile() {
		return logFile;
	}
	
	public Logs getLogs() {
		return logs;
	}
	
	public String toString() {
		return logFile.getName();
	}
	
	public int compareTo(Object compareToObject) {
		if (!(compareToObject instanceof Log)) return 0;
		Log compareToLog = (Log)compareToObject;
		return toString().compareTo(compareToLog.toString());
	}
	
	public Object getEditableValue() {
		return toString();
	}
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return (IPropertyDescriptor[])getDescriptors().toArray(new IPropertyDescriptor[getDescriptors().size()]);
	}
	
	private static List<PropertyDescriptor> getDescriptors() {
		return descriptors;
	}	
	
	public Object getPropertyValue(Object id) {
		if (P_ID_PATH.equals(id)) return logFile.getAbsolutePath();
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
