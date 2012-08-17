package com.collabnet.ccf.editors;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;

public class StringStorage extends PlatformObject implements IStorage {
	private String string;
	private String name;
	
	public StringStorage(String string, String name) {
		this.string = string;
		this.name = name;
	}

	public InputStream getContents() throws CoreException {
		 return new ByteArrayInputStream(string.getBytes());
	}

	public IPath getFullPath() {
		return null;
	}

	public String getName() {
		return name;
	}

	public boolean isReadOnly() {
		return true;
	}

}
