package com.collabnet.ccf.wizards;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.dom4j.Document;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.editors.ExternalFileEditorInput;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.core.CCFRuntimeException;
import com.collabnet.ccf.schemageneration.CCFSchemaAndXSLTFileGenerator;
import com.collabnet.ccf.schemageneration.CCFXSLTSchemaAndXSLTFileGenerator;
import com.collabnet.ccf.core.GenericArtifact;
import com.collabnet.ccf.core.GenericArtifactHelper;
import com.collabnet.ccf.core.GenericArtifactParsingException;
import com.collabnet.ccf.schemageneration.PTLayoutExtractor;
import com.collabnet.ccf.schemageneration.QCLayoutExtractor;
import com.collabnet.ccf.schemageneration.RepositoryLayoutExtractor;
import com.collabnet.ccf.schemageneration.TFLayoutExtractor;
import com.collabnet.ccf.schemageneration.XSLTInitialMFDGenerator;

public class EditFieldMappingsWizard extends Wizard {
	private SynchronizationStatus projectMapping;

	private EditFieldMappingsWizardMainPage mainPage;
	
	private Exception mapForceException;
	private boolean canceled;
	

	public EditFieldMappingsWizard(SynchronizationStatus projectMapping) {
		super();
		this.projectMapping = projectMapping;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("Edit Field Mappings");
		mainPage = new EditFieldMappingsWizardMainPage("main", "Choices", Activator.getDefault().getImageDescriptor(Activator.IMAGE_EDIT_FIELD_MAPPINGS_WIZBAN));
		addPage(mainPage);
	}

	@Override
	public boolean performFinish() {
		if (mainPage.isEdit()) {
			return edit(false);
		}
		if (mainPage.isMapForceEdit()) {
			return editWithMapForce();
		}
		if (mainPage.isSwitchOnly()) {
			return switchOnly();
		}
		return true;
	}
	
	public boolean needsProgressMonitor() {
		return true;
	}
	
	public SynchronizationStatus getProjectMapping() {
		return projectMapping;
	}
	
	private String getMapForcePath() {
		return mainPage.getMapForcePath();
	}
	
