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

	private Properties ccfProperties1;
	private Properties ccfProperties2;
	private Properties properties1;
	private Properties properties2;
	
	private File configFile1;
	private File configFile2;
	private File logsFolder1;
	private File logsFolder2;
	private File xsltFolder1;
	private File xsltFolder2;
	private File libFolder;
	private File log4jFile;
	private File log4jRenameFile;
	
	public final static String TYPE_QC = "QC";
	public final static String TYPE_TF = "TF";
	public final static String TYPE_PT = "PT";
	public final static String TYPE_CCF = "CCF";
	
	public final static String TYPE_DESCRIPTION_QC = "Quality Center";
	public final static String TYPE_DESCRIPTION_TF = "TeamForge";
	public final static String TYPE_DESCRIPTION_PT = "Project Tracker";
	
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
	
	public String getDatabaseUrl() {
		String url = null;
		ccfProperties1 = getCcfProperties1();
		if (ccfProperties1 != null) {
			url = ccfProperties1.getProperty(Activator.PROPERTIES_CCF_URL);
		}
		return url;
	}
	
	public String getDatabaseDriver() {
		String driver = null;
		ccfProperties1 = getCcfProperties1();
		if (ccfProperties1 != null) {
			driver = ccfProperties1.getProperty(Activator.PROPERTIES_CCF_DRIVER);
		}
		return driver;
	}
	
	public String getDatabaseUser() {
		String user = null;
		ccfProperties1 = getCcfProperties1();
		if (ccfProperties1 != null) {
			user = ccfProperties1.getProperty(Activator.PROPERTIES_CCF_USER);
		}
		return user;
	}
	
	public String getDatabasePassword() {
		String password = null;
		ccfProperties1 = getCcfProperties1();
		if (ccfProperties1 != null) {
			password = ccfProperties1.getProperty(Activator.PROPERTIES_CCF_PASSWORD);
		}
		return password;
	}
	
	public String getHostName1() {
		String hostName = getCcfHost1();
		int index = hostName.indexOf("//");
		if (index != -1) {
			hostName = hostName.substring(index + 2);
		}
		return hostName;
	}
	
	public String getHostName2() {
		String hostName = getCcfHost2();
		int index = hostName.indexOf("//");
		if (index != -1) {
			hostName = hostName.substring(index + 2);
		}
		return hostName;
	}
	
	public String getCcfHost1() {
		String hostName = null;
		ccfProperties1 = getCcfProperties1();
		if (ccfProperties1 != null) {
			hostName = ccfProperties1.getProperty(Activator.PROPERTIES_CCF_HOST_NAME, "http://localhost");
		}
		return hostName;
	}
	
	public String getCcfHost2() {
		String hostName = null;
		ccfProperties2 = getCcfProperties2();
		if (ccfProperties2 != null) {
			hostName = ccfProperties2.getProperty(Activator.PROPERTIES_CCF_HOST_NAME, "http://localhost");
		}
		return hostName;
	}		
	
	public String getJmxPort1() {
		String port = null;
		ccfProperties1 = getCcfProperties1();
		if (ccfProperties1 != null) {
			port = ccfProperties1.getProperty(Activator.PROPERTIES_CCF_JMX_PORT);
		}
		return port;
	}
	
	public String getJmxUrl1() {
		StringBuffer url = new StringBuffer(getCcfHost1());
		String port = getJmxPort1();
		if (port != null && port.length() > 0) {
			url.append(":" + port);
		}
		return url.toString();
	}
	
	public String getJmxUrl2() {
		StringBuffer url = new StringBuffer(getCcfHost2());
		String port = getJmxPort2();
		if (port != null && port.length() > 0) {
			url.append(":" + port);
		}
		return url.toString();
	}
	
	public String getJmxPort2() {
		String port = null;
		ccfProperties2 = getCcfProperties2();
		if (ccfProperties2 != null) {
			port = ccfProperties2.getProperty(Activator.PROPERTIES_CCF_JMX_PORT);
		}
		return port;
	}
	
	public String getLogMessageTemplate1() {
		String template = null;
		ccfProperties1 = getCcfProperties1();
		if (ccfProperties1 != null) {
			template = ccfProperties1.getProperty(Activator.PROPERTIES_CCF_LOG_MESSAGE_TEMPLATE);
		}
		return template;
	}
	
	public String getLogMessageTemplate2() {
		String template = null;
		ccfProperties2 = getCcfProperties2();
		if (ccfProperties2 != null) {
			template = ccfProperties2.getProperty(Activator.PROPERTIES_CCF_LOG_MESSAGE_TEMPLATE);
		}
		return template;
	}
	
	public String getId1() {
		return getTypeDescription(type1);
	}
	
	public String getId2() {
		return getTypeDescription(type2);
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
		properties2 = getProperties2();
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
		properties2 = getProperties2();
		if (properties2 != null) {
			encoding = properties2.getProperty(Activator.PROPERTIES_SYSTEM_ENCODING);
		}
		return encoding;
	}
	
	public Properties getCcfProperties1() {
		if (ccfProperties1 == null) {
			ccfProperties1 = getProperties(configurationFolder1, TYPE_CCF);
		}
		return ccfProperties1;
	}
	
	public Properties getCcfProperties2() {
		if (ccfProperties2 == null) {
			ccfProperties2 = getProperties(configurationFolder2, TYPE_CCF);
		}
		return ccfProperties2;
	}
	
	public Properties getQcProperties() {
		if (type1.equals(TYPE_QC)) return getProperties1();
		if (type2.equals(TYPE_QC)) return getProperties2();
		return null;
	}
	
	public Properties getSfeeProperties() {
		if (type1.equals(TYPE_TF)) return getProperties1();
		if (type2.equals(TYPE_TF)) return getProperties2();
		return null;
	}
	
	public Properties getCeeProperties() {
		if (type1.equals(TYPE_PT)) return getProperties1();
		if (type2.equals(TYPE_PT)) return getProperties2();
		return null;
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
	
	public File getConfigurationFile1() {
		if (configFile1 == null) {
			configFile1 = new File(getConfigurationFolder1(), "config.xml");
		}
		return configFile1;
	}
	
	public File getConfigurationFile2() {
		if (configFile2 == null) {
			configFile2 = new File(getConfigurationFolder2(), "config.xml");
		}
		return configFile2;
	}
	
	public File getLogsFolder1() {
		if (logsFolder1 == null) {
			logsFolder1 = getLogsFolder(configurationFolder1);
		}
		if (logsFolder1.exists()) return logsFolder1;
		else return null;
	}
	
	public File getLogsFolder2() {
		if (logsFolder2 == null) {
			logsFolder2 = getLogsFolder(configurationFolder2);
		}
		if (logsFolder2.exists()) return logsFolder2;
		else return null;
	}
	
	private File getLogsFolder(String config) {
		File configurationFolder = new File(config);
		File logsFolder = new File(configurationFolder.getParent(), "logs");
		return logsFolder;
	}
	
	public File getXsltFolder1() {
		if (xsltFolder1 == null) {
			xsltFolder1 = getXsltFolder(configurationFolder1);
		}
		if (xsltFolder1.exists()) return xsltFolder1;
		else return null;
	}
	
	public File getXsltFolder2() {
		if (xsltFolder2 == null) {
			xsltFolder2 = getXsltFolder(configurationFolder2);
		}
		if (xsltFolder2.exists()) return xsltFolder2;
		else return null;
	}
	
	private File getXsltFolder(String config) {
		File configurationFolder = new File(config);
		File xsltFolder = new File(configurationFolder.getParent(), "xslt");
		return xsltFolder;
	}
	
	public File getLog4jFile() {
		if (log4jFile == null) {
			libFolder = getLibFolder();
			if (libFolder != null) {
				log4jFile = new File(libFolder, "log4j.xml");
			}
		}
		return log4jFile;
	}
	
	public File getLog4jRenameFile() {
		if (log4jRenameFile == null) {
			libFolder = getLibFolder();
			if (libFolder != null) {
				log4jRenameFile = new File(libFolder, "log4j.xml.rename_me");
			}
		}
		return log4jRenameFile;
	}
	
	private File getLibFolder() {
		if (libFolder == null) {
			File configurationFolder = new File(getConfigurationFolder());
			File parent = configurationFolder.getParentFile();
			if (parent != null) {
				parent = parent.getParentFile();
				if (parent != null) {
					parent = parent.getParentFile();
					if (parent != null) {
						parent = parent.getParentFile();
						if (parent != null) {
							libFolder = new File(parent, "lib");
						}
					}
				}
			}			
		}
		return libFolder;
	}
	
	public Log[] getLogs1(Logs logs) {
		List<Log> logList = new ArrayList<Log>();
		File[] logFiles = getLogFiles(getLogsFolder1());
		if (logFiles != null) {
			for (File logFile : logFiles) {
				Log log = new Log(logs, logFile);
				logList.add(log);
			}
		}
		Log[] logArray = new Log[logList.size()];
		logList.toArray(logArray);
		return logArray;
	}
	
	public Log[] getLogs2(Logs logs) {
		List<Log> logList = new ArrayList<Log>();
		File[] logFiles = getLogFiles(getLogsFolder2());
		if (logFiles != null) {
			for (File logFile : logFiles) {
				Log log = new Log(logs, logFile);
				logList.add(log);
			}
		}
		Log[] logArray = new Log[logList.size()];
		logList.toArray(logArray);
		return logArray;
	}
	
	public Log[] getLogs(Logs logs) {
		if (logs.getType() == Logs.TYPE_1_2) return getLogs1(logs);
		else return getLogs2(logs);
	}
	
	public static String getTypeDescription(String type) {
		String description;
		if (type.equals(TYPE_QC)) {
			description = TYPE_DESCRIPTION_QC;
		}
		else if (type.equals(TYPE_TF)) {
			description = TYPE_DESCRIPTION_TF;
		}
		else if (type.equals(TYPE_PT)) {
			description = TYPE_DESCRIPTION_PT;
		} else {
			description = type;
		}
		return description;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Landscape) {
			Landscape compareTo = (Landscape)obj;
			return getId1().equals(compareTo.getId1()) && getId2().equals(compareTo.getId2());
		}
		return super.equals(obj);
	}
	private File[] getLogFiles(File logsFolder) {
		List<File> logFiles = new ArrayList<File>();
		File[] logs = null;
		if (logsFolder != null) {
			logs = logsFolder.listFiles();
			if (logs != null) {
				for (File logFile : logs) {
					if (!logFile.isDirectory()) {
						logFiles.add(logFile);
					}
				}
			}
		}
		File[] logFileArray = new File[logFiles.size()];
		logFiles.toArray(logFileArray);
		return logFileArray;		
	}
	
	private Properties getProperties(String configurationFolder, String type) {
		if (configurationFolder == null) return null;
		Properties properties = null;
		File folder = new File(configurationFolder);
		String propertyFile = null;
		if (type.equals(TYPE_CCF)) {
			propertyFile = "ccf.properties";
		}
		else if (type.equals(TYPE_QC)) {
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
