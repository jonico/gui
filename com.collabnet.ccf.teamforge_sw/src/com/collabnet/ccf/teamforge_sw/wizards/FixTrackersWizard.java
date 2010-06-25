package com.collabnet.ccf.teamforge_sw.wizards;

import com.collabnet.ccf.model.MappingGroup;

public class FixTrackersWizard extends AbstractMappingWizard {
	private FixTrackersWizardPage wizardPage;

	public FixTrackersWizard(MappingGroup mappingGroup) {
		super(mappingGroup);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("Fix TeamForge Trackers");
		wizardPage = new FixTrackersWizardPage();
		addPage(wizardPage);
	}

	@Override
	public boolean performFinish() {
		return wizardPage.fixProblems();
	}

}
