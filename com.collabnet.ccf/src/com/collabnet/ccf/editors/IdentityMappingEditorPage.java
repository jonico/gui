package com.collabnet.ccf.editors;

import java.sql.Timestamp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.db.Update;
import com.collabnet.ccf.model.IdentityMapping;

public class IdentityMappingEditorPage extends FormPage {
	private ScrolledForm form;
	private FormToolkit toolkit;
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	private CcfDataProvider dataProvider = new CcfDataProvider();
	
	private Label errorImageLabel;
	private Label errorTextLabel;
	
	private Section sourceSection;
	private Composite sourceSectionClient;
	private Section targetSection;
	private Composite targetSectionClient;
	private Section childSection;
	private Composite childSectionClient;
	private Section parentSection;
	private Composite parentSectionClient;
	
	private Text artifactTypeText;
	
	private Text sourceSystemIdText;
	private Text sourceRepositoryIdText;
	private Text sourceSystemKindText;
	private Text sourceRepositoryKindText;
	private Text sourceArtifactIdText;
	private Text sourceLastModificationText;
	private Text sourceArtifactVersionText;
	
	private Text targetSystemIdText;
	private Text targetRepositoryIdText;
	private Text targetSystemKindText;
	private Text targetRepositoryKindText;
	private Text targetArtifactIdText;
	private Text targetLastModificationText;
	private Text targetArtifactVersionText;
	
	private Text childSourceRepositoryIdText;
	private Text childSourceRepositoryKindText;
	private Text childSourceArtifactIdText;
	private Text childTargetRepositoryIdText;
	private Text childTargetRepositoryKindText;
	private Text childTargetArtifactIdText;
	
	private Text parentSourceRepositoryIdText;
	private Text parentSourceRepositoryKindText;
	private Text parentSourceArtifactIdText;
	private Text parentTargetRepositoryIdText;
	private Text parentTargetRepositoryKindText;
	private Text parentTargetArtifactIdText;
	
	private String artifactType;
	
	private String sourceSystemId;
	private String sourceRepositoryId;
	private String sourceSystemKind;
	private String sourceRepositoryKind;
	private String sourceArtifactId;
	private String sourceLastModification;
	private String sourceArtifactVersion;
	
	private String targetSystemId;
	private String targetRepositoryId;
	private String targetSystemKind;
	private String targetRepositoryKind;
	private String targetArtifactId;
	private String targetLastModification;
	private String targetArtifactVersion;
	
	private String childSourceRepositoryId;
	private String childSourceRepositoryKind;
	private String childSourceArtifactId;
	private String childTargetRepositoryId;
	private String childTargetRepositoryKind;
	private String childTargetArtifactId;
	
	private String parentSourceRepositoryId;
	private String parentSourceRepositoryKind;
	private String parentSourceArtifactId;
	private String parentTargetRepositoryId;
	private String parentTargetRepositoryKind;
	private String parentTargetArtifactId;
	
	private boolean saveError;

	public final static String SOURCE_SECTION_STATE = "IdentityMappingEditorPage.sourceSectionExpanded";
	public final static String TARGET_SECTION_STATE = "IdentityMappingEditorPage.targetSectionExpanded";
	public final static String CHILD_SECTION_STATE = "IdentityMappingEditorPage.childSectionExpanded";
	public final static String PARENT_SECTION_STATE = "IdentityMappingEditorPage.parentSectionExpanded";
	
	public final static String STATE_CONTRACTED = "C";
	public final static String STATE_EXPANDED = "E";

	public IdentityMappingEditorPage(String id, String title) {
		super(id, title);
	}

