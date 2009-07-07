package com.collabnet.ccf.schemageneration;

import com.collabnet.ccf.core.GenericArtifact;

public interface RepositoryLayoutExtractor {

	public abstract GenericArtifact getRepositoryLayout(String repositoryId);

}