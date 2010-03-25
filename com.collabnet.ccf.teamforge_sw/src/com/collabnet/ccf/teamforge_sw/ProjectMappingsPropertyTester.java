package com.collabnet.ccf.teamforge_sw;

import org.eclipse.core.expressions.PropertyTester;

import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.ProjectMappings;

public class ProjectMappingsPropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof ProjectMappings) {
			ProjectMappings projectMappings = (ProjectMappings)receiver;
			Landscape landscape = projectMappings.getLandscape();
			if (landscape != null) {
				if (!landscape.getType1().equals(landscape.getType2())) {
					if (landscape.getType1().equals("TF") || landscape.getType1().equals("SWP")) {
						if (landscape.getType2().equals("TF") || landscape.getType2().equals("SWP")) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