	public IdentityMappingEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();
        toolkit = getEditor().getToolkit();
        TableWrapLayout formLayout = new TableWrapLayout();
        formLayout.makeColumnsEqualWidth = true;
        formLayout.numColumns = 2;
        form.getBody().setLayout(formLayout);
		createControls(form.getBody());
	}
	
	private void createControls(Composite composite) {	
		artifactType = getIdentityMapping().getArtifactType();
		if (artifactType == null) artifactType = "";
		
		sourceSystemId = getIdentityMapping().getSourceSystemId();
		if (sourceSystemId == null) sourceSystemId = "";
		sourceRepositoryId = getIdentityMapping().getSourceRepositoryId();
		if (sourceRepositoryId == null) sourceRepositoryId = "";
		sourceSystemKind = getIdentityMapping().getSourceSystemKind();
		if (sourceSystemKind == null) sourceSystemKind = "";
		sourceRepositoryKind = getIdentityMapping().getSourceRepositoryKind();
		if (sourceRepositoryKind == null) sourceRepositoryKind = "";
		sourceArtifactId = getIdentityMapping().getSourceArtifactId();
		if (sourceArtifactId == null) sourceArtifactId = "";
		sourceArtifactVersion = getIdentityMapping().getSourceArtifactVersion();
		if (sourceArtifactVersion == null) sourceArtifactVersion = "";
		
		if (getIdentityMapping().getSourceLastModificationTime() == null) {
			sourceLastModification = "";
		} else {
			sourceLastModification = getIdentityMapping().getSourceLastModificationTime().toString();
		}
		
		targetSystemId = getIdentityMapping().getTargetSystemId();
		if (targetSystemId == null) targetSystemId = "";
		targetRepositoryId = getIdentityMapping().getTargetRepositoryId();
		if (targetRepositoryId == null) targetRepositoryId = "";
		targetSystemKind = getIdentityMapping().getTargetSystemKind();
		if (targetSystemKind == null) targetSystemKind = "";
		targetRepositoryKind = getIdentityMapping().getTargetRepositoryKind();
		if (targetRepositoryKind == null) targetRepositoryKind = "";
		targetArtifactId = getIdentityMapping().getTargetArtifactId();
		if (targetArtifactId == null) targetArtifactId = "";
		targetArtifactVersion = getIdentityMapping().getTargetArtifactVersion();
		if (targetArtifactVersion == null) targetArtifactVersion = "";
		
		if (getIdentityMapping().getTargetLastModificationTime() == null) {
			targetLastModification = "";
		} else {
			targetLastModification = getIdentityMapping().getTargetLastModificationTime().toString();
		}
		
		childSourceRepositoryId = getIdentityMapping().getChildSourceRepositoryId();
		if (childSourceRepositoryId == null) childSourceRepositoryId = "";
		childSourceRepositoryKind = getIdentityMapping().getChildSourceRepositoryKind();
		if (childSourceRepositoryKind == null) childSourceRepositoryKind = "";
		childSourceArtifactId = getIdentityMapping().getChildSourceArtifactId();
		if (childSourceArtifactId == null) childSourceArtifactId = "";
		childTargetRepositoryId = getIdentityMapping().getChildTargetRepositoryId();
		if (childTargetRepositoryId == null) childTargetRepositoryId = "";
		childTargetRepositoryKind = getIdentityMapping().getChildTargetRepositoryKind();
		if (childTargetRepositoryKind == null) childTargetRepositoryKind = "";
		childTargetArtifactId = getIdentityMapping().getChildTargetArtifactId();
		if (childTargetArtifactId == null) childTargetArtifactId = "";
		
		parentSourceRepositoryId = getIdentityMapping().getParentSourceRepositoryId();
		if (parentSourceRepositoryId == null) parentSourceRepositoryId = "";
		parentSourceRepositoryKind = getIdentityMapping().getParentSourceRepositoryKind();
		if (parentSourceRepositoryKind == null) parentSourceRepositoryKind = "";
		parentSourceArtifactId = getIdentityMapping().getParentSourceArtifactId();
		if (parentSourceArtifactId == null) parentSourceArtifactId = "";
		parentTargetRepositoryId = getIdentityMapping().getParentTargetRepositoryId();
		if (parentTargetRepositoryId == null) parentTargetRepositoryId = "";
		parentTargetRepositoryKind = getIdentityMapping().getParentTargetRepositoryKind();
		if (parentTargetRepositoryKind == null) parentTargetRepositoryKind = "";
		parentTargetArtifactId = getIdentityMapping().getParentTargetArtifactId();
		if (parentTargetArtifactId == null) parentTargetArtifactId = "";
		
	    Composite headerGroup = toolkit.createComposite(composite);
	    GridLayout headerLayout = new GridLayout();
	    headerLayout.numColumns = 3;
	    headerGroup.setLayout(headerLayout);
        TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
        td.colspan = 2;
        headerGroup.setLayoutData(td);
		
		Label headerLabel = new Label(headerGroup, SWT.NONE);
		headerLabel.setText("Identity Mapping Details");
		headerLabel.setBackground(((FormEditor)getEditor()).getToolkit().getColors().getBackground());
		headerLabel.setFont(JFaceResources.getHeaderFont());

		errorImageLabel = new Label(headerGroup, SWT.NONE);
		errorImageLabel.setImage(Activator.getImage(Activator.IMAGE_ERROR));
		errorImageLabel.setBackground(((FormEditor)getEditor()).getToolkit().getColors().getBackground());
		errorImageLabel.setVisible(false);
		
		errorTextLabel = new Label(headerGroup, SWT.NONE);
		errorTextLabel.setText("One or more required field is empty or invalid.");
		errorTextLabel.setBackground(((FormEditor)getEditor()).getToolkit().getColors().getBackground());
		errorTextLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		errorTextLabel.setVisible(false);

	    Composite typeGroup = toolkit.createComposite(composite);
	    GridLayout typeLayout = new GridLayout();
	    typeLayout.numColumns = 2;
	    typeGroup.setLayout(typeLayout);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        td.colspan = 2;
        typeGroup.setLayoutData(td);
        
        toolkit.createLabel(typeGroup, "Artifact type:");
        String type;
        if (artifactType == null) type = "";
        else type = artifactType;
        artifactTypeText = toolkit.createText(typeGroup, type);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		artifactTypeText.setLayoutData(gd);
		
        createSourceSection(composite);
        createTargetSection(composite);
        createChildSection(composite);
        createParentSection(composite);
        
        toolkit.paintBordersFor(sourceSectionClient);
        String expansionState = settings.get(SOURCE_SECTION_STATE);
        sourceSection.setExpanded(expansionState == null  || expansionState.equals(STATE_EXPANDED));
        
        toolkit.paintBordersFor(targetSectionClient);
        expansionState = settings.get(TARGET_SECTION_STATE);
        targetSection.setExpanded(expansionState == null  || expansionState.equals(STATE_EXPANDED));
        
        toolkit.paintBordersFor(childSectionClient);
        expansionState = settings.get(CHILD_SECTION_STATE);
        childSection.setExpanded(expansionState == null  || expansionState.equals(STATE_EXPANDED));
        
        toolkit.paintBordersFor(parentSectionClient);
        expansionState = settings.get(PARENT_SECTION_STATE);
        parentSection.setExpanded(expansionState == null  || expansionState.equals(STATE_EXPANDED));
	
        ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				errorImageLabel.setVisible(!canLeaveThePage());
				errorTextLabel.setVisible(!canLeaveThePage());
				((IdentityMappingEditor)getEditor()).setDirty();
			}			
		};
		
		artifactTypeText.addModifyListener(modifyListener);
		
		sourceSystemIdText.addModifyListener(modifyListener);
		sourceRepositoryIdText.addModifyListener(modifyListener);
		sourceSystemKindText.addModifyListener(modifyListener);
		sourceRepositoryKindText.addModifyListener(modifyListener);
		sourceArtifactIdText.addModifyListener(modifyListener);
		sourceLastModificationText.addModifyListener(modifyListener);
		sourceArtifactVersionText.addModifyListener(modifyListener);
		
		targetSystemIdText.addModifyListener(modifyListener);
		targetRepositoryIdText.addModifyListener(modifyListener);
		targetSystemKindText.addModifyListener(modifyListener);
		targetRepositoryKindText.addModifyListener(modifyListener);
		targetArtifactIdText.addModifyListener(modifyListener);
		targetLastModificationText.addModifyListener(modifyListener);
		targetArtifactVersionText.addModifyListener(modifyListener);
		
		childSourceRepositoryIdText.addModifyListener(modifyListener);
		childSourceRepositoryKindText.addModifyListener(modifyListener);
		childSourceArtifactIdText.addModifyListener(modifyListener);
		childTargetRepositoryIdText.addModifyListener(modifyListener);
		childTargetRepositoryKindText.addModifyListener(modifyListener);
		childTargetArtifactIdText.addModifyListener(modifyListener);
		
		parentSourceRepositoryIdText.addModifyListener(modifyListener);
		parentSourceRepositoryKindText.addModifyListener(modifyListener);
		parentSourceArtifactIdText.addModifyListener(modifyListener);
		parentTargetRepositoryIdText.addModifyListener(modifyListener);
		parentTargetRepositoryKindText.addModifyListener(modifyListener);
		parentTargetArtifactIdText.addModifyListener(modifyListener);
		
		FocusListener focusListener = new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				((Text)e.getSource()).selectAll();
			}
			public void focusLost(FocusEvent e) {
				((Text)e.getSource()).setText(((Text)e.getSource()).getText());
			}					
		};
		
		artifactTypeText.addFocusListener(focusListener);
		
		sourceSystemIdText.addFocusListener(focusListener);
		sourceRepositoryIdText.addFocusListener(focusListener);
		sourceSystemKindText.addFocusListener(focusListener);
		sourceRepositoryKindText.addFocusListener(focusListener);
		sourceArtifactIdText.addFocusListener(focusListener);
		sourceLastModificationText.addFocusListener(focusListener);
		sourceArtifactVersionText.addFocusListener(focusListener);
		
		targetSystemIdText.addFocusListener(focusListener);
		targetRepositoryIdText.addFocusListener(focusListener);
		targetSystemKindText.addFocusListener(focusListener);
		targetRepositoryKindText.addFocusListener(focusListener);
		targetArtifactIdText.addFocusListener(focusListener);
		targetLastModificationText.addFocusListener(focusListener);
		targetArtifactVersionText.addFocusListener(focusListener);
		
		childSourceRepositoryIdText.addFocusListener(focusListener);
		childSourceRepositoryKindText.addFocusListener(focusListener);
		childSourceArtifactIdText.addFocusListener(focusListener);
		childTargetRepositoryIdText.addFocusListener(focusListener);
		childTargetRepositoryKindText.addFocusListener(focusListener);
		childTargetArtifactIdText.addFocusListener(focusListener);
		
		parentSourceRepositoryIdText.addFocusListener(focusListener);
		parentSourceRepositoryKindText.addFocusListener(focusListener);
		parentSourceArtifactIdText.addFocusListener(focusListener);
		parentTargetRepositoryIdText.addFocusListener(focusListener);
		parentTargetRepositoryKindText.addFocusListener(focusListener);
		parentTargetArtifactIdText.addFocusListener(focusListener);
	}

	private void createSourceSection(Composite composite) {
		TableWrapData td;
		sourceSection = toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        sourceSection.setLayoutData(td);
        sourceSection.setText("Source");
        sourceSectionClient = toolkit.createComposite(sourceSection); 
        GridLayout sourceLayout = new GridLayout();
        sourceLayout.numColumns = 2;
        sourceLayout.verticalSpacing = 10;
        sourceSectionClient.setLayout(sourceLayout);
        sourceSection.setClient(sourceSectionClient);
        sourceSection.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
                if (e.getState()) settings.put(SOURCE_SECTION_STATE, STATE_EXPANDED);
                else settings.put(SOURCE_SECTION_STATE, STATE_CONTRACTED);
            }
        });
        
        toolkit.createLabel(sourceSectionClient, "System ID:");
        String systemId = getIdentityMapping().getSourceSystemId();
        if (systemId == null) systemId = "";
        sourceSystemIdText = toolkit.createText(sourceSectionClient, systemId, SWT.BORDER);
        GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        sourceSystemIdText.setLayoutData(gd);        
        
        toolkit.createLabel(sourceSectionClient, "Repository ID:");
        String repositoryId = getIdentityMapping().getSourceRepositoryId();
        if (repositoryId == null) repositoryId = "";
        sourceRepositoryIdText = toolkit.createText(sourceSectionClient, repositoryId, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        sourceRepositoryIdText.setLayoutData(gd);

        toolkit.createLabel(sourceSectionClient, "System kind:");
        String systemKind = getIdentityMapping().getSourceSystemKind();
        if (systemKind == null) systemKind = "";
        sourceSystemKindText = toolkit.createText(sourceSectionClient, systemKind, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        sourceSystemKindText.setLayoutData(gd);
        
        toolkit.createLabel(sourceSectionClient, "Repository kind:");
        String repositoryKind = getIdentityMapping().getSourceRepositoryKind();
        if (repositoryKind == null) repositoryKind = "";
        sourceRepositoryKindText = toolkit.createText(sourceSectionClient, repositoryKind, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        sourceRepositoryKindText.setLayoutData(gd);
        
        toolkit.createLabel(sourceSectionClient, "Artifact ID:");
        String artifactId = getIdentityMapping().getSourceArtifactId();
        if (artifactId == null) artifactId = "";
        sourceArtifactIdText = toolkit.createText(sourceSectionClient, artifactId, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        sourceArtifactIdText.setLayoutData(gd);
        
        toolkit.createLabel(sourceSectionClient, "Last modification:");
        sourceLastModificationText = toolkit.createText(sourceSectionClient, sourceLastModification, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        sourceLastModificationText.setLayoutData(gd);        
        
        toolkit.createLabel(sourceSectionClient, "Artifact version:");
        String artifactVersion = getIdentityMapping().getSourceArtifactVersion();
        if (artifactVersion == null) artifactVersion = "";
        sourceArtifactVersionText = toolkit.createText(sourceSectionClient, artifactVersion, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        sourceArtifactVersionText.setLayoutData(gd);
        
	}
	
	private void createTargetSection(Composite composite) {
		TableWrapData td;
		targetSection = toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        targetSection.setLayoutData(td);
        targetSection.setText("Target");
        targetSectionClient = toolkit.createComposite(targetSection); 
        GridLayout targetLayout = new GridLayout();
        targetLayout.numColumns = 2;
        targetLayout.verticalSpacing = 10;
        targetSectionClient.setLayout(targetLayout);
        targetSection.setClient(targetSectionClient);
        targetSection.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
                if (e.getState()) settings.put(TARGET_SECTION_STATE, STATE_EXPANDED);
                else settings.put(TARGET_SECTION_STATE, STATE_CONTRACTED);
            }
        });
        
        toolkit.createLabel(targetSectionClient, "System ID:");
        String systemId = getIdentityMapping().getTargetSystemId();
        if (systemId == null) systemId = "";
        targetSystemIdText = toolkit.createText(targetSectionClient, systemId, SWT.BORDER);
        GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        targetSystemIdText.setLayoutData(gd);
        
        toolkit.createLabel(targetSectionClient, "Repository ID:");
        String repositoryId = getIdentityMapping().getTargetRepositoryId();
        if (repositoryId == null) repositoryId = "";
        targetRepositoryIdText = toolkit.createText(targetSectionClient, repositoryId, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        targetRepositoryIdText.setLayoutData(gd);
        
        toolkit.createLabel(targetSectionClient, "System kind:");
        String systemKind = getIdentityMapping().getTargetSystemKind();
        if (systemKind == null) systemKind = "";
        targetSystemKindText = toolkit.createText(targetSectionClient, systemKind, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        targetSystemKindText.setLayoutData(gd);
        
        toolkit.createLabel(targetSectionClient, "Repository kind:");
        String repositoryKind = getIdentityMapping().getTargetRepositoryKind();
        if (repositoryKind == null) repositoryKind = "";
        targetRepositoryKindText = toolkit.createText(targetSectionClient, repositoryKind, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        targetRepositoryKindText.setLayoutData(gd);
        
        toolkit.createLabel(targetSectionClient, "Artifact ID:");
        String artifactId = getIdentityMapping().getTargetArtifactId();
        if (artifactId == null) artifactId = "";
        targetArtifactIdText = toolkit.createText(targetSectionClient, artifactId, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        targetArtifactIdText.setLayoutData(gd);
        
        toolkit.createLabel(targetSectionClient, "Last modification:");
        targetLastModificationText = toolkit.createText(targetSectionClient, targetLastModification, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        targetLastModificationText.setLayoutData(gd);     
        
        toolkit.createLabel(targetSectionClient, "Artifact version:");
        String artifactVersion = getIdentityMapping().getTargetArtifactVersion();
        if (artifactVersion == null) artifactVersion = "";
        targetArtifactVersionText = toolkit.createText(targetSectionClient, artifactVersion, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        targetArtifactVersionText.setLayoutData(gd);

	}
	
	private void createParentSection(Composite composite) {
		TableWrapData td;
		parentSection = toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        parentSection.setLayoutData(td);
        parentSection.setText("Parent");
        parentSectionClient = toolkit.createComposite(parentSection); 
        GridLayout parentLayout = new GridLayout();
        parentLayout.numColumns = 2;
        parentLayout.verticalSpacing = 10;
        parentSectionClient.setLayout(parentLayout);
        parentSection.setClient(parentSectionClient);
        parentSection.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
                if (e.getState()) settings.put(PARENT_SECTION_STATE, STATE_EXPANDED);
                else settings.put(PARENT_SECTION_STATE, STATE_CONTRACTED);
            }
        });
        
        toolkit.createLabel(parentSectionClient, "Source repository ID:");
        String repositoryId = getIdentityMapping().getParentSourceRepositoryId();
        if (repositoryId == null) repositoryId = "";
        parentSourceRepositoryIdText = toolkit.createText(parentSectionClient, repositoryId, SWT.BORDER);
        GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        parentSourceRepositoryIdText.setLayoutData(gd);
        
        toolkit.createLabel(parentSectionClient, "Source repository kind:");
        String repositoryKind = getIdentityMapping().getParentSourceRepositoryKind();
        if (repositoryKind == null) repositoryKind = "";
        parentSourceRepositoryKindText = toolkit.createText(parentSectionClient, repositoryKind, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        parentSourceRepositoryKindText.setLayoutData(gd);
        
        toolkit.createLabel(parentSectionClient, "Source artifact ID:");
        String artifactId = getIdentityMapping().getParentSourceArtifactId();
        if (artifactId == null) artifactId = "";
        parentSourceArtifactIdText = toolkit.createText(parentSectionClient, artifactId, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        parentSourceArtifactIdText.setLayoutData(gd);
        
        toolkit.createLabel(parentSectionClient, "Target repository ID:");
        repositoryId = getIdentityMapping().getParentTargetRepositoryId();
        if (repositoryId == null) repositoryId = "";
        parentTargetRepositoryIdText = toolkit.createText(parentSectionClient, repositoryId, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        parentTargetRepositoryIdText.setLayoutData(gd);
        
        toolkit.createLabel(parentSectionClient, "Target repository kind:");
        repositoryKind = getIdentityMapping().getParentTargetRepositoryKind();
        if (repositoryKind == null) repositoryKind = "";
        parentTargetRepositoryKindText = toolkit.createText(parentSectionClient, repositoryKind, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        parentTargetRepositoryKindText.setLayoutData(gd);
        
        toolkit.createLabel(parentSectionClient, "Target artifact ID:");
        artifactId = getIdentityMapping().getParentTargetArtifactId();
        if (artifactId == null) artifactId = "";
        parentTargetArtifactIdText = toolkit.createText(parentSectionClient, artifactId, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        parentTargetArtifactIdText.setLayoutData(gd);
   
	}

	private void createChildSection(Composite composite) {
		TableWrapData td;
		childSection = toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        childSection.setLayoutData(td);
        childSection.setText("Child");
        childSectionClient = toolkit.createComposite(childSection); 
        GridLayout childLayout = new GridLayout();
        childLayout.numColumns = 2;
        childLayout.verticalSpacing = 10;
        childSectionClient.setLayout(childLayout);
        childSection.setClient(childSectionClient);
        childSection.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
                if (e.getState()) settings.put(CHILD_SECTION_STATE, STATE_EXPANDED);
                else settings.put(CHILD_SECTION_STATE, STATE_CONTRACTED);
            }
        });
        
        toolkit.createLabel(childSectionClient, "Source repository ID:");
        String repositoryId = getIdentityMapping().getChildSourceRepositoryId();
        if (repositoryId == null) repositoryId = "";
        childSourceRepositoryIdText = toolkit.createText(childSectionClient, repositoryId, SWT.BORDER);
        GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        childSourceRepositoryIdText.setLayoutData(gd);
        
        toolkit.createLabel(childSectionClient, "Source repository kind:");
        String repositoryKind = getIdentityMapping().getChildSourceRepositoryKind();
        if (repositoryKind == null) repositoryKind = "";
        childSourceRepositoryKindText = toolkit.createText(childSectionClient, repositoryKind, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        childSourceRepositoryKindText.setLayoutData(gd);
        
        toolkit.createLabel(childSectionClient, "Source artifact ID:");
        String artifactId = getIdentityMapping().getChildSourceArtifactId();
        if (artifactId == null) artifactId = "";
        childSourceArtifactIdText = toolkit.createText(childSectionClient, artifactId, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        childSourceArtifactIdText.setLayoutData(gd);
        
        toolkit.createLabel(childSectionClient, "Target repository ID:");
        repositoryId = getIdentityMapping().getChildTargetRepositoryId();
        if (repositoryId == null) repositoryId = "";
        childTargetRepositoryIdText = toolkit.createText(childSectionClient, repositoryId, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        childTargetRepositoryIdText.setLayoutData(gd);
        
        toolkit.createLabel(childSectionClient, "Target repository kind:");
        repositoryKind = getIdentityMapping().getChildTargetRepositoryKind();
        if (repositoryKind == null) repositoryKind = "";
        childTargetRepositoryKindText = toolkit.createText(childSectionClient, repositoryKind, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        childTargetRepositoryKindText.setLayoutData(gd);
        
        toolkit.createLabel(childSectionClient, "Target artifact ID:");
        artifactId = getIdentityMapping().getChildTargetArtifactId();
        if (artifactId == null) artifactId = "";
        childTargetArtifactIdText = toolkit.createText(childSectionClient, artifactId, SWT.BORDER);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        childTargetArtifactIdText.setLayoutData(gd);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		saveError = false;
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				Filter sourceRepositoryIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, sourceRepositoryId, true);
				Filter targetRepositoryIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, targetRepositoryId, true);
				Filter sourceArtifactIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_ID, sourceArtifactId, true);
				Filter artifactTypeFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_ARTIFACT_TYPE, artifactType, true);
				Filter[] filters = { sourceRepositoryIdFilter, targetRepositoryIdFilter, sourceArtifactIdFilter, artifactTypeFilter };
				
				Update sourceSystemIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_ID, sourceSystemIdText.getText().trim());
				Update sourceRepositoryIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, sourceRepositoryIdText.getText().trim());
				Update sourceSystemKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_KIND, sourceSystemKindText.getText().trim());
				Update sourceRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_KIND, sourceRepositoryKindText.getText().trim());
				Update sourceArtifactIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_ID, sourceArtifactIdText.getText().trim());
				Update sourceLastModificationUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_LAST_MODIFICATION_TIME, sourceLastModificationText.getText().trim());
				Update sourceArtifactVersionUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_VERSION, sourceArtifactVersionText.getText().trim());
				
				Update targetSystemIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_ID, targetSystemIdText.getText().trim());
				Update targetRepositoryIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, targetRepositoryIdText.getText().trim());
				Update targetSystemKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_KIND, targetSystemKindText.getText().trim());
				Update targetRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_KIND, targetRepositoryKindText.getText().trim());
				Update targetArtifactIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_ARTIFACT_ID, targetArtifactIdText.getText().trim());
				Update targetLastModificationUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_LAST_MODIFICATION_TIME, targetLastModificationText.getText().trim());			
				Update targetArtifactVersionUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_ARTIFACT_VERSION, targetArtifactVersionText.getText().trim());

				Update artifactTypeUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_ARTIFACT_TYPE, artifactTypeText.getText().trim());
				
				Update childSourceArtifactIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_SOURCE_ARTIFACT_ID, childSourceArtifactIdText.getText().trim());
				Update childSourceRepositoryIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_SOURCE_REPOSITORY_ID, childSourceRepositoryIdText.getText().trim());
				Update childSourceRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_SOURCE_REPOSITORY_KIND, childSourceRepositoryKindText.getText().trim());
				Update childTargetArtifactIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_TARGET_ARTIFACT_ID, childTargetArtifactIdText.getText().trim());
				Update childTargetRepositoryIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_TARGET_REPOSITORY_ID, childTargetRepositoryIdText.getText().trim());
				Update childTargetRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_TARGET_REPOSITORY_KIND, childTargetRepositoryKindText.getText().trim());
				
				Update parentSourceArtifactIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_SOURCE_ARTIFACT_ID, parentSourceArtifactIdText.getText().trim());
				Update parentSourceRepositoryIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_SOURCE_REPOSITORY_ID, parentSourceRepositoryIdText.getText().trim());
				Update parentSourceRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_SOURCE_REPOSITORY_KIND, parentSourceRepositoryKindText.getText().trim());
				Update parentTargetArtifactIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_TARGET_ARTIFACT_ID, parentTargetArtifactIdText.getText().trim());
				Update parentTargetRepositoryIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_TARGET_REPOSITORY_ID, parentTargetRepositoryIdText.getText().trim());
				Update parentTargetRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_TARGET_REPOSITORY_KIND, parentTargetRepositoryKindText.getText().trim());
				
				Update[] updates = { 
						sourceSystemIdUpdate,
						sourceRepositoryIdUpdate,
						sourceSystemKindUpdate,
						sourceRepositoryKindUpdate,
						sourceArtifactIdUpdate,
						sourceLastModificationUpdate,
						sourceArtifactVersionUpdate,
						targetSystemIdUpdate,
						targetRepositoryIdUpdate,
						targetSystemKindUpdate,
						targetRepositoryKindUpdate,
						targetArtifactIdUpdate,
						targetLastModificationUpdate,
						targetArtifactVersionUpdate,
						artifactTypeUpdate,
						childSourceArtifactIdUpdate,
						childSourceRepositoryIdUpdate,
						childSourceRepositoryKindUpdate,
						childTargetArtifactIdUpdate,
						childTargetRepositoryIdUpdate,
						childTargetRepositoryKindUpdate,						
						parentSourceArtifactIdUpdate,
						parentSourceRepositoryIdUpdate,
						parentSourceRepositoryKindUpdate,
						parentTargetArtifactIdUpdate,
						parentTargetRepositoryIdUpdate,
						parentTargetRepositoryKindUpdate
				};
				
				try {
					dataProvider.updateIdentityMappings(getIdentityMapping().getLandscape(), updates, filters);
				} catch (Exception e) {
					saveError = true;
					Activator.handleError(e);
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Save Identity Mapping", e.getMessage());
				}
			}			
		});
		
		if (saveError) return;
		
		sourceSystemId = sourceSystemIdText.getText().trim();
		sourceRepositoryId = sourceRepositoryIdText.getText().trim();
		sourceSystemKind = sourceSystemKindText.getText().trim();
		sourceRepositoryKind = sourceRepositoryKindText.getText().trim();
		sourceArtifactId = sourceArtifactIdText.getText().trim();
		sourceLastModification = sourceLastModificationText.getText().trim();
		sourceArtifactVersion = sourceArtifactVersionText.getText().trim();
		
		targetSystemId = targetSystemIdText.getText().trim();
		targetRepositoryId = targetRepositoryIdText.getText().trim();
		targetSystemKind = targetSystemKindText.getText().trim();
		targetRepositoryKind = targetRepositoryKindText.getText().trim();
		targetArtifactId = targetArtifactIdText.getText().trim();
		targetLastModification = targetLastModificationText.getText().trim();
		targetArtifactVersion = targetArtifactVersionText.getText().trim();
		
		artifactType = artifactTypeText.getText().trim();
		
		childSourceRepositoryId = childSourceRepositoryIdText.getText().trim();
		childSourceRepositoryKind = childSourceRepositoryKindText.getText().trim();
		childSourceArtifactId = childSourceArtifactIdText.getText().trim();
		childTargetRepositoryId = childTargetRepositoryIdText.getText().trim();
		childTargetRepositoryKind = childTargetRepositoryKindText.getText().trim();
		childTargetArtifactId = childTargetArtifactIdText.getText().trim();
		
		parentSourceRepositoryId = parentSourceRepositoryIdText.getText().trim();
		parentSourceRepositoryKind = parentSourceRepositoryKindText.getText().trim();
		parentSourceArtifactId = parentSourceArtifactIdText.getText().trim();
		parentTargetRepositoryId = parentTargetRepositoryIdText.getText().trim();
		parentTargetRepositoryKind = parentTargetRepositoryKindText.getText().trim();
		parentTargetArtifactId = parentTargetArtifactIdText.getText().trim();
	}
	
	@Override
	public boolean canLeaveThePage() {
		if (sourceLastModificationText.getText().trim().length() > 0) {
			Timestamp timestamp = null;
			try {
				timestamp = Timestamp.valueOf(sourceLastModificationText.getText().trim());
			} catch (Exception e) {}
			if (timestamp == null) return false;
		}
		if (targetLastModificationText.getText().trim().length() > 0) {
			Timestamp timestamp = null;
			try {
				timestamp = Timestamp.valueOf(targetLastModificationText.getText().trim());
			} catch (Exception e) {}
			if (timestamp == null) return false;
		}
		return true;
	}
	
	public boolean isDirty() {
		if (sourceRepositoryIdText == null) return false;
		return !sourceRepositoryIdText.getText().trim().equals(sourceRepositoryId) ||
		!sourceSystemIdText.getText().trim().equals(sourceSystemId) ||
		!sourceSystemKindText.getText().trim().equals(sourceSystemKind) ||
		!sourceRepositoryKindText.getText().trim().equals(sourceRepositoryKind) ||
		!sourceArtifactIdText.getText().trim().equals(sourceArtifactId) ||
		!sourceLastModificationText.getText().trim().equals(sourceLastModification) ||
		!sourceArtifactVersionText.getText().trim().equals(sourceArtifactVersion) ||
		!targetSystemIdText.getText().trim().equals(targetSystemId)	||
		!targetRepositoryIdText.getText().trim().equals(targetRepositoryId)	||
		!targetSystemKindText.getText().trim().equals(targetSystemKind) ||
		!targetRepositoryKindText.getText().trim().equals(targetRepositoryKind) ||
		!targetArtifactIdText.getText().trim().equals(targetArtifactId) ||	
		!targetLastModificationText.getText().trim().equals(targetLastModification) ||
		!targetArtifactVersionText.getText().trim().equals(targetArtifactVersion) ||
		!artifactTypeText.getText().trim().equals(artifactType) ||
		!childSourceArtifactIdText.getText().trim().equals(childSourceArtifactId) ||
		!childSourceRepositoryIdText.getText().trim().equals(childSourceRepositoryId) ||
		!childSourceRepositoryKindText.getText().trim().equals(childSourceRepositoryKind) ||
		!childTargetArtifactIdText.getText().trim().equals(childTargetArtifactId) ||
		!childTargetRepositoryIdText.getText().trim().equals(childTargetRepositoryId) ||
		!childTargetRepositoryKindText.getText().trim().equals(childTargetRepositoryKind) ||
		!parentSourceArtifactIdText.getText().trim().equals(parentSourceArtifactId) ||
		!parentSourceRepositoryIdText.getText().trim().equals(parentSourceRepositoryId) ||
		!parentSourceRepositoryKindText.getText().trim().equals(parentSourceRepositoryKind) ||
		!parentTargetArtifactIdText.getText().trim().equals(parentTargetArtifactId) ||
		!parentTargetRepositoryIdText.getText().trim().equals(parentTargetRepositoryId) ||
		!parentTargetRepositoryKindText.getText().trim().equals(parentTargetRepositoryKind);	
	}
	
	public boolean isSaveError() {
		return saveError;
	}
	
	public IdentityMapping getIdentityMapping() {
		return ((IdentityMappingEditorInput)getEditorInput()).getIdentityMapping();
	}

}
