package com.collabnet.ccf.schemageneration;

import javax.xml.transform.TransformerException;

import org.dom4j.Document;

public interface CCFSchemaAndXSLTFileGenerator {

	/**
	 * Returns XML schema for repository specific layout
	 * 
	 * @param ga
	 *            generic artifact in XML format that describes repository
	 *            specific layout
	 * @return XML schema for repository specific layout
	 * @throws TransformerException
	 */
	public abstract Document getRepositorySpecificLayout(Document ga)
			throws TransformerException;

	/**
	 * Returns XSLT script that transforms from generic artifact to repository
	 * specific layout
	 * 
	 * @param ga
	 *            generic artifact in XML format that describes repository
	 *            specific layout
	 * @return XSLT script that transforms from generic artifact to repository
	 *         specific layout
	 * @throws TransformerException
	 */
	public abstract Document getGenericArtifactToRepositoryXSLTFile(Document ga)
			throws TransformerException;

	/**
	 * Returns XSLT script that transforms from repository specific layout to
	 * generic artifact layout
	 * 
	 * @param ga
	 *            generic artifact in XML format that describes repository
	 *            specific layout
	 * @return XSLT script that transforms repository specific layout to generic
	 *         artifact layout
	 * @throws TransformerException
	 */
	public abstract Document getRepositoryToGenericArtifactXSLTFile(Document ga)
			throws TransformerException;

}