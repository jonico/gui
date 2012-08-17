package com.collabnet.ccf.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.EditorPart;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Update;
import com.collabnet.ccf.dialogs.UpdateReverseIdentityMappingsDialog;
import com.collabnet.ccf.model.IdentityMapping;
import com.collabnet.ccf.views.IdentityMappingView;

public class IdentityMappingEditor extends FormEditor implements ISaveablePart2 {
	private IdentityMapping identityMapping;
	private IdentityMapping reverseIdentityMapping;
	
	private IdentityMappingEditorPage detailsPage;
	private IdentityMappingEditorPage reversePage;
	
	public final static String ID = "com.collabnet.ccf.editors.IdentityMappingEditor";
	
	public IdentityMappingEditor() {
		super();
	}
	
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        setSite(site);
        setInput(input);
        setPartName(input.getName());
        setTitleImage(Activator.getImage(Activator.IMAGE_IDENTITY_MAPPING));
        identityMapping = ((IdentityMappingEditorInput)input).getIdentityMapping();
        reverseIdentityMapping = ((IdentityMappingEditorInput)input).getReverseIdentityMapping();
    }

	@SuppressWarnings("unchecked")
	@Override
	protected void addPages() {
       try {
        	detailsPage = new IdentityMappingEditorPage(this, "details", getEditorInput().getName(), identityMapping);
	        int detailsIndex = addPage(detailsPage);
	        setPageText(detailsIndex, identityMapping.getEditableValue().toString());
	        pages.add(detailsPage);
	        if (reverseIdentityMapping != null) {
	           	reversePage = new IdentityMappingEditorPage(this, "reverse", getEditorInput().getName(), reverseIdentityMapping);
		        int reverseIndex = addPage(reversePage);
		        setPageText(reverseIndex, reverseIdentityMapping.getEditableValue().toString());
		        pages.add(reversePage);
	        }
        } catch (Exception e) { 
        	Activator.handleError(e);
        }
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (detailsPage.isDirty()) detailsPage.doSave(monitor);
		if (detailsPage.isSaveError()) {
			monitor.setCanceled(true);
		} else {
			if (reversePage != null) {
				if (reversePage.isDirty()) reversePage.doSave(monitor);
				if (reversePage.isSaveError()) {
					monitor.setCanceled(true);
				}
			}
		}
		setDirty();
		if (IdentityMappingView.getView() != null) {
			IdentityMappingView.getView().refresh();
		}
		
		if (reverseIdentityMapping != null) {
			List<Update> reverseUpdateList = new ArrayList<Update>();
			List<Update> updateList = new ArrayList<Update>();
			if (detailsPage.isSourceSystemIdChanged()) {
				if (reversePage == null || !reversePage.isTargetSystemIdChanged()) {		
					Update targetSystemIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_ID, identityMapping.getSourceSystemId());
					reverseUpdateList.add(targetSystemIdUpdate);
				}	
			}
			if (detailsPage.isSourceRepositoryIdChanged()) {
				if (reversePage == null || !reversePage.isTargetRepositoryIdChanged()) {		
					Update targetRepositoryIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, identityMapping.getSourceRepositoryId());
					reverseUpdateList.add(targetRepositoryIdUpdate);
				}	
			}
			if (detailsPage.isSourceSystemKindChanged()) {
				if (reversePage == null || !reversePage.isTargetSystemKindChanged()) {		
					Update targetSystemKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_KIND, identityMapping.getSourceSystemKind());
					reverseUpdateList.add(targetSystemKindUpdate);
				}	
			}	
			if (detailsPage.isSourceRepositoryKindChanged()) {
				if (reversePage == null || !reversePage.isTargetRepositoryKindChanged()) {		
					Update targetRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_KIND, identityMapping.getSourceRepositoryKind());
					reverseUpdateList.add(targetRepositoryKindUpdate);
				}	
			}
			if (detailsPage.isSourceArtifactIdChanged()) {
				if (reversePage == null || !reversePage.isTargetArtifactIdChanged()) {		
					Update targetArtifactIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_ARTIFACT_ID, identityMapping.getSourceArtifactId());
					reverseUpdateList.add(targetArtifactIdUpdate);
				}	
			}
			if (detailsPage.isSourceLastModificationChanged()) {
				if (reversePage == null || !reversePage.isTargetLastModificationChanged()) {		
					Update targetLastModificationUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_LAST_MODIFICATION_TIME, identityMapping.getSourceLastModificationTime().toString());
					reverseUpdateList.add(targetLastModificationUpdate);
				}	
			}	
			if (detailsPage.isSourceArtifactVersionChanged()) {
				if (reversePage == null || !reversePage.isTargetArtifactVersionChanged()) {		
					Update targetArtifactVersionUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_ARTIFACT_VERSION, identityMapping.getSourceArtifactVersion());
					reverseUpdateList.add(targetArtifactVersionUpdate);
				}	
			}			
			if (detailsPage.isTargetSystemIdChanged()) {
				if (reversePage == null || !reversePage.isSourceSystemIdChanged()) {		
					Update sourceSystemIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_ID, identityMapping.getTargetSystemId());
					reverseUpdateList.add(sourceSystemIdUpdate);
				}	
			}
			if (detailsPage.isTargetRepositoryIdChanged()) {
				if (reversePage == null || !reversePage.isSourceRepositoryIdChanged()) {		
					Update sourceRepositoryIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, identityMapping.getTargetRepositoryId());
					reverseUpdateList.add(sourceRepositoryIdUpdate);
				}	
			}
			if (detailsPage.isTargetSystemKindChanged()) {
				if (reversePage == null || !reversePage.isSourceSystemKindChanged()) {		
					Update sourceSystemKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_KIND, identityMapping.getTargetSystemKind());
					reverseUpdateList.add(sourceSystemKindUpdate);
				}	
			}	
			if (detailsPage.isTargetRepositoryKindChanged()) {
				if (reversePage == null || !reversePage.isSourceRepositoryKindChanged()) {		
					Update sourceRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_KIND, identityMapping.getTargetRepositoryKind());
					reverseUpdateList.add(sourceRepositoryKindUpdate);
				}	
			}
			if (detailsPage.isTargetArtifactIdChanged()) {
				if (reversePage == null || !reversePage.isSourceArtifactIdChanged()) {		
					Update sourceArtifactIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_ID, identityMapping.getTargetArtifactId());
					reverseUpdateList.add(sourceArtifactIdUpdate);
				}	
			}
			if (detailsPage.isTargetLastModificationChanged()) {
				if (reversePage == null || !reversePage.isSourceLastModificationChanged()) {		
					Update sourceLastModificationUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_LAST_MODIFICATION_TIME, identityMapping.getTargetLastModificationTime().toString());
					reverseUpdateList.add(sourceLastModificationUpdate);
				}	
			}	
			if (detailsPage.isTargetArtifactVersionChanged()) {
				if (reversePage == null || !reversePage.isSourceArtifactVersionChanged()) {		
					Update sourceArtifactVersionUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_VERSION, identityMapping.getTargetArtifactVersion());
					reverseUpdateList.add(sourceArtifactVersionUpdate);
				}	
			}
			
			if (reversePage != null) {
				if (reversePage.isSourceSystemIdChanged()) {
					if (detailsPage == null || !detailsPage.isTargetSystemIdChanged()) {		
						Update targetSystemIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_ID, reverseIdentityMapping.getSourceSystemId());
						updateList.add(targetSystemIdUpdate);
					}	
				}
				if (reversePage.isSourceRepositoryIdChanged()) {
					if (detailsPage == null || !detailsPage.isTargetRepositoryIdChanged()) {		
						Update targetRepositoryIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, reverseIdentityMapping.getSourceRepositoryId());
						updateList.add(targetRepositoryIdUpdate);
					}	
				}
				if (reversePage.isSourceSystemKindChanged()) {
					if (detailsPage == null || !detailsPage.isTargetSystemKindChanged()) {		
						Update targetSystemKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_KIND, reverseIdentityMapping.getSourceSystemKind());
						updateList.add(targetSystemKindUpdate);
					}	
				}	
				if (reversePage.isSourceRepositoryKindChanged()) {
					if (detailsPage == null || !detailsPage.isTargetRepositoryKindChanged()) {		
						Update targetRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_KIND, reverseIdentityMapping.getSourceRepositoryKind());
						updateList.add(targetRepositoryKindUpdate);
					}	
				}
				if (reversePage.isSourceArtifactIdChanged()) {
					if (detailsPage == null || !detailsPage.isTargetArtifactIdChanged()) {		
						Update targetArtifactIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_ARTIFACT_ID, reverseIdentityMapping.getSourceArtifactId());
						updateList.add(targetArtifactIdUpdate);
					}	
				}
				if (reversePage.isSourceLastModificationChanged()) {
					if (detailsPage == null || !detailsPage.isTargetLastModificationChanged()) {		
						Update targetLastModificationUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_LAST_MODIFICATION_TIME, reverseIdentityMapping.getSourceLastModificationTime().toString());
						updateList.add(targetLastModificationUpdate);
					}	
				}	
				if (reversePage.isSourceArtifactVersionChanged()) {
					if (detailsPage == null || !detailsPage.isTargetArtifactVersionChanged()) {		
						Update targetArtifactVersionUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_ARTIFACT_VERSION, reverseIdentityMapping.getSourceArtifactVersion());
						updateList.add(targetArtifactVersionUpdate);
					}	
				}			
				if (reversePage.isTargetSystemIdChanged()) {
					if (detailsPage == null || !detailsPage.isSourceSystemIdChanged()) {		
						Update sourceSystemIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_ID, reverseIdentityMapping.getTargetSystemId());
						updateList.add(sourceSystemIdUpdate);
					}	
				}
				if (reversePage.isTargetRepositoryIdChanged()) {
					if (detailsPage == null || !detailsPage.isSourceRepositoryIdChanged()) {		
						Update sourceRepositoryIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, reverseIdentityMapping.getTargetRepositoryId());
						updateList.add(sourceRepositoryIdUpdate);
					}	
				}
				if (reversePage.isTargetSystemKindChanged()) {
					if (detailsPage == null || !detailsPage.isSourceSystemKindChanged()) {		
						Update sourceSystemKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_KIND, reverseIdentityMapping.getTargetSystemKind());
						updateList.add(sourceSystemKindUpdate);
					}	
				}	
				if (reversePage.isTargetRepositoryKindChanged()) {
					if (detailsPage == null || !detailsPage.isSourceRepositoryKindChanged()) {		
						Update sourceRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_KIND, reverseIdentityMapping.getTargetRepositoryKind());
						updateList.add(sourceRepositoryKindUpdate);
					}	
				}
				if (reversePage.isTargetArtifactIdChanged()) {
					if (detailsPage == null || !detailsPage.isSourceArtifactIdChanged()) {		
						Update sourceArtifactIdUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_ID, reverseIdentityMapping.getTargetArtifactId());
						updateList.add(sourceArtifactIdUpdate);
					}	
				}
				if (reversePage.isTargetLastModificationChanged()) {
					if (detailsPage == null || !detailsPage.isSourceLastModificationChanged()) {		
						Update sourceLastModificationUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_LAST_MODIFICATION_TIME, reverseIdentityMapping.getTargetLastModificationTime().toString());
						updateList.add(sourceLastModificationUpdate);
					}	
				}	
				if (reversePage.isTargetArtifactVersionChanged()) {
					if (detailsPage == null || !detailsPage.isSourceArtifactVersionChanged()) {		
						Update sourceArtifactVersionUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_VERSION, reverseIdentityMapping.getTargetArtifactVersion());
						updateList.add(sourceArtifactVersionUpdate);
					}	
				}
			}
			
			if (reverseUpdateList.size() > 0 || updateList.size() > 0) {
				UpdateReverseIdentityMappingsDialog dialog = new UpdateReverseIdentityMappingsDialog(Display.getDefault().getActiveShell(), identityMapping, reverseIdentityMapping, updateList, reverseUpdateList);
				dialog.open();
			}
			
