package com.collabnet.ccf.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.CCFJMXMonitorBean;
import com.collabnet.ccf.model.Landscape;

public class JmxConsoleDialog extends CcfDialog {
	private Landscape landscape;
	
	private CCFJMXMonitorBean monitor1;
	private CCFJMXMonitorBean monitor2;
	
	private Button aliveButton1;
	private Text uptimeText1;
	private Text memoryConsumptionText1;
	private Button restartButton1;
	private Button refreshButton1;
	
	private Button aliveButton2;
	private Text uptimeText2;
	private Text memoryConsumptionText2;
	private Button restartButton2;
	private Button refreshButton2;
	
	private boolean running1;
	private boolean running2;
	
	private int port1;
	private int port2;

	public JmxConsoleDialog(Shell shell, Landscape landscape) {
		super(shell, "JmxConsoleDialog");
		this.landscape = landscape;	
	}
	
	protected Control createDialogArea(Composite parent) {
		if (landscape.getType2().equals(Landscape.TYPE_TF)) {
			port1 = 10001;
			port2 = 10002;
		}
		if (landscape.getType2().equals(Landscape.TYPE_PT)) {
			port1 = 10000;
			port2 = 9999;
		}
		
		monitor1 = new CCFJMXMonitorBean();
		monitor1.setHostName(landscape.getHostName1());
		monitor1.setRmiPort(port1);
		
		monitor2 = new CCFJMXMonitorBean();
		monitor2.setHostName(landscape.getHostName2());
		monitor2.setRmiPort(port2);
		
		getShell().setText("JMX Console");
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group group1 = new Group(composite, SWT.NULL);
		GridLayout group1Layout = new GridLayout();
		group1Layout.numColumns = 2;
		group1.setLayout(group1Layout);
		group1.setLayoutData(new GridData(GridData.FILL_BOTH));
		group1.setText(Landscape.getTypeDescription(landscape.getType2()) + " => " + Landscape.getTypeDescription(landscape.getType1()));

		aliveButton1 = new Button(group1, SWT.CHECK);
		aliveButton1.setText("Running");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		aliveButton1.setLayoutData(data);
		
		Label host1Label = new Label(group1, SWT.NONE);
		host1Label.setText("Host:");
		Text host1Text = new Text(group1, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		host1Text.setLayoutData(data);
		host1Text.setText(landscape.getHostName1() + ":" + port1);

		Label uptimeLabel1 = new Label(group1, SWT.NONE);
		uptimeLabel1.setText("Uptime:");
		uptimeText1 = new Text(group1, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		uptimeText1.setLayoutData(data);
		
		Label memoryConsumptionLabel1 = new Label(group1, SWT.NONE);
		memoryConsumptionLabel1.setText("Memory consumption:");
		memoryConsumptionText1 = new Text(group1, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		memoryConsumptionText1.setLayoutData(data);
		
		Composite refreshGroup1 = new Composite(group1, SWT.NULL);
		GridLayout refresh1Layout = new GridLayout();
		refresh1Layout.numColumns = 2;
		refreshGroup1.setLayout(refresh1Layout);
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		data.horizontalSpan = 2;
		refreshGroup1.setLayoutData(data);
		
		restartButton1 = new Button(refreshGroup1, SWT.PUSH);
		restartButton1.setText("Restart");
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		restartButton1.setLayoutData(data);
		
		refreshButton1 = new Button(refreshGroup1, SWT.PUSH);
		refreshButton1.setText("Refresh");
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		refreshButton1.setLayoutData(data);
		
		Group group2 = new Group(composite, SWT.NULL);
		GridLayout group2Layout = new GridLayout();
		group2Layout.numColumns = 2;
		group2.setLayout(group2Layout);
		group2.setLayoutData(new GridData(GridData.FILL_BOTH));
		group2.setText(Landscape.getTypeDescription(landscape.getType1()) + " => " + Landscape.getTypeDescription(landscape.getType2()));
		
		aliveButton2 = new Button(group2, SWT.CHECK);
		aliveButton2.setText("Running");
		data = new GridData();
		data.horizontalSpan = 2;
		aliveButton2.setLayoutData(data);
		
		Label host2Label = new Label(group2, SWT.NONE);
		host2Label.setText("Host:");
		Text host2Text = new Text(group2, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		host2Text.setLayoutData(data);
		host2Text.setText(landscape.getHostName2() + ":" + port2);
		
		Label uptimeLabel2 = new Label(group2, SWT.NONE);
		uptimeLabel2.setText("Uptime:");
		uptimeText2 = new Text(group2, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		uptimeText2.setLayoutData(data);
		
		Label memoryConsumptionLabel2 = new Label(group2, SWT.NONE);
		memoryConsumptionLabel2.setText("Memory consumption:");
		memoryConsumptionText2 = new Text(group2, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		memoryConsumptionText2.setLayoutData(data);
		
		Composite refreshGroup2 = new Composite(group2, SWT.NULL);
		GridLayout refresh2Layout = new GridLayout();
		refresh2Layout.numColumns = 2;
		refreshGroup2.setLayout(refresh2Layout);
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		data.horizontalSpan = 2;
		refreshGroup2.setLayoutData(data);
		
		restartButton2 = new Button(refreshGroup2, SWT.PUSH);
		restartButton2.setText("Restart");
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		restartButton2.setLayoutData(data);
		
		refreshButton2 = new Button(refreshGroup2, SWT.PUSH);
		refreshButton2.setText("Refresh");
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		refreshButton2.setLayoutData(data);
		
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				getMonitor1Info();
				getMonitor2Info();
			}			
		});
		
		SelectionListener selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (se.getSource() == aliveButton1) {
					aliveButton1.setSelection(running1);
				}
				if (se.getSource() == aliveButton2) {
					aliveButton2.setSelection(running2);
				}
				if (se.getSource() == refreshButton1) {
					BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
						public void run() {
							getMonitor1Info();
						}			
					});					
				}
				if (se.getSource() == refreshButton2) {
					BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
						public void run() {
							getMonitor2Info();
						}			
					});					
				}	
				if (se.getSource() == restartButton1) {
					restart(monitor1);
				}
				if (se.getSource() == restartButton2) {
					restart(monitor2);
				}
			}			
		};
		
		aliveButton1.addSelectionListener(selectionListener);
		aliveButton2.addSelectionListener(selectionListener);
		refreshButton1.addSelectionListener(selectionListener);
		refreshButton2.addSelectionListener(selectionListener);
		restartButton1.addSelectionListener(selectionListener);
		restartButton2.addSelectionListener(selectionListener);
		
		return composite;
	}
	
	private void getMonitor1Info() {
		running1 = monitor1.isAlive();
		aliveButton1.setSelection(running1);
		Long uptime = monitor1.getCCFUptime();
		uptimeText1.setText(getTime(uptime));
		String memoryConsumption = monitor1.getCCFMemoryConsumption();
		if (memoryConsumption == null) memoryConsumptionText1.setText("");
		else memoryConsumptionText1.setText(memoryConsumption);
		restartButton1.setEnabled(running1);
	}
	
	private void getMonitor2Info() {
		running2 = monitor2.isAlive();
		aliveButton2.setSelection(running2);
		Long uptime = monitor2.getCCFUptime();
		uptimeText2.setText(getTime(uptime));
		String memoryConsumption = monitor2.getCCFMemoryConsumption();
		if (memoryConsumption == null) memoryConsumptionText2.setText("");
		else memoryConsumptionText2.setText(memoryConsumption);		
		restartButton2.setEnabled(running2);
	}
	
	private void restart(CCFJMXMonitorBean monitor) {
		monitor.restartCCFInstance();
		if (monitor == monitor1) getMonitor1Info();
		if (monitor == monitor2) getMonitor2Info();
	}
	
	private String getTime(Long milliseconds) {
		if (milliseconds != null) {
			long timeMilliseconds = milliseconds.longValue();
			long time = timeMilliseconds/1000;
			String seconds = Integer.toString((int)(time % 60));  
			String minutes = Integer.toString((int)((time % 3600) / 60));  
			String hours = Integer.toString((int)(time / 3600));  
			for (int i = 0; i < 2; i++) {  
				if (seconds.length() < 2) {  
					seconds = "0" + seconds;  
				}  
				if (minutes.length() < 2) {  
					minutes = "0" + minutes;  
				}  
				if (hours.length() < 2) {  
					hours = "0" + hours;  
				}  
			}
			return hours + ":" + minutes + ":" + seconds;
		}
		return "";
	}

}
