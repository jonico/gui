package com.collabnet.ccf;

import java.io.IOException;
import java.sql.Timestamp;

import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.FormEditor;

import com.collabnet.ccf.core.GenericArtifactParsingException;
import com.collabnet.ccf.editors.CcfEditorPage;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.MappingGroup;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.schemageneration.CCFSchemaAndXSLTFileGenerator;

@SuppressWarnings("unchecked")
public interface ICcfParticipant extends Comparable {
	public static final int SOURCE = 0;
	public static final int TARGET = 1;
	
	public void setId(String id);
	
	public void setName(String name);
	
	public void setType(String type);
	
	public void setRepositoryKind(String repositoryKind);
	
	public void setDescription(String description);
	
	public void setImage(Image image);
	
	public void setSequence(int sequence);
	
	public void setPropertiesFileName(String propertiesFile);
	
	public String getUrl(Landscape landscape, int systemNumber);
	
	public String getId();
	
	public String getName();
	
	public String getType();
	
	public String getRepositoryKind();
	
	public String getDescription();
	
	public Image getImage();
	
	public int getSequence();
	
	public String getPropertiesFileName();
	
	public IMappingSection getMappingSection(int systemNumber);
	
	public CcfEditorPage getEditorPage1(FormEditor formEditor, String title);
	
	public CcfEditorPage getEditorPage2(FormEditor formEditor, String title);
	
	public String getNewProjectMappingVersion();
	
	public boolean showResetDate();
	
	public boolean showResetVersion();
	
	public String getResetProjectMappingVersion(Timestamp timestamp);
	
	public String getEntityType(String repositoryId);
	
	public String getInitialMDFFileNameSegment(String repositoryId, boolean isSource);
	
	public String getDefaultJmxPort();
	
	public String getReaderMetricsName();
	
	public String getWriterMetricsName();
	
	public void extract(SynchronizationStatus status,
			CCFSchemaAndXSLTFileGenerator xmlFileGenerator,
			IProgressMonitor monitor) throws GenericArtifactParsingException,
			IOException, TransformerException;
	
	public MappingGroup[] getMappingGroups(ProjectMappings projectMappingsParent, SynchronizationStatus[] projectMappings);

	public int getSortPriority();
	
	public boolean allowAsSourceRepository(String repositoryId);
	
	public boolean allowAsTargetRepository(String repositoryId);
	
	public boolean enableFieldMappingEditing(String toType);
}
