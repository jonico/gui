package com.collabnet.ccf.editors;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.ide.FileStoreEditorInput;

public class ExternalFileEditorInput extends FileStoreEditorInput {
	private String name;

	public ExternalFileEditorInput(IFileStore fileStore, String name) {
		super(fileStore);
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExternalFileEditorInput)
			return ((ExternalFileEditorInput)obj).getName().equals(name);
		else
			return super.equals(obj);
	}

	@Override
	public String getName() {
		return name;
	}

}
