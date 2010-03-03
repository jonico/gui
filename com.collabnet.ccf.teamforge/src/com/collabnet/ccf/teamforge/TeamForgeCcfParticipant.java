package com.collabnet.ccf.teamforge;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.FormEditor;

import com.collabnet.ccf.CcfParticipant;
import com.collabnet.ccf.IMappingSection;
import com.collabnet.ccf.core.GenericArtifactParsingException;
import com.collabnet.ccf.editors.CcfEditorPage;
import com.collabnet.ccf.editors.CcfSystemEditorPage;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.schemageneration.CCFSchemaAndXSLTFileGenerator;
import com.collabnet.ccf.teamforge.schemageneration.TFLayoutExtractor;

public class TeamForgeCcfParticipant extends CcfParticipant {
	public static final String CREATE_INITIAL_MFD_FILE_TF_PF = "TFPlanningFolder"; //$NON-NLS-1$
	public static final String CREATE_INITIAL_MFD_FILE_TF_TRACKER_ITEM = "TFTrackerItem"; //$NON-NLS-1$

	public static final String PROPERTIES_SFEE_URL = "sfee.server.1.url"; //$NON-NLS-1$
	public static final String PROPERTIES_SFEE_USER = "sfee.server.1.username"; //$NON-NLS-1$
	public static final String PROPERTIES_SFEE_PASSWORD = "sfee.server.1.password"; //$NON-NLS-1$
	public static final String PROPERTIES_SFEE_RESYNC_USER = "sfee.server.1.resync.username"; //$NON-NLS-1$
	public static final String PROPERTIES_SFEE_RESYNC_PASSWORD = "sfee.server.1.resync.password"; //$NON-NLS-1$
	public static final String PROPERTIES_SFEE_ATTACHMENT_SIZE = "sfee.max.attachmentsize.per.artifact"; //$NON-NLS-1$
	
	public static final String TFREADER_METRICS = "openadaptor:id=TFReader-metrics";
	public static final String TFWRITER_METRICS = "openadaptor:id=TFWriter-metrics";
	
	public static final String DEFAULT_JMX_PORT_1 = "8085";
	public static final String DEFAULT_JMX_PORT_2 = "8084";
	
	public IMappingSection getMappingSection(int systemNumber) {
		IMappingSection mappingSection = new TeamForgeMappingSection();
		mappingSection.setSystemNumber(systemNumber);
		return mappingSection;
	}

	public CcfEditorPage getEditorPage1(FormEditor formEditor, String title) {
		CcfSystemEditorPage editorPage = new CcfSystemEditorPage(formEditor, "sfee1", title, CcfSystemEditorPage.TF);
		editorPage.setSystemNumber(1);
		return editorPage;
	}
	
	public CcfEditorPage getEditorPage2(FormEditor formEditor, String title) {
		CcfSystemEditorPage editorPage = new CcfSystemEditorPage(formEditor, "sfee2", title, CcfSystemEditorPage.TF);
		editorPage.setSystemNumber(2);
		return editorPage;
	}

	public String getNewProjectMappingVersion() {
		return "0";
	}

	public String getResetProjectMappingVersion(Timestamp timestamp) {
		return "0";
	}
	
	@Override
	public Image getImage() {
		return Activator.getImage(Activator.IMAGE_TEAM_FORGE);
	}

	public int getJmxMonitor1Port() {
		return 10001;
	}

	public int getJmxMonitor2Port() {
		return 10002;
	}

	public String getDefaultJmxPort1() {
		return DEFAULT_JMX_PORT_1;
	}

	public String getDefaultJmxPort2() {
		return DEFAULT_JMX_PORT_2;
	}

	public String getReaderMetricsName() {
		return TFREADER_METRICS;
	}

	public String getWriterMetricsName() {
		return TFWRITER_METRICS;
	}

	public String getInitialMDFFileNameSegment(String repositoryId, boolean isSource) {
		String fileNameSegment;
		if (TFLayoutExtractor.isTrackerRepository(repositoryId)) {
			fileNameSegment = com.collabnet.ccf.Activator.CREATE_INITIAL_MFD_FILE_PREFIX
			+ com.collabnet.ccf.Activator.CREATE_INITIAL_MFD_FILE_SEPARATOR
			+ CREATE_INITIAL_MFD_FILE_TF_TRACKER_ITEM;
		} else {
			fileNameSegment = com.collabnet.ccf.Activator.CREATE_INITIAL_MFD_FILE_PREFIX
			+ com.collabnet.ccf.Activator.CREATE_INITIAL_MFD_FILE_SEPARATOR
			+ CREATE_INITIAL_MFD_FILE_TF_PF;
		}
		if (isSource) {
			fileNameSegment = fileNameSegment + com.collabnet.ccf.Activator.CREATE_INITIAL_MFD_FILE_SEPARATOR;
		}
		return fileNameSegment;
	}
	
	public String getUrl(Landscape landscape, int systemNumber) {
		Properties properties;
		if (systemNumber == 1) {
			properties = landscape.getProperties1();
		} else {
			properties = landscape.getProperties2();
		}
		return properties.getProperty(PROPERTIES_SFEE_URL);
	}
	
	public void extract(SynchronizationStatus status,
			CCFSchemaAndXSLTFileGenerator xmlFileGenerator,
			IProgressMonitor monitor) throws GenericArtifactParsingException,
			IOException, TransformerException {
		Properties properties = status.getLandscape().getProperties2();
		String url = properties.getProperty(PROPERTIES_SFEE_URL, "");
		String user = properties
				.getProperty(PROPERTIES_SFEE_USER, "");
		String password = properties.getProperty(
				PROPERTIES_SFEE_PASSWORD, "");
		TFLayoutExtractor tfLayoutExtractor = new TFLayoutExtractor(url, user, password);
		String repositoryId = null;

		File artifactToSchemaFile = null;
		File schemaToArtifactFile = null;
		File repositorySchemaFile = null;

		boolean isSourceSystem = false;
		if (status.getSourceSystemKind().startsWith(getType())) {
			isSourceSystem = true;
			repositoryId = status.getSourceRepositoryId();
			artifactToSchemaFile = status.getMappingFile(status
					.getGenericArtifactToSourceRepositorySchemaFileName());
			schemaToArtifactFile = status.getMappingFile(status
					.getSourceRepositorySchemaToGenericArtifactFileName());
			repositorySchemaFile = status.getMappingFile(status
					.getSourceRepositorySchemaFileName());
		} else {
			repositoryId = status.getTargetRepositoryId();
			artifactToSchemaFile = status.getMappingFile(status
					.getGenericArtifactToTargetRepositorySchemaFileName());
			schemaToArtifactFile = status.getMappingFile(status
					.getTargetRepositorySchemaToGenericArtifactFileName());
			repositorySchemaFile = status.getMappingFile(status
					.getTargetRepositorySchemaFileName());
		}
		outputSchemaAndXSLTFiles(tfLayoutExtractor, repositoryId,
				xmlFileGenerator, artifactToSchemaFile, schemaToArtifactFile,
				repositorySchemaFile, isSourceSystem, monitor);
	}
	
}