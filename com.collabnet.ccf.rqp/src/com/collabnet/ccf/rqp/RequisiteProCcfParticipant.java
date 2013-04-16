package com.collabnet.ccf.rqp;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.FormEditor;

import com.collabnet.ccf.CcfParticipant;
import com.collabnet.ccf.IConnectionTester;
import com.collabnet.ccf.IMappingSection;
import com.collabnet.ccf.core.GenericArtifactParsingException;
import com.collabnet.ccf.editors.CcfEditorPage;
import com.collabnet.ccf.editors.CcfSystemEditorPage;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.rqp.schemageneration.RQPLayoutExtractor;
import com.collabnet.ccf.schemageneration.CCFSchemaAndXSLTFileGenerator;

public class RequisiteProCcfParticipant extends CcfParticipant {
	
	public static final String CREATE_INITIAL_MFD_FILE_RQP_DEFECT = "RQPDefect"; //$NON-NLS-1$
	public static final String CREATE_INITIAL_MFD_FILE_RQP_REQUIREMENT = "RQPRequirement"; //$NON-NLS-1$

	public static final String PROPERTIES_RQP_URL = "rqp.system.1.url"; //$NON-NLS-1$
	public static final String PROPERTIES_RQP_USER = "rqp.system.1.username"; //$NON-NLS-1$
	public static final String PROPERTIES_RQP_PASSWORD = "rqp.system.1.password"; //$NON-NLS-1$
	public static final String PROPERTIES_RQP_RESYNC_USER = "rqp.system.1.resync.username"; //$NON-NLS-1$
	public static final String PROPERTIES_RQP_RESYNC_PASSWORD = "rqp.system.1.resync.password"; //$NON-NLS-1$
	public static final String PROPERTIES_RQP_ATTACHMENT_SIZE = "rqp.max.attachmentsize.per.artifact"; //$NON-NLS-1$
	
	public static final String RQPREADER_METRICS = "openadaptor:id=RQPReader-metrics";
	public static final String RQPWRITER_METRICS = "openadaptor:id=RQPWriter-metrics";
	
	public static final String DEFAULT_JMX_PORT = "8085";
	
	public IMappingSection getMappingSection(int systemNumber) {
		IMappingSection mappingSection = new RequisiteProMappingSection();
		mappingSection.setSystemNumber(systemNumber);
		return mappingSection;
	}

	public CcfEditorPage getEditorPage1(FormEditor formEditor, String title) {
		CcfSystemEditorPage editorPage = new CcfSystemEditorPage(formEditor, "rqp1", title, CcfSystemEditorPage.RQP);
		editorPage.setSystemNumber(1);
		return editorPage;
	}
	
	public CcfEditorPage getEditorPage2(FormEditor formEditor, String title) {
		CcfSystemEditorPage editorPage = new CcfSystemEditorPage(formEditor, "rqp2", title, CcfSystemEditorPage.RQP);
		editorPage.setSystemNumber(2);
		return editorPage;
	}

	public String getNewProjectMappingVersion() {
		return "0";
	}

	public String getResetProjectMappingVersion(Timestamp timestamp) {
		return "0";
	}

	public boolean showResetDate() {
		return false;
	}

	public boolean showResetVersion() {
		return true;
	}

	@Override
	public Image getImage() {
		return Activator.getImage(Activator.IMAGE_RQP);
	}

	public String getDefaultJmxPort() {
		return DEFAULT_JMX_PORT;
	}

	public String getReaderMetricsName() {
		return RQPREADER_METRICS;
	}

	public String getWriterMetricsName() {
		return RQPWRITER_METRICS;
	}

	public String getInitialMDFFileNameSegment(String repositoryId, boolean isSource) {
		String fileNameSegment = isSource ?
				(com.collabnet.ccf.Activator.CREATE_INITIAL_MFD_FILE_PREFIX + com.collabnet.ccf.Activator.CREATE_INITIAL_MFD_FILE_SEPARATOR) 
				: "";
		if (RQPLayoutExtractor.isDefectRepository(repositoryId)) {
			fileNameSegment += CREATE_INITIAL_MFD_FILE_RQP_DEFECT;
		} else {
			fileNameSegment += CREATE_INITIAL_MFD_FILE_RQP_REQUIREMENT;
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
		return properties.getProperty(PROPERTIES_RQP_URL);
	}
	
	public void extract(SynchronizationStatus status,
			CCFSchemaAndXSLTFileGenerator xmlFileGenerator,
			IProgressMonitor monitor) throws GenericArtifactParsingException,
			IOException, TransformerException {
		RQPLayoutExtractor rqpLayoutExtractor = new RQPLayoutExtractor();
		Properties properties;
		if (status.getLandscape().getType2().equals(getType())) {
			properties = status.getLandscape().getProperties2();
		} else {
			properties = status.getLandscape().getProperties1();
		}
		String url = properties.getProperty(PROPERTIES_RQP_URL, "");
		String user = properties.getProperty(PROPERTIES_RQP_USER, "");
		String password = com.collabnet.ccf.Activator.decodePassword(properties.getProperty(
				PROPERTIES_RQP_PASSWORD, ""));
		rqpLayoutExtractor.setServerUrl(url);
		rqpLayoutExtractor.setUserName(user);
		rqpLayoutExtractor.setPassword(password);
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

		outputSchemaAndXSLTFiles(rqpLayoutExtractor, repositoryId,
				xmlFileGenerator, artifactToSchemaFile, schemaToArtifactFile,
				repositorySchemaFile, isSourceSystem, monitor);
	}

	public String getEntityType(String repositoryId) {
		int index = repositoryId.indexOf("-");
		if (index != -1) {
			String project = repositoryId.substring(index + 1);
			index = project.indexOf("-");
			if (index != -1) {
				return project.substring(index + 1);
			}
		}
		return null;
	}
	
	@Override
	public IConnectionTester getConnectionTester() {
		return null;
	}
	
}
