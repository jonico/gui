package com.collabnet.ccf.sw;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import com.collabnet.ccf.model.AdministratorMappingGroup;
import com.collabnet.ccf.model.AdministratorProjectMappings;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.MappingGroup;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.schemageneration.CCFSchemaAndXSLTFileGenerator;

public class ScrumWorksCcfParticipant extends CcfParticipant {
	public static final String DEFAULT_JMX_PORT = "8086";
	public static final String TYPE = "SWP";
	public static final String SWPREADER_METRICS = "openadaptor:id=SWPReader-metrics";
	public static final String SWPWRITER_METRICS = "openadaptor:id=SWPWriter-metrics";
	
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
		Properties properties;
		if (systemNumber == 1) {
			properties = landscape.getProperties1();
		} else {
			properties = landscape.getProperties2();
		}
		return properties.getProperty(com.collabnet.ccf.Activator.PROPERTIES_SW_URL);
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

	public String getDefaultJmxPort() {
		return DEFAULT_JMX_PORT;
	}

	public IMappingSection getMappingSection(int systemNumber) {
		IMappingSection mappingSection = new ScrumWorksMappingSection();
		mappingSection.setSystemNumber(systemNumber);
		return mappingSection;
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

	public String getReaderMetricsName() {
		return SWPREADER_METRICS;
	}

	public String getWriterMetricsName() {
		return SWPWRITER_METRICS;
	}

	public String getEntityType(String repositoryId) {
		return SWPMetaData.retrieveSWPTypeFromRepositoryId(repositoryId).toString();
	}

	@Override
	public int getSortPriority() {
		return 4;
	}

	@Override
	public boolean allowAsTargetRepository(String repositoryId) {
		if (repositoryId.endsWith("-Theme")) {
			return false;
		}
		return super.allowAsTargetRepository(repositoryId);
	}

	@Override
	public MappingGroup[] getMappingGroups(ProjectMappings projectMappingsParent, SynchronizationStatus[] projectMappings) {
		List<String> products = new ArrayList<String>();
		for (SynchronizationStatus projectMapping : projectMappings) {
			String product = getProduct(projectMapping);
			if (product != null && !products.contains(product)) {
				products.add(product);
			}
		}
		String[] productArray = new String[products.size()];
		products.toArray(productArray);
		Arrays.sort(productArray);
		List<MappingGroup> mappingGroups = new ArrayList<MappingGroup>();
		for (String product : productArray) {
			MappingGroup productsGroup;
			MappingGroup pbiGroup;
			MappingGroup taskGroup;
			MappingGroup productGroup;
			MappingGroup releaseGroup;
			MappingGroup themeGroup;
			if (projectMappingsParent instanceof AdministratorProjectMappings) {
				productsGroup = new AdministratorMappingGroup(this, projectMappingsParent, product, product, Activator.getImage(Activator.IMAGE_SWP_PRODUCT));
				pbiGroup = new AdministratorMappingGroup(this, projectMappingsParent, product + "-" + SWPMetaData.PBI.toString(), SWPMetaData.PBI.toString(), Activator.getImage(Activator.IMAGE_PBI));
				taskGroup = new AdministratorMappingGroup(this, projectMappingsParent, product + "-" + SWPMetaData.TASK.toString(), SWPMetaData.TASK.toString(), Activator.getImage(Activator.IMAGE_TASK));
				productGroup = new AdministratorMappingGroup(this, projectMappingsParent, product + "-" + SWPMetaData.PRODUCT.toString(), SWPMetaData.PRODUCT.toString(), Activator.getImage(Activator.IMAGE_PRODUCT));
				releaseGroup = new AdministratorMappingGroup(this, projectMappingsParent, product + "-" + SWPMetaData.RELEASE.toString(), SWPMetaData.RELEASE.toString(), Activator.getImage(Activator.IMAGE_RELEASE));		
				themeGroup = new AdministratorMappingGroup(this, projectMappingsParent, product + "-" + SWPMetaData.THEME.toString(), SWPMetaData.THEME.toString(), Activator.getImage(Activator.IMAGE_THEME));				
			} else {
				productsGroup = new MappingGroup(this, projectMappingsParent, product, product, Activator.getImage(Activator.IMAGE_SWP_PRODUCT));
				pbiGroup = new MappingGroup(this, projectMappingsParent, product + "-" + SWPMetaData.PBI.toString(), SWPMetaData.PBI.toString(), Activator.getImage(Activator.IMAGE_PBI));
				taskGroup = new MappingGroup(this, projectMappingsParent, product + "-" + SWPMetaData.TASK.toString(), SWPMetaData.TASK.toString(), Activator.getImage(Activator.IMAGE_TASK));
				productGroup = new MappingGroup(this, projectMappingsParent, product + "-" + SWPMetaData.PRODUCT.toString(), SWPMetaData.PRODUCT.toString(), Activator.getImage(Activator.IMAGE_PRODUCT));
				releaseGroup = new MappingGroup(this, projectMappingsParent, product + "-" + SWPMetaData.RELEASE.toString(), SWPMetaData.RELEASE.toString(), Activator.getImage(Activator.IMAGE_RELEASE));
				themeGroup = new MappingGroup(this, projectMappingsParent, product + "-" + SWPMetaData.THEME.toString(), SWPMetaData.THEME.toString(), Activator.getImage(Activator.IMAGE_THEME));
			}
			setChildMappings(product, pbiGroup, taskGroup, productGroup, releaseGroup, themeGroup, projectMappings);
			MappingGroup[] subGroups = { pbiGroup, taskGroup, productGroup, releaseGroup, themeGroup };
			productsGroup.setChildGroups(subGroups);
			mappingGroups.add(productsGroup);
		}
		MappingGroup[] mappingGroupArray = new MappingGroup[mappingGroups.size()];
		mappingGroups.toArray(mappingGroupArray);
		return mappingGroupArray;
	}
	
	private void setChildMappings(String product, MappingGroup pbiGroup, MappingGroup taskGroup, MappingGroup productGroup, MappingGroup releaseGroup, MappingGroup themeGroup, SynchronizationStatus[] projectMappings) {
		List<SynchronizationStatus> pbiMappings = new ArrayList<SynchronizationStatus>();
		List<SynchronizationStatus> taskMappings = new ArrayList<SynchronizationStatus>();
		List<SynchronizationStatus> productMappings = new ArrayList<SynchronizationStatus>();
		List<SynchronizationStatus> releaseMappings = new ArrayList<SynchronizationStatus>();
		List<SynchronizationStatus> themeMappings = new ArrayList<SynchronizationStatus>();
		for (SynchronizationStatus projectMapping : projectMappings) {
			if (getProduct(projectMapping).equals(product)) {
				if (projectMapping.getSourceRepositoryId().endsWith("-PBI") || projectMapping.getTargetRepositoryId().endsWith("-PBI")) {
					pbiMappings.add(projectMapping);
				}
				else if (projectMapping.getSourceRepositoryId().endsWith("-Task") || projectMapping.getTargetRepositoryId().endsWith("-Task")) {
					taskMappings.add(projectMapping);
				}	
				else if (projectMapping.getSourceRepositoryId().endsWith("-Product") || projectMapping.getTargetRepositoryId().endsWith("-Product")) {
					productMappings.add(projectMapping);
				}
				else if (projectMapping.getSourceRepositoryId().endsWith("-Release") || projectMapping.getTargetRepositoryId().endsWith("-Release")) {
					releaseMappings.add(projectMapping);
				}
				else if (projectMapping.getSourceRepositoryId().endsWith("-Theme") || projectMapping.getTargetRepositoryId().endsWith("-Theme")) {
					themeMappings.add(projectMapping);
				}	
			}
		}
		SynchronizationStatus[] pbiMappingArray = new SynchronizationStatus[pbiMappings.size()];
		pbiMappings.toArray(pbiMappingArray);
		pbiGroup.setChildMappings(pbiMappingArray);
		SynchronizationStatus[] taskMappingArray = new SynchronizationStatus[taskMappings.size()];
		taskMappings.toArray(taskMappingArray);
		taskGroup.setChildMappings(taskMappingArray);
		SynchronizationStatus[] productMappingArray = new SynchronizationStatus[productMappings.size()];
		productMappings.toArray(productMappingArray);
		productGroup.setChildMappings(productMappingArray);
		SynchronizationStatus[] releaseMappingArray = new SynchronizationStatus[releaseMappings.size()];
		releaseMappings.toArray(releaseMappingArray);
		releaseGroup.setChildMappings(releaseMappingArray);
		SynchronizationStatus[] themeMappingArray = new SynchronizationStatus[themeMappings.size()];
		themeMappings.toArray(themeMappingArray);
		themeGroup.setChildMappings(themeMappingArray);
	}
	
	private String getProduct(SynchronizationStatus projectMapping) {
		String repositoryId = null;
		if (projectMapping.getSourceRepositoryId().endsWith("-PBI") ||
		    projectMapping.getSourceRepositoryId().endsWith("-Task") ||
	        projectMapping.getSourceRepositoryId().endsWith("-Product") ||
	        projectMapping.getSourceRepositoryId().endsWith("-Theme") ||
	        projectMapping.getSourceRepositoryId().endsWith("-Release")) {
			repositoryId = projectMapping.getSourceRepositoryId();
		}
		else if (projectMapping.getTargetRepositoryId().endsWith("-PBI") ||
			    projectMapping.getTargetRepositoryId().endsWith("-Task") ||
		        projectMapping.getTargetRepositoryId().endsWith("-Product") ||
		        projectMapping.getTargetRepositoryId().endsWith("-Theme") ||
		        projectMapping.getTargetRepositoryId().endsWith("-Release")) {
				repositoryId = projectMapping.getTargetRepositoryId();
		}
		if (repositoryId != null) {
			String product = repositoryId.substring(0, repositoryId.lastIndexOf("-"));
			return product;
		}
		return null;
	}

}
