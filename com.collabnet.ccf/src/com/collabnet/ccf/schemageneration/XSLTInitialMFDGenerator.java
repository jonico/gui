/*
 * Copyright 2009 CollabNet, Inc. ("CollabNet")
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/

package com.collabnet.ccf.schemageneration;


import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

import com.collabnet.ccf.core.CCFRuntimeException;

/**
 * This component will apply an XSLT script to generate a MFD document
 * with intitial mappings inside
 * 
 * @author jnicolai
 * 
 */
public class XSLTInitialMFDGenerator {

	public final static String INITIAL_MFD_XSLT_FILE = "CreateInitialMFD.xsl";

	private Transformer initialMFDFileTransformer = null;

	public XSLTInitialMFDGenerator(String xsltDirectory) {
		initialMFDFileTransformer = loadXSLT(xsltDirectory+"/"+INITIAL_MFD_XSLT_FILE);
	}

	/**
	 * Applies the transform to the Dom4J document
	 * 
	 * @param transformer
	 * @param d
	 *            the document to transform
	 * 
	 * @return an array containing a single XML string representing the
	 *         transformed document
	 * @throws TransformerException
	 *             thrown if an XSLT runtime error happens during transformation
	 */
	private static Document transform(Transformer transformer, Document d)
			throws TransformerException {
		DocumentSource source = new DocumentSource(d);
		DocumentResult result = new DocumentResult();
		// TODO: Allow the user to specify stylesheet parameters?
		transformer.transform(source, result);
		return result.getDocument();
	}

	/**
	 * Generates an intial MFD document that can be used for the graphical data mapping
	 * @param sourceSchemaName file name of the source schema
	 * @param targetSchemaName file name of the target schema
	 * @return
	 * @throws TransformerException
	 */
	public Document generateInitialMFD(String sourceSchemaName, String targetSchemaName)
			throws TransformerException {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		Element rootElement = document.addElement("CreateInitialMFDDocument");
		rootElement.addAttribute("sourceSchemaName", sourceSchemaName);
		rootElement.addAttribute("targetSchemaName", targetSchemaName);
		return transform(initialMFDFileTransformer, document);
	}

	/**
	 * Loads XSLT files and builds transformer
	 */
	private static Transformer loadXSLT(String xsltFile) {
		Transformer transform = null;
		// InputStream inputStream = null;

		//inputStream = XSLTEmptyMFDDocumentGenerator.class.getResourceAsStream("/" + xsltFile);
		// load the transform
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			//StreamSource streamSource = new StreamSource(inputStream);
			StreamSource streamSource = new StreamSource(xsltFile);
			transform = factory.newTransformer(streamSource);

			// log.debug("Loaded XSLT [" + xsltFile + "] successfully");
		} catch (TransformerConfigurationException e) {
			throw new CCFRuntimeException("Could not load XSLT file " + xsltFile + ": "
					+ e.getMessage(), e);
		}
		return transform;
	}
}
