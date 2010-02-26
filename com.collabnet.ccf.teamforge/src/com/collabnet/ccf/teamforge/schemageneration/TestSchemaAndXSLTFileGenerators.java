package com.collabnet.ccf.teamforge.schemageneration;

import javax.xml.transform.TransformerException;

import org.dom4j.Document;

import com.collabnet.ccf.core.GenericArtifact;
import com.collabnet.ccf.core.GenericArtifactHelper;
import com.collabnet.ccf.core.GenericArtifactParsingException;
import com.collabnet.ccf.schemageneration.CCFSchemaAndXSLTFileGenerator;
import com.collabnet.ccf.schemageneration.CCFXSLTSchemaAndXSLTFileGenerator;
import com.collabnet.ccf.schemageneration.RepositoryLayoutExtractor;

public class TestSchemaAndXSLTFileGenerators {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TFLayoutExtractor tfLayoutExtractor = new TFLayoutExtractor("http://cu085.cubit.sp.collab.net", "CCFConnectorUser", "CCF11");
		CCFSchemaAndXSLTFileGenerator generator = new CCFXSLTSchemaAndXSLTFileGenerator(".");
		outputSchemaAndXSLTFiles(tfLayoutExtractor, "tracker1008", generator);
	}

	private static void outputSchemaAndXSLTFiles(
			RepositoryLayoutExtractor extractor, String repositoryId,
			CCFSchemaAndXSLTFileGenerator generator) {
		try {
			System.out
					.println("Generating schema and XSLT files for repository id "
							+ repositoryId + " ...");
			GenericArtifact genericArtifact = extractor
					.getRepositoryLayout(repositoryId);
			Document genericArtifactDocument = GenericArtifactHelper
					.createGenericArtifactXMLDocument(genericArtifact);
			System.out
					.println("Generic artifact describing the repository specific layout:\n"
							+ genericArtifactDocument.asXML());

			System.out.println();
			System.out.println();
			System.out.println();

			System.out
					.println("XML Schema describing the repository specific layout:\n"
							+ generator.getRepositorySpecificLayout(
									genericArtifactDocument).asXML());
			System.out.println();
			System.out.println();
			System.out.println();

			System.out
					.println("XSLT file to transform from generic artifact format to repository specific format:\n"
							+ generator.getGenericArtifactToRepositoryXSLTFile(
									genericArtifactDocument).asXML());

			System.out.println();
			System.out.println();
			System.out.println();

			System.out
					.println("XSLT file to transform from repository specific format to generic artifact format:\n"
							+ generator.getRepositoryToGenericArtifactXSLTFile(
									genericArtifactDocument).asXML());

			System.out.println();
		} catch (GenericArtifactParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
