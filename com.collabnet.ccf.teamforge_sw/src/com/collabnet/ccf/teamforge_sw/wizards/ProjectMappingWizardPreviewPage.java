package com.collabnet.ccf.teamforge_sw.wizards;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.collabnet.ccf.Activator;

public class ProjectMappingWizardPreviewPage extends WizardPage {
	private Table table;
	private TableViewer viewer;
	
	private String[] mappings;
	
	private String product;
	private String projectId;
	private String taskTracker;
	private String pbiTracker;
	
	private String[] columnHeaders = {"Project Mapping"};
	private ColumnLayoutData columnLayouts[] = {
		new ColumnWeightData(450, 450, true)};

	public ProjectMappingWizardPreviewPage() {
		super("previewPage", "Mappings Preview", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_PROJECT_MAPPING_WIZBAN));
		setPageComplete(true);
	}

	public void createControl(Composite parent) {
		Composite outerContainer = new Composite(parent,SWT.NONE);
		outerContainer.setLayout(new GridLayout());
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		table = new Table(outerContainer, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL);
		gd.widthHint = 500;
		gd.heightHint = 200;
		table.setLayoutData(gd);
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);
		viewer = new TableViewer(table);		
		viewer.setContentProvider(new MappingsContentProvider());
		viewer.setLabelProvider(new MappingsLabelProvider());
		for (int i = 0; i < columnHeaders.length; i++) {
			tableLayout.addColumnData(columnLayouts[i]);
			TableColumn tc = new TableColumn(table, SWT.NONE,i);
			tc.setResizable(columnLayouts[i].resizable);
			tc.setText(columnHeaders[i]);
		}
		viewer.setInput(this);	

		setMessage("The following mappings will be created.");

		setControl(outerContainer);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {		
			if (product == null || !product.equals(((ProjectMappingWizard)getWizard()).getSelectedProduct().getName()) ||
				projectId == null || !projectId.equals(((ProjectMappingWizard)getWizard()).getSelectedProject().getId()) ||
				taskTracker == null || !taskTracker.equals(((ProjectMappingWizard)getWizard()).getSelectedTaskTracker().getId()) ||
				pbiTracker == null || !pbiTracker.equals(((ProjectMappingWizard)getWizard()).getSelectedPbiTracker().getId())) {
				product = ((ProjectMappingWizard)getWizard()).getSelectedProduct().getName();
				projectId = ((ProjectMappingWizard)getWizard()).getSelectedProject().getId();
				taskTracker = ((ProjectMappingWizard)getWizard()).getSelectedTaskTracker().getId();
				pbiTracker = ((ProjectMappingWizard)getWizard()).getSelectedPbiTracker().getId();
				mappings = getMappings();
				viewer.refresh();
			}
		}
	}	
	
	private String[] getMappings() {
		ProjectMappingWizard wizard = (ProjectMappingWizard)getWizard();
		String trackerTaskMapping = wizard.getSelectedTaskTracker().getId() + "=>" + wizard.getSelectedProduct().getName() + "-Task";
		String trackerPbiMapping = wizard.getSelectedPbiTracker().getId() + "=>" + wizard.getSelectedProduct().getName() + "-PBI";
		String planningFolderProductMapping = wizard.getSelectedProject().getId() + "-planningFolders=>" + wizard.getSelectedProduct().getName() + "-Product";
		String taskTrackerMapping = wizard.getSelectedProduct().getName() + "-Task=>" + wizard.getSelectedTaskTracker().getId();
		String pbiTrackerMapping = wizard.getSelectedProduct().getName() + "-PBI=>" + wizard.getSelectedPbiTracker().getId();
		String productPlanningFolderMapping = wizard.getSelectedProduct().getName() + "-Product=>" + wizard.getSelectedProject().getId() + "-planningFolders";
		String[] mappings = { trackerTaskMapping, trackerPbiMapping, planningFolderProductMapping, taskTrackerMapping, pbiTrackerMapping, productPlanningFolderMapping };
		return mappings;
	}
	
	static class MappingsLabelProvider extends LabelProvider implements ITableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) { 
				case 0: return element.toString();
			}
			return "";  //$NON-NLS-1$
		}
		
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	
	}	
	
	class MappingsContentProvider implements IStructuredContentProvider {
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public Object[] getElements(Object obj) {
			if (mappings == null) {
				return new String[0];
			}
			return mappings;
		}
	}	

}
