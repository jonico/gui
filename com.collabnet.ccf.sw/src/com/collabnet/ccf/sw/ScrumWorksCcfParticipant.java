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
import com.collabnet.ccf.editors.CcfSystemEditorPage;
import com.collabnet.ccf.model.Landscape;
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

	public String getUrl(Landscape landscape, int systemNumber) {
		// TODO Auto-generated method stub
		return null;
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
		CcfSystemEditorPage editorPage = new CcfSystemEditorPage(formEditor, "sw1", title, CcfSystemEditorPage.SW);
		editorPage.setSystemNumber(1);
		return editorPage;
	}

	public CcfEditorPage getEditorPage2(FormEditor formEditor, String title) {
		CcfSystemEditorPage editorPage = new CcfSystemEditorPage(formEditor, "sw2", title, CcfSystemEditorPage.SW);
		editorPage.setSystemNumber(2);
		return editorPage;
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
