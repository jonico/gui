package com.collabnet.ccf.wizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.ILandscapeContributor;
import com.collabnet.ccf.model.Landscape;

public class NewLandscapeWizardMainPage extends WizardPage {
	private Composite outerContainer;
	private Text descriptionText;
	private Button administratorButton;
	private Button operatorButton;
	private Group descriptionGroup;
	private Label descriptionImage;
	private Label descriptionLabel;
	private ILandscapeContributor[] landscapeContributors;
	private ILandscapeContributor selectedLandscapeContributor;
	private IDialogSettings settings;

	private static final String LAST_ROLE = "NewLandscapeWizardMainPage.lastRole"; //$NON-NLS-1$
	private static final String LAST_LANDSCAPE_CONTRIBUTOR = "NewLandscapeWizardMainPage.lastLandscapeContributor"; //$NON-NLS-1$
	
	public NewLandscapeWizardMainPage(String pageName, String title, ImageDescriptor titleImage, ILandscapeContributor[] landscapeContributors) {
		super(pageName, title, titleImage);
		this.landscapeContributors = landscapeContributors;
		setPageComplete(true);
		settings = Activator.getDefault().getDialogSettings();
	}

	public void createControl(Composite parent) {
		outerContainer = new Composite(parent,SWT.NONE);
		outerContainer.setLayout(new GridLayout());
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		Group descriptionGroup = new Group(outerContainer, SWT.NONE);
		descriptionGroup.setText("Description:");
		GridLayout descriptionLayout = new GridLayout();
		descriptionLayout.numColumns = 1;
		descriptionGroup.setLayout(descriptionLayout);
		descriptionGroup.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		descriptionText = new Text(descriptionGroup, SWT.BORDER);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		descriptionText.setLayoutData(gd);
		
		Group roleGroup = new Group(outerContainer, SWT.NONE);
		roleGroup.setText("Role:");
		GridLayout roleLayout = new GridLayout();
		roleLayout.numColumns = 1;
		roleGroup.setLayout(roleLayout);
		roleGroup.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		administratorButton = new Button(roleGroup, SWT.RADIO);
		administratorButton.setText("Administrator");
		operatorButton = new Button(roleGroup, SWT.RADIO);
		operatorButton.setText("Operator");
		
		int lastRole = Landscape.ROLE_ADMINISTRATOR;
		try {
			lastRole = settings.getInt(LAST_ROLE);
		} catch (Exception e) {}
		if (lastRole == Landscape.ROLE_OPERATOR) operatorButton.setSelection(true);
		else administratorButton.setSelection(true);
		
		SelectionListener roleListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (operatorButton.getSelection()) settings.put(LAST_ROLE, Landscape.ROLE_OPERATOR);
				else settings.put(LAST_ROLE, Landscape.ROLE_ADMINISTRATOR);
				setPageComplete(true);
			}		
		};
		administratorButton.addSelectionListener(roleListener);
		operatorButton.addSelectionListener(roleListener);
		
		Group landscapeGroup = new Group(outerContainer, SWT.NONE);
		landscapeGroup.setText("Landscape type:");
		GridLayout landscapeLayout = new GridLayout();
		landscapeLayout.numColumns = 1;
		landscapeGroup.setLayout(landscapeLayout);
		landscapeGroup.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		getLandscapeProviders(landscapeGroup);
		
		createDescriptionArea();
		
		setMessage("Specify the type and description of landscape");

		setControl(outerContainer);
	}
	
	private void getLandscapeProviders(Composite landscapeGroup) {
		SelectionListener selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button)e.getSource();
				Integer integer = (Integer)button.getData();
				selectedLandscapeContributor = landscapeContributors[integer.intValue()];
				settings.put(LAST_LANDSCAPE_CONTRIBUTOR, selectedLandscapeContributor.getName());
				createDescriptionArea();
				setPageComplete(true);
			}			
		};
		
		String lastLandscapeContributor = settings.get(LAST_LANDSCAPE_CONTRIBUTOR);
		for (int i = 0; i < landscapeContributors.length; i++) {
			Button landscapeButton = new Button(landscapeGroup, SWT.RADIO);
			landscapeButton.setText("&" + landscapeContributors[i].getName()); //$NON-NLS-1$
			landscapeButton.setData(new Integer(i));
			if (lastLandscapeContributor != null && landscapeContributors[i].getName().equals(lastLandscapeContributor)) { 
				landscapeButton.setSelection(true);
				selectedLandscapeContributor = landscapeContributors[i];
			}
			if (lastLandscapeContributor == null && i == 0) {
				landscapeButton.setSelection(true);
				selectedLandscapeContributor = landscapeContributors[i];					
			}
			landscapeButton.addSelectionListener(selectionListener);			
		}
		if (selectedLandscapeContributor == null) selectedLandscapeContributor = landscapeContributors[0];
	}
	
	private void createDescriptionArea() {
		boolean needsRedraw = descriptionGroup != null;
		if (descriptionGroup == null) {
			descriptionGroup = new Group(outerContainer, SWT.NONE);
			GridLayout descriptionLayout = new GridLayout();
			descriptionLayout.numColumns = 1;
			descriptionGroup.setLayout(descriptionLayout);
			descriptionGroup.setLayoutData(
			new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL));			
		}
		descriptionGroup.setText(selectedLandscapeContributor.getName());
		if (descriptionLabel != null && !descriptionLabel.isDisposed()) descriptionLabel.dispose();
		if (descriptionImage != null && !descriptionImage.isDisposed()) descriptionImage.dispose();
		if (selectedLandscapeContributor.getImage() != null) {
			descriptionImage = new Label(descriptionGroup, SWT.NONE);
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
			descriptionImage.setLayoutData(data);
			descriptionImage.setImage(selectedLandscapeContributor.getImage());			
		}
		descriptionLabel = new Label(descriptionGroup, SWT.WRAP);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL);
		data.widthHint = 500;
		descriptionLabel.setLayoutData(data);
		descriptionLabel.setText(selectedLandscapeContributor.getDescription());
		if (needsRedraw) {
			descriptionGroup.layout(true);
			descriptionGroup.redraw();
		}
	}
	
	public ILandscapeContributor getSelectedLandscapeContributor() {
		return selectedLandscapeContributor;
	}
	
	public String getDescription() {
		if (descriptionText.getText().trim().length() == 0)
			return selectedLandscapeContributor.getName();
		else
			return descriptionText.getText().trim();
	}
	
	public int getRole() {
		if (operatorButton.getSelection()) return Landscape.ROLE_OPERATOR;
		else return Landscape.ROLE_ADMINISTRATOR;
	}

}
