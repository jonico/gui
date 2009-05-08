package com.collabnet.ccf.dialogs;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.collabnet.ccf.Activator;

public class ResetProjectMappingDialog extends CcfDialog {
	private Button resetTo1999Button;
	private Button resetToCurrentButton;
	private Button resetToSelectedButton;
	private Label dateLabel;
	private DateTime date;
	private Label timeLabel;
	private DateTime time;
	
	private Timestamp resetDate;

	public ResetProjectMappingDialog(Shell shell) {
		super(shell, "ResetProjectMappingDialog");
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Reset Synchronization Status");
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		int delay = Activator.getDefault().getPreferenceStore().getInt(Activator.PREFERENCES_RESET_DELAY);
		if (delay > 0) {
			Label pauseLabel = new Label(composite, SWT.WRAP);
			pauseLabel.setText("Synchronization will be paused for " + delay + " seconds before resetting, then resumed automatically.\n\n");
			GridData gd = new GridData();
			gd.horizontalSpan = 5;
			pauseLabel.setLayoutData(gd);
		}
		
		resetTo1999Button = new Button(composite, SWT.RADIO);
		resetTo1999Button.setText("Reset to 1999");
		GridData gd = new GridData();
		gd.horizontalSpan = 5;
		resetTo1999Button.setLayoutData(gd);
		
		resetToCurrentButton = new Button(composite, SWT.RADIO);
		resetToCurrentButton.setText("Reset to current date/time");
		gd = new GridData();
		gd.horizontalSpan = 5;
		resetToCurrentButton.setLayoutData(gd);
		
		resetToSelectedButton = new Button(composite, SWT.RADIO);
		resetToSelectedButton.setText("Reset to selected date/time");
		
		dateLabel = new Label(composite, SWT.NONE);
		dateLabel.setText("Date:");
		date = new DateTime(composite, SWT.DATE | SWT.MEDIUM);
		timeLabel = new Label(composite, SWT.NONE);
		timeLabel.setText("Time:");
		time = new DateTime(composite, SWT.TIME | SWT.MEDIUM);
		
		resetTo1999Button.setSelection(true);
		
		setDateTimeEnablement();
		
		SelectionListener selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				setDateTimeEnablement();
			}			
		};
		
		resetTo1999Button.addSelectionListener(selectionListener);
		resetToCurrentButton.addSelectionListener(selectionListener);
		resetToSelectedButton.addSelectionListener(selectionListener);
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		if (resetTo1999Button.getSelection()) {
			resetDate = Timestamp.valueOf("1999-01-01 00:00:00.0");
		}
		else if (resetToCurrentButton.getSelection()) {
			Date date = new Date();
			resetDate = new Timestamp(date.getTime());
		}
		else if (resetToSelectedButton.getSelection()) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(date.getYear(), date.getMonth(), date.getDay());
			calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
			calendar.set(Calendar.MINUTE, time.getMinutes());
			calendar.set(Calendar.SECOND, time.getSeconds());
			resetDate = new Timestamp(calendar.getTimeInMillis());
		}
		super.okPressed();
	}

	private void setDateTimeEnablement() {
		dateLabel.setEnabled(resetToSelectedButton.getSelection());
		date.setEnabled(resetToSelectedButton.getSelection());
		timeLabel.setEnabled(resetToSelectedButton.getSelection());
		time.setEnabled(resetToSelectedButton.getSelection());
	}
	
	public Timestamp getResetDate() {
		return resetDate;
	}

}
