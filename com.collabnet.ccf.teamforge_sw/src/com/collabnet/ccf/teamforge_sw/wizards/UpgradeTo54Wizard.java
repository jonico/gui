package com.collabnet.ccf.teamforge_sw.wizards;

import org.eclipse.jface.wizard.Wizard;

import com.collabnet.ccf.model.Landscape;

public class UpgradeTo54Wizard extends Wizard {
	private Landscape landscape;
	private UpgradeTo54WizardPage upgradePage;

	public UpgradeTo54Wizard(Landscape landscape) {
		this.landscape = landscape;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("Upgrade TeamForge Trackers to 5.4 Layout");
		upgradePage = new UpgradeTo54WizardPage(landscape);
		addPage(upgradePage);
	}
	
	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	@Override
	public boolean performFinish() {
		return upgradePage.upgradeTo54();
	}

}
