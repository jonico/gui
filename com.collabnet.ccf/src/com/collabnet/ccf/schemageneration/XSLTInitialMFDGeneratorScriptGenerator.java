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


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

import com.collabnet.ccf.core.CCFRuntimeException;

/**
 * This component will apply an XSLT script on an exiting 
 * MFD document to produce an XSLT script that can be used
 * as input for the XSLTInitialMFDGenerator
 * 
 * @author jnicolai
 * 
 */
public class XSLTInitialMFDGeneratorScriptGenerator {

	public final static String INITIAL_MFD_XSLT_FILE = "xslt/CreateCreateInitialMFD.xsl";

	private Transformer initialMFDFileTransformer = null;

	public XSLTInitialMFDGeneratorScriptGenerator() {
		initialMFDFileTransformer = loadXSLT(INITIAL_MFD_XSLT_FILE);
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
	 * Creates a new CreateInitialMFD.xsl file based on an already existing MFD file
	 * @param mfdTemplateFile path to mfdFile
	 * @return XML document containing CreateInitialMFD XSLT script
	 * @throws TransformerException
	 * @throws IOException 
	 * @throws DocumentException
	 */
	public Document generateCreateInitialMFDScript(String mfdTemplateFile)
			throws TransformerException, DocumentException, IOException {
		Document document = DocumentHelper.parseText(readFileAsString(mfdTemplateFile));
		return transform(initialMFDFileTransformer, document);
	}
	
	private static String readFileAsString(String filePath)
    throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            fileData.append(buf, 0, numRead);
        }
        reader.close();
        return fileData.toString();
    }


	/**
	 * Loads XSLT files and builds transformer
	 */
	private static Transformer loadXSLT(String xsltFile) {
		Transformer transform = null;
		InputStream inputStream = null;

		inputStream = XSLTInitialMFDGeneratorScriptGenerator.class.getResourceAsStream("/" + xsltFile);
		// load the transform
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			StreamSource streamSource = new StreamSource(inputStream);
			transform = factory.newTransformer(streamSource);

			// log.debug("Loaded XSLT [" + xsltFile + "] successfully");
		} catch (TransformerConfigurationException e) {
			throw new CCFRuntimeException("Could not load XSLT file " + xsltFile + ": "
					+ e.getMessage(), e);
		}
		return transform;
	}
}
