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

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.CCFJMXMonitorBean;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.model.Landscape;

public class JmxConsoleDialog extends CcfDialog {
	private Landscape landscape;
	
	private CCFJMXMonitorBean monitor1;
	private CCFJMXMonitorBean monitor2;
	
	private Button aliveButton1;
	private Text uptimeText1;
	private Text extractionTimeText1;
	private Text updateTimeText1;
	private Text memoryConsumptionText1;
	private Text artifactsShippedText1;
	private Text exceptionsCaughtText1;
	private Text artifactsQuarantinedText1;
	private Text hospitalCountText1;
	private Button restartButton1;
	private Button refreshButton1;
	
	private Button aliveButton2;
	private Text uptimeText2;
	private Text extractionTimeText2;
	private Text updateTimeText2;
	private Text memoryConsumptionText2;
	private Text artifactsShippedText2;
	private Text exceptionsCaughtText2;
	private Text artifactsQuarantinedText2;
	private Text hospitalCountText2;
	private Button restartButton2;
	private Button refreshButton2;
	
	private boolean running1;
	private boolean running2;
	
	private int port1;
	private int port2;
	
	private CcfDataProvider dataProvider;

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
		
		Label extractionTimeLabel1 = new Label(group1, SWT.NONE);
		extractionTimeLabel1.setText("Artifact extraction time:");
		extractionTimeText1 = new Text(group1, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		extractionTimeText1.setLayoutData(data);
		
		Label updateTimeLabel1 = new Label(group1, SWT.NONE);
		updateTimeLabel1.setText("Artifact update time:");
		updateTimeText1 = new Text(group1, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		updateTimeText1.setLayoutData(data);
		
		Label memoryConsumptionLabel1 = new Label(group1, SWT.NONE);
		memoryConsumptionLabel1.setText("Memory consumption:");
		memoryConsumptionText1 = new Text(group1, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		memoryConsumptionText1.setLayoutData(data);
		
		Label artifactsShippedLabel1 = new Label(group1, SWT.NONE);
		artifactsShippedLabel1.setText("Artifacts shipped:");
		artifactsShippedText1 = new Text(group1, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		artifactsShippedText1.setLayoutData(data);
		
		Label exceptionsCaughtLabel1 = new Label(group1, SWT.NONE);
		exceptionsCaughtLabel1.setText("Exceptions caught:");
		exceptionsCaughtText1 = new Text(group1, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		exceptionsCaughtText1.setLayoutData(data);
		
		Label artifactsQuarantinedLabel1 = new Label(group1, SWT.NONE);
		artifactsQuarantinedLabel1.setText("Artifacts quarantined:");
		artifactsQuarantinedText1 = new Text(group1, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		artifactsQuarantinedText1.setLayoutData(data);
		
		Label hospitalCountLabel1 = new Label(group1, SWT.NONE);
		hospitalCountLabel1.setText("Hospital entries:");
		hospitalCountText1 = new Text(group1, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		hospitalCountText1.setLayoutData(data);
		
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
		
		Label extractionTimeLabel2 = new Label(group2, SWT.NONE);
		extractionTimeLabel2.setText("Artifact extraction time:");
		extractionTimeText2 = new Text(group2, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		extractionTimeText2.setLayoutData(data);
		
		Label updateTimeLabel2 = new Label(group2, SWT.NONE);
		updateTimeLabel2.setText("Artifact update time:");
		updateTimeText2 = new Text(group2, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		updateTimeText2.setLayoutData(data);
		
		Label memoryConsumptionLabel2 = new Label(group2, SWT.NONE);
		memoryConsumptionLabel2.setText("Memory consumption:");
		memoryConsumptionText2 = new Text(group2, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		memoryConsumptionText2.setLayoutData(data);
		
		Label artifactsShippedLabel2 = new Label(group2, SWT.NONE);
		artifactsShippedLabel2.setText("Artifacts shipped:");
		artifactsShippedText2 = new Text(group2, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		artifactsShippedText2.setLayoutData(data);
		
		Label exceptionsCaughtLabel2 = new Label(group2, SWT.NONE);
		exceptionsCaughtLabel2.setText("Exceptions caught:");
		exceptionsCaughtText2 = new Text(group2, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		exceptionsCaughtText2.setLayoutData(data);
		
		Label artifactsQuarantinedLabel2 = new Label(group2, SWT.NONE);
		artifactsQuarantinedLabel2.setText("Artifacts quarantined:");
		artifactsQuarantinedText2 = new Text(group2, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		artifactsQuarantinedText2.setLayoutData(data);
		
		Label hospitalCountLabel2 = new Label(group2, SWT.NONE);
		hospitalCountLabel2.setText("Hospital entries:");
		hospitalCountText2 = new Text(group2, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		hospitalCountText2.setLayoutData(data);
		
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
		String readerMetricsName = null;
		String writerMetricsName = null;
		String extractionTime = null;
		String updateTime = null;
		if (landscape.getType2().equals(Landscape.TYPE_QC)) readerMetricsName = CCFJMXMonitorBean.QCREADER_METRICS;
		else if (landscape.getType2().equals(Landscape.TYPE_TF)) readerMetricsName = CCFJMXMonitorBean.TFREADER_METRICS;
		else if (landscape.getType2().equals(Landscape.TYPE_PT)) readerMetricsName = CCFJMXMonitorBean.PTREADER_METRICS;
		if (landscape.getType1().equals(Landscape.TYPE_QC)) writerMetricsName = CCFJMXMonitorBean.QCWRITER_METRICS;
		else if (landscape.getType1().equals(Landscape.TYPE_TF)) writerMetricsName = CCFJMXMonitorBean.TFWRITER_METRICS;
		else if (landscape.getType1().equals(Landscape.TYPE_PT)) writerMetricsName = CCFJMXMonitorBean.PTWRITER_METRICS;		
		extractionTime = monitor1.getArtifactExtractionProcessingTime(readerMetricsName);
		updateTime = monitor1.getArtifactUpdateProcessingTime(writerMetricsName);
		if (extractionTime == null) extractionTimeText1.setText("");
		else extractionTimeText1.setText(extractionTime);
		if (updateTime == null) updateTimeText1.setText("");
		else updateTimeText1.setText(updateTime);
		String memoryConsumption = monitor1.getCCFMemoryConsumption();
		if (memoryConsumption == null) memoryConsumptionText1.setText("");
		else memoryConsumptionText1.setText(memoryConsumption);
		restartButton1.setEnabled(running1);
		String artifactsShipped = monitor1.getNumberOfArtifactsShipped(readerMetricsName);
		if (artifactsShipped == null) artifactsShippedText1.setText("");
		else artifactsQuarantinedText1.setText(artifactsShipped);
		String exceptionsCaught = monitor1.getNumberOfCCFExceptionsCaught();
		if (exceptionsCaught == null) exceptionsCaughtText1.setText("");
		else exceptionsCaughtText1.setText(exceptionsCaught);
		String artifactsQuarantined = monitor1.getNumberOfArtifactsQuarantined();
		if (artifactsQuarantined == null) artifactsQuarantinedText1.setText("");
		else artifactsQuarantinedText1.setText(artifactsQuarantined);
		
		int hospitalEntries = getHospitalCount(landscape.getType1());
		if (hospitalEntries == 0) hospitalCountText1.setText("");
		else hospitalCountText1.setText(Integer.toString(hospitalEntries));
	}
	
	private void getMonitor2Info() {
		running2 = monitor2.isAlive();
		aliveButton2.setSelection(running1);
		Long uptime = monitor2.getCCFUptime();
		uptimeText2.setText(getTime(uptime));
		String readerMetricsName = null;
		String writerMetricsName = null;
		String extractionTime = null;
		String updateTime = null;
		if (landscape.getType1().equals(Landscape.TYPE_QC)) readerMetricsName = CCFJMXMonitorBean.QCREADER_METRICS;
		else if (landscape.getType1().equals(Landscape.TYPE_TF)) readerMetricsName = CCFJMXMonitorBean.TFREADER_METRICS;
		else if (landscape.getType1().equals(Landscape.TYPE_PT)) readerMetricsName = CCFJMXMonitorBean.PTREADER_METRICS;
		if (landscape.getType2().equals(Landscape.TYPE_QC)) writerMetricsName = CCFJMXMonitorBean.QCWRITER_METRICS;
		else if (landscape.getType2().equals(Landscape.TYPE_TF)) writerMetricsName = CCFJMXMonitorBean.TFWRITER_METRICS;
		else if (landscape.getType2().equals(Landscape.TYPE_PT)) writerMetricsName = CCFJMXMonitorBean.PTWRITER_METRICS;		
		extractionTime = monitor2.getArtifactExtractionProcessingTime(readerMetricsName);
		updateTime = monitor2.getArtifactUpdateProcessingTime(writerMetricsName);
		if (extractionTime == null) extractionTimeText2.setText("");
		else extractionTimeText2.setText(extractionTime);
		if (updateTime == null) updateTimeText2.setText("");
		else updateTimeText2.setText(updateTime);
		String memoryConsumption = monitor2.getCCFMemoryConsumption();
		if (memoryConsumption == null) memoryConsumptionText2.setText("");
		else memoryConsumptionText2.setText(memoryConsumption);
		restartButton2.setEnabled(running1);
		String artifactsShipped = monitor2.getNumberOfArtifactsShipped(readerMetricsName);
		if (artifactsShipped == null) artifactsShippedText2.setText("");
		else artifactsQuarantinedText2.setText(artifactsShipped);
		String exceptionsCaught = monitor2.getNumberOfCCFExceptionsCaught();
		if (exceptionsCaught == null) exceptionsCaughtText2.setText("");
		else exceptionsCaughtText2.setText(exceptionsCaught);
		String artifactsQuarantined = monitor2.getNumberOfArtifactsQuarantined();
		if (artifactsQuarantined == null) artifactsQuarantinedText2.setText("");
		else artifactsQuarantinedText2.setText(artifactsQuarantined);
		
		int hospitalEntries = getHospitalCount(landscape.getType2());
		if (hospitalEntries == 0) hospitalCountText2.setText("");
		else hospitalCountText2.setText(Integer.toString(hospitalEntries));
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
	
	private int getHospitalCount(String target) {
		String targetType = null;
		if (target.equals(Landscape.TYPE_QC)) {
			targetType = landscape.getType1();
		} else {
			targetType = landscape.getType2();
		}
		Filter targetTypeFilter = new Filter(CcfDataProvider.HOSPITAL_TARGET_SYSTEM_KIND, targetType, true, Filter.FILTER_TYPE_LIKE);
		Filter fixedFilter = new Filter(CcfDataProvider.HOSPITAL_FIXED, "false", false, Filter.FILTER_TYPE_EQUAL);
		Filter[] filters = { targetTypeFilter, fixedFilter };
		Filter[][] filterGroups = { filters };
		int hospitalCount= 0;
		try {
			hospitalCount = getDataProvider().getPatients(landscape, filterGroups).length;
		} catch (Exception e) {
			Activator.handleError(e);
		}
		return hospitalCount;
	}
	
	private CcfDataProvider getDataProvider() {
		if (dataProvider == null) {
			dataProvider = new CcfDataProvider();
		}
		return dataProvider;
	}

}
