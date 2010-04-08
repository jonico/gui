package com.collabnet.ccf.teamforge_sw;

import com.collabnet.ccf.IProjectMappingVisibilityChecker;
import com.collabnet.ccf.model.SynchronizationStatus;

public class ProjectMappingVisibilityChecker implements
		IProjectMappingVisibilityChecker {

	public boolean isProjectMappingVisible(SynchronizationStatus projectMapping) {
		if (projectMapping.getSourceSystemKind().startsWith("TF") && projectMapping.getTargetSystemKind().startsWith("SWP")) {
			if (projectMapping.getSourceRepositoryId().endsWith("-planningFolders")) {
				return false;
			}
		}
		return true;
	}

}