//			if (reverseUpdateList.size() > 0) {
//				CcfDataProvider dataProvider = new CcfDataProvider();
//				
//				Filter sourceRepositoryIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, reverseIdentityMapping.getSourceRepositoryId(), true);
//				Filter targetRepositoryIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, reverseIdentityMapping.getTargetRepositoryId(), true);
//				Filter sourceArtifactIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_ID, reverseIdentityMapping.getSourceArtifactId(), true);
//				Filter artifactTypeFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_ARTIFACT_TYPE, reverseIdentityMapping.getArtifactType(), true);
//				Filter[] filters = { sourceRepositoryIdFilter, targetRepositoryIdFilter, sourceArtifactIdFilter, artifactTypeFilter };
//				
//				Update[] updates = new Update[reverseUpdateList.size()];
//				reverseUpdateList.toArray(updates);
//				
//				try {
//					dataProvider.updateIdentityMappings(reverseIdentityMapping.getLandscape(), updates, filters);
//				} catch (Exception e) {
//					monitor.setCanceled(true);
//					Activator.handleError(e);
//					MessageDialog.openError(Display.getDefault().getActiveShell(), "Save Identity Mapping", e.getMessage());
//				}
//			}
//			
//			if (updateList.size() > 0) {
//				CcfDataProvider dataProvider = new CcfDataProvider();
//				
//				Filter sourceRepositoryIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, identityMapping.getSourceRepositoryId(), true);
//				Filter targetRepositoryIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, identityMapping.getTargetRepositoryId(), true);
//				Filter sourceArtifactIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_ID, identityMapping.getSourceArtifactId(), true);
//				Filter artifactTypeFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_ARTIFACT_TYPE, identityMapping.getArtifactType(), true);
//				Filter[] filters = { sourceRepositoryIdFilter, targetRepositoryIdFilter, sourceArtifactIdFilter, artifactTypeFilter };
//				
//				Update[] updates = new Update[updateList.size()];
//				updateList.toArray(updates);
//				
//				try {
//					dataProvider.updateIdentityMappings(identityMapping.getLandscape(), updates, filters);
//				} catch (Exception e) {
//					monitor.setCanceled(true);
//					Activator.handleError(e);
//					MessageDialog.openError(Display.getDefault().getActiveShell(), "Save Identity Mapping", e.getMessage());
//				}
//			}
//			
		}
		if (IdentityMappingView.getView() != null) {
			IdentityMappingView.getView().refresh();
		}
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

    public void setDirty() {
    	firePropertyChange(EditorPart.PROP_DIRTY); 
    }

	public int promptToSaveOnClose() {
		String[] buttons = { "&No", "&Cancel", "&Yes" };
		MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Save Identity Mapping", null, "'" + identityMapping.getEditableValue() + "' has been modified.  Save changes?", MessageDialog.QUESTION, buttons, 2);
		switch (dialog.open()) {
		case 0:	
			return ISaveablePart2.NO;
		case 1:	
			return ISaveablePart2.CANCEL;
		case 2:	
			return ISaveablePart2.YES;			
		default:
			return ISaveablePart2.DEFAULT;
		}
	}

}
