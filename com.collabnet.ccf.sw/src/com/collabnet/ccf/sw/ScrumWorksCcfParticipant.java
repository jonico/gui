package com.collabnet.ccf.sw;

import java.io.IOException;
import java.sql.Timestamp;

import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.FormEditor;

import com.collabnet.ccf.CcfParticipant;
import com.collabnet.ccf.IMappingSection;
import com.collabnet.ccf.core.GenericArtifactParsingException;
import com.collabnet.ccf.editors.CcfEditorPage;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.schemageneration.CCFSchemaAndXSLTFileGenerator;

public class ScrumWorksCcfParticipant extends CcfParticipant {
	
	@Override
	public Image getImage() {
		return Activator.getImage(Activator.IMAGE_SW);
	}

	public void extract(SynchronizationStatus status,
			CCFSchemaAndXSLTFileGenerator xmlFileGenerator,
			IProgressMonitor monitor) throws GenericArtifactParsingException,
			IOException, TransformerException {

	}

	public String getDefaultJmxPort1() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDefaultJmxPort2() {
		// TODO Auto-generated method stub
		return null;
	}

	public CcfEditorPage getEditorPage1(FormEditor formEditor, String title) {
		// TODO Auto-generated method stub
		return null;
	}

	public CcfEditorPage getEditorPage2(FormEditor formEditor, String title) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInitialMDFFileNameSegment(String repositoryId,
			boolean isSource) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getJmxMonitor1Port() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getJmxMonitor2Port() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IMappingSection getMappingSection(int systemNumber) {
		IMappingSection mappingSection = new ScrumWorksMappingSection();
		mappingSection.setSystemNumber(systemNumber);
		return mappingSection;
	}

	public String getNewProjectMappingVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReaderMetricsName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getResetProjectMappingVersion(Timestamp timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getWriterMetricsName() {
		// TODO Auto-generated method stub
		return null;
	}

}