	private boolean switchOnly() {
		String message = null;
		if (projectMapping.usesGraphicalMapping()) {
			projectMapping.switchToNonGraphicalMapping();
			message = "Switched to non-graphical mapping.";
		} else {
			projectMapping.switchToGraphicalMapping();
			message = "Switched to graphical mapping.";
		}
		CcfDataProvider dataProvider = new CcfDataProvider();
		try {
			dataProvider.setFieldMappingMode(projectMapping);
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "Edit Field Mappings", e.getMessage());
			Activator.handleError(e);
			return false;
		}
		MessageDialog.openInformation(getShell(), "Edit Field Mappings", message);
		return true;
	}
	
	public boolean editWithMapForce() {
		canceled = false;
		mapForceException = null;
		final String mapForcePath = getMapForcePath();
		final boolean generate = mainPage.isGenerate();
		final boolean switchToGraphical = mainPage.isSwitchToGraphicalMapping();
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if (switchToGraphical) {
					projectMapping.switchToGraphicalMapping();
					CcfDataProvider dataProvider = new CcfDataProvider();
					try {
						dataProvider.setFieldMappingMode(projectMapping);
					} catch (Exception e) {
						mapForceException = e;
						Activator.handleError(e);
						return;
					}
				}
				if (generate) {
					monitor.beginTask("Generate Files", 7);
					try {
						CCFSchemaAndXSLTFileGenerator xmlFileGenerator = new CCFXSLTSchemaAndXSLTFileGenerator(
								projectMapping.getXSLTFolder().getAbsolutePath());
						if (projectMapping.getLandscape().getType2().equals(
								Landscape.TYPE_TF)) {
							extractTF(projectMapping, xmlFileGenerator, monitor);
							if (monitor.isCanceled()) {
								canceled = true;
								return;
							}
						}
						if (projectMapping.getLandscape().getType2().equals(
								Landscape.TYPE_PT)) {
							extractPT(projectMapping, xmlFileGenerator, monitor);
							if (monitor.isCanceled()) {
								canceled = true;
								return;
							}
						}
						extractQC(projectMapping, xmlFileGenerator, monitor);
						if (monitor.isCanceled()) {
							canceled = true;
							return;
						}

						File mfdFile = projectMapping.getMappingFile(projectMapping
								.getMFDFileName());
						if (mfdFile != null && !mfdFile.exists()) {
							monitor.subTask("Generating MFD-File");
							XSLTInitialMFDGenerator mfdFileGenerator = new XSLTInitialMFDGenerator(
									projectMapping.getXSLTFolder()
											.getAbsolutePath());
							Document mfdDocument = mfdFileGenerator
									.generateInitialMFD(
											projectMapping
													.getSourceRepositorySchemaFileName(),
											projectMapping
													.getTargetRepositorySchemaFileName());
							writeFile(mfdFile, mfdDocument);
						}
						monitor.worked(1);

						if (monitor.isCanceled()) {
							canceled = true;
							return;	
						}
					} catch (Exception e) {
						mapForceException = e;
						Activator.handleError(e);
					}
				}	
				if (canceled || mapForceException != null) {
					return;
				}
				try {
					monitor.beginTask(
							"Use external application for mapping ...",
							4);
					monitor
							.subTask("Launch MapForce and wait until MapForce is closed ...");

					// start mapforce
					File mfdFile = projectMapping.getMappingFile(projectMapping
							.getMFDFileName());
					String interactiveMapforce[] = { mapForcePath,
							mfdFile.getCanonicalPath() };
					Process process = Runtime.getRuntime().exec(
							interactiveMapforce);
					process.waitFor();
					monitor.worked(1);

					if (monitor.isCanceled())
						return;

					monitor
							.subTask("Launch MapForce in background again to generate XSLT file ...");

					String scriptedMapforce[] = { mapForcePath,
							mfdFile.getCanonicalPath(), "/XSLT",
							mfdFile.getParentFile().getCanonicalPath() };
					process = Runtime.getRuntime().exec(
							scriptedMapforce);
					process.getOutputStream().close();
					process.getInputStream().close();
					process.getErrorStream().close();
					process.waitFor();
					monitor.worked(1);

					if (monitor.isCanceled())
						return;

					monitor.subTask("Rename generated XSLT file ...");
					File mfXSLFile = projectMapping.getMappingFile(projectMapping
							.getMFXslFilename());
					File graphicalXSLFile = projectMapping
							.getMappingFile(projectMapping
									.getGraphicalXslFileName());
					if (!mfXSLFile.exists()) {
						throw new CCFRuntimeException(
								"Could not find file "
										+ mfXSLFile.getCanonicalPath()
										+ " that should have been generated by MapForce.\nMaybe you have not designed a valid mapping?\nPlease try to at least connect the root nodes of your systems.");
					} else {
						if (graphicalXSLFile.exists()) {
							graphicalXSLFile.delete();
						}
						if (!mfXSLFile.renameTo(graphicalXSLFile) || !graphicalXSLFile.exists()) {
							throw new CCFRuntimeException(
									"Could not rename MapForce file "
											+ mfXSLFile
													.getCanonicalPath()
											+ " to CCF file "
											+ graphicalXSLFile
													.getCanonicalPath());
						}
					}
					monitor.worked(1);					
				} catch (Exception e) {
					mapForceException = e;
					Activator.handleError(e);					
				}
				monitor.done();
			}		
		};
		try {
			getContainer().run(true, true, runnable);
		} catch (Exception e) {
			mapForceException = e;
			Activator.handleError(e);
		}
		if (mapForceException != null) {
			String errorMessage = null;
			if (mapForceException instanceof InvocationTargetException) {
				errorMessage = mapForceException.getCause().getMessage();
			} else {
				if (mapForceException.getMessage() == null
						|| mapForceException.getMessage().trim().length() == 0) {
					errorMessage = mapForceException.toString();
				} else {
					errorMessage = mapForceException.getMessage();
				}
			}
			MessageDialog.openError(Display.getDefault()
					.getActiveShell(), "Edit Field Mappings",
					errorMessage);
		}
		return !canceled && mapForceException == null;
	}
	
	public boolean edit(boolean prompt) {
		if (mainPage != null && mainPage.isSwitchToNonGraphicalMapping()) {
			projectMapping.switchToNonGraphicalMapping();
			CcfDataProvider dataProvider = new CcfDataProvider();
			try {
				dataProvider.setFieldMappingMode(projectMapping);
			} catch (Exception e) {
				Activator.handleError(e);
				return false;
			}
		}
		File xslFile = projectMapping.getXslFile();
		if (!xslFile.exists()) {
			if (prompt && !MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Edit Field Mappings", "File " + xslFile.getName() + " does not exist.\n\nDo you wish to create it by copying sample.xsl?")) {
				return false;
			}
			try {
				xslFile.createNewFile();
				File sampleFile = projectMapping.getSampleXslFile();
				if (sampleFile != null && sampleFile.exists()) {
					CcfDataProvider.copyFile(sampleFile, xslFile);
				}
			} catch (IOException e) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Edit Field Mappings", "Unable to create file " + xslFile.getName() + ":\n\n" + e.getMessage());
				Activator.handleError(e);
				return false;
			}
		}
		IFileStore fileStore =  EFS.getLocalFileSystem().getStore(new Path(xslFile.getAbsolutePath()));
		final IEditorInput input = new ExternalFileEditorInput(fileStore, xslFile.getName());
		IEditorRegistry registry = Activator.getDefault().getWorkbench().getEditorRegistry();
		IEditorDescriptor descriptor = registry.getDefaultEditor(xslFile.getName());
		String id;
		if (descriptor == null) {
			id = "org.eclipse.ui.DefaultTextEditor"; //$NON-NLS-1$
		} else {
			id = descriptor.getId();
		}
		IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			page.openEditor(input, id);
		} catch (PartInitException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Edit Field Mappings", "Unable to open editor:\n\n" + e.getMessage());
			Activator.handleError(e);
			return false;
		}
		return true;
	}
	
	private void extractQC(SynchronizationStatus status,
			CCFSchemaAndXSLTFileGenerator xmlFileGenerator,
			IProgressMonitor monitor) throws GenericArtifactParsingException,
			IOException, TransformerException {
		QCLayoutExtractor qcLayoutExtractor = new QCLayoutExtractor();
		Properties properties = status.getLandscape().getProperties1();
		String url = properties.getProperty(Activator.PROPERTIES_QC_URL, "");
		String user = properties.getProperty(Activator.PROPERTIES_QC_USER, "");
		String password = properties.getProperty(
				Activator.PROPERTIES_QC_PASSWORD, "");
		qcLayoutExtractor.setServerUrl(url);
		qcLayoutExtractor.setUserName(user);
		qcLayoutExtractor.setPassword(password);
		String repositoryId = null;

		File artifactToSchemaFile = null;
		File schemaToArtifactFile = null;
		File repositorySchemaFile = null;

		boolean isSourceSystem = false;
		if (status.getSourceSystemKind().startsWith(Landscape.TYPE_QC)) {
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

		outputSchemaAndXSLTFiles(qcLayoutExtractor, repositoryId,
				xmlFileGenerator, artifactToSchemaFile, schemaToArtifactFile,
				repositorySchemaFile, isSourceSystem, monitor);
	}

	private void extractTF(SynchronizationStatus status,
			CCFSchemaAndXSLTFileGenerator xmlFileGenerator,
			IProgressMonitor monitor) throws GenericArtifactParsingException,
			IOException, TransformerException {
		TFLayoutExtractor tfLayoutExtractor = new TFLayoutExtractor();
		Properties properties = status.getLandscape().getProperties2();
		String url = properties.getProperty(Activator.PROPERTIES_SFEE_URL, "");
		String user = properties
				.getProperty(Activator.PROPERTIES_SFEE_USER, "");
		String password = properties.getProperty(
				Activator.PROPERTIES_SFEE_PASSWORD, "");
		tfLayoutExtractor.setServerUrl(url);
		tfLayoutExtractor.setUsername(user);
		tfLayoutExtractor.setPassword(password);
		String repositoryId = null;

		File artifactToSchemaFile = null;
		File schemaToArtifactFile = null;
		File repositorySchemaFile = null;

		boolean isSourceSystem = false;
		if (status.getSourceSystemKind().startsWith(Landscape.TYPE_TF)) {
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

	private void extractPT(SynchronizationStatus status,
			CCFSchemaAndXSLTFileGenerator xmlFileGenerator,
			IProgressMonitor monitor) throws GenericArtifactParsingException,
			IOException, TransformerException {
		PTLayoutExtractor ptLayoutExtractor = new PTLayoutExtractor();
		Properties properties = status.getLandscape().getProperties2();
		String url = properties.getProperty(Activator.PROPERTIES_CEE_URL, "");
		String user = properties.getProperty(Activator.PROPERTIES_CEE_USER, "");
		String password = properties.getProperty(
				Activator.PROPERTIES_CEE_PASSWORD, "");
		ptLayoutExtractor.setServerUrl(url);
		ptLayoutExtractor.setUsername(user);
		ptLayoutExtractor.setPassword(password);
		String repositoryId = null;

		File artifactToSchemaFile = null;
		File schemaToArtifactFile = null;
		File repositorySchemaFile = null;

		boolean isSourceSystem = false;
		if (status.getSourceSystemKind().startsWith(Landscape.TYPE_PT)) {
			isSourceSystem = true;
			repositoryId = status.getSourceRepositoryId();
			artifactToSchemaFile = status.getMappingFile(status
					.getGenericArtifactToSourceRepositorySchemaFileName());
			// schemaToArtifactFile = status.getMappingFile(status.
			// getSourceRepositorySchemaToGenericArtifactFileName());
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
		outputSchemaAndXSLTFiles(ptLayoutExtractor, repositoryId,
				xmlFileGenerator, artifactToSchemaFile, schemaToArtifactFile,
				repositorySchemaFile, isSourceSystem, monitor);
	}

	private static void writeFile(File file, Document content)
			throws IOException {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			out.write(content.asXML());
			/*XMLWriter writer = new XMLWriter(out, format);
			writer.write(content);
			writer.close();*/
		} catch (IOException e) {
			throw e;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private static void outputSchemaAndXSLTFiles(
			RepositoryLayoutExtractor extractor, String repositoryId,
			CCFSchemaAndXSLTFileGenerator generator, File artifactToSchemaFile,
			File schemaToArtifactFile, File repositorySchemaFile,
			boolean isSourceSystem, IProgressMonitor monitor)
			throws GenericArtifactParsingException, IOException,
			TransformerException {

		monitor.subTask("Creating generic artifact XML document for "
				+ repositorySchemaFile.getName());

		GenericArtifact genericArtifact = extractor
				.getRepositoryLayout(repositoryId);
		Document genericArtifactDocument = GenericArtifactHelper
				.createGenericArtifactXMLDocument(genericArtifact);

		monitor.worked(1);
		if (monitor.isCanceled()) 
			return;

		monitor.subTask("Generating " + repositorySchemaFile.getName());

		writeFile(repositorySchemaFile, generator
				.getRepositorySpecificLayout(genericArtifactDocument));

		monitor.worked(1);
		if (monitor.isCanceled())
			return;

		if (isSourceSystem) {
			monitor.subTask("Generating " + artifactToSchemaFile.getName());

			writeFile(
					artifactToSchemaFile,
					generator
							.getGenericArtifactToRepositoryXSLTFile(genericArtifactDocument));
			monitor.worked(1);
		}

		if (monitor.isCanceled())
			return;

		if (!isSourceSystem) {
			monitor.subTask("Generating " + schemaToArtifactFile.getName());

			writeFile(
					schemaToArtifactFile,
					generator
							.getRepositoryToGenericArtifactXSLTFile(genericArtifactDocument));
			monitor.worked(1);
		}

		if (monitor.isCanceled())
			return;
	}

}
