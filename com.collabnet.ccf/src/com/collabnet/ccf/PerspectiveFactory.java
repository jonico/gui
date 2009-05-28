package com.collabnet.ccf;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.collabnet.ccf.views.CcfExplorerView;
import com.collabnet.ccf.views.HospitalView;
import com.collabnet.ccf.views.IdentityMappingConsistencyCheckView;
import com.collabnet.ccf.views.IdentityMappingView;

public class PerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout factory) {
		addViews(factory);
	}
	
	private void addViews(IPageLayout factory) {
		IFolderLayout topLeft =
			factory.createFolder(
				"topLeft", //$NON-NLS-1$
				IPageLayout.LEFT,
				0.25f,
				factory.getEditorArea());
		topLeft.addView(CcfExplorerView.ID);

		IFolderLayout bottomLeft =
			factory.createFolder(
				"bottomLeft", //$NON-NLS-1$
				IPageLayout.BOTTOM,
				0.75f,
				"topLeft"); //$NON-NLS-1$
		bottomLeft.addView(IPageLayout.ID_PROP_SHEET);
		bottomLeft.addPlaceholder(IdentityMappingConsistencyCheckView.ID);
		
		IFolderLayout bottom =
			factory.createFolder(
				"bottomRight", //$NON-NLS-1$
				IPageLayout.BOTTOM,
				0.75f,
				factory.getEditorArea());
		bottom.addView(HospitalView.ID);
		bottom.addPlaceholder(IdentityMappingView.ID);
	}

}
