package com.collabnet.ccf;

import javax.xml.transform.TransformerException;

import com.collabnet.ccf.schemageneration.XSLTInitialMFDGenerator;

public class TestEmptyMFDDocumentGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XSLTInitialMFDGenerator generator = new XSLTInitialMFDGenerator(".");
		try {
			System.out.println("Generated mdf file:\n"
					+ generator.generateInitialMFD("generatedCSFESchema.xsd",
							"generatedPTSchema.xsd").asXML());
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
