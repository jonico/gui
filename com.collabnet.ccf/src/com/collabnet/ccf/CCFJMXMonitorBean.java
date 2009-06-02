package com.collabnet.ccf;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


public class CCFJMXMonitorBean {
	private static final String UPTIME = "Uptime";
	private static final String JAVA_LANG_TYPE_RUNTIME = "java.lang:type=Runtime";
	private static final String EXIT = "exit";
	private static final String MEMORY = "Memory";
	private static final String OPENADAPTOR_ID_SYSTEM_UTIL = "openadaptor:id=SystemUtil";
	public static final String DEFAULT_HOSTNAME = "localhost";
	public static final int QC2PT_PORT = 9999;
	public static final int PT2QC_PORT = 10000;
	public static final int TF2QC_PORT = 10001;
	public static final int QC2TF_PORT = 10002;
	
	private String hostName=DEFAULT_HOSTNAME;
	private int rmiPort=QC2TF_PORT;
	private JMXConnector connector = null;
	private MBeanServerConnection connection = null;
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public void setRmiPort(int rmiPort) {
		this.rmiPort = rmiPort;
	}
	
	public int getRmiPort() {
		return rmiPort;
	}
	
	/**
	 * Connects to the JMX port
	 * If this operation fails, CCFInstance is not up and running
	 * @throws IOException 
	 */
	private void connect() throws IOException {
		close();
		JMXServiceURL url=new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+hostName+":"+rmiPort+"/jmxrmi");
		connector=JMXConnectorFactory.connect(url);
		connection = connector.getMBeanServerConnection();
	}
	
	/**
	 * Finds out whether CCFInstance is alive
	 * @return true if CCF is alive, false if not
	 */
	public boolean isAlive() {
		if (connection == null) {
			try {
				connect();
			} catch (IOException e) {
				return false;
			}
		}
		else {
			try {
				connection.getMBeanCount();
			} catch (IOException e) {
				try {
					connect();
				} catch (IOException e1) {
					return false;
				}
			}
		}
		return true;
	}
	
	private void close() {
		if (connector != null) {
			try {
				connection=null;
				connector.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}
	
	/**
	 * Executes JMX method
	 * @param objectName
	 * @param operationName
	 * @param params
	 * @param signature
	 * @return result or null if operation failed
	 */
	public Object invokeJMXOperation (String objectName, String operationName, Object[] params, String [] signature) {
		if (!isAlive()) {
			return null;
		}
		try {
			return connection.invoke(new ObjectName(objectName), operationName, params, signature);
		} catch (InstanceNotFoundException e) {
			//e.printStackTrace();
			return null;
		} catch (MalformedObjectNameException e) {
			//e.printStackTrace();
			return null;
		} catch (MBeanException e) {
			//e.printStackTrace();
			return null;
		} catch (ReflectionException e) {
			//e.printStackTrace();
			return null;
		} catch (NullPointerException e) {
			//e.printStackTrace();
			return null;
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public void restartCCFInstance() {
		invokeJMXOperation(OPENADAPTOR_ID_SYSTEM_UTIL, EXIT, new Object[]{}, new String[] {});
	}
	
	/**
	 * Returns JMX attribute value
	 * @param objectName
	 * @param attributeName
	 * @return value or null if an error occured
	 */
	public Object getJMXAttribute (String objectName, String attributeName) {
		if (!isAlive()) {
			return null;
		}
		try {
			return connection.getAttribute(new ObjectName(objectName), attributeName);
		} catch (AttributeNotFoundException e) {
			return null;
		} catch (InstanceNotFoundException e) {
			return null;
		} catch (MalformedObjectNameException e) {
			return null;
		} catch (MBeanException e) {
			return null;
		} catch (ReflectionException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Returns CCF memory consumption
	 * @return memory consumption or null if info is not available 
	 */
	public String getCCFMemoryConsumption() {
		return (String) getJMXAttribute(OPENADAPTOR_ID_SYSTEM_UTIL, MEMORY);
	}
	
	/**
	 * Returns CCF uptime in milliseconds
	 * @return uptime in milliseconds or null if info is not available 
	 */
	public Long getCCFUptime() {
		return (Long) getJMXAttribute(JAVA_LANG_TYPE_RUNTIME, UPTIME);
	}
	
	
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
}
