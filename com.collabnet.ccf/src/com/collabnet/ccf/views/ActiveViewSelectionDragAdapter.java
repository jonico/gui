package com.collabnet.ccf.views;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;

public class ActiveViewSelectionDragAdapter extends DragSourceAdapter implements TransferDragSourceListener {
	private final ISelectionProvider fProvider;

	public ActiveViewSelectionDragAdapter(ISelectionProvider provider) {
		assert provider != null;
		fProvider = provider;
	}

	public Transfer getTransfer() {
		return LocalSelectionTransfer.getTransfer();
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		ISelection selection = fProvider.getSelection();
		LocalSelectionTransfer.getTransfer().setSelection(selection);
		LocalSelectionTransfer.getTransfer().setSelectionSetTime(event.time & 0xFFFFFFFFL);
		event.doit = isDragable(selection);
	}

	protected boolean isDragable(ISelection selection) {
		return true;
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		event.data = LocalSelectionTransfer.getTransfer().getSelection();
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		assert event.detail != DND.DROP_MOVE;
		LocalSelectionTransfer.getTransfer().setSelection(null);
		LocalSelectionTransfer.getTransfer().setSelectionSetTime(0);
	}
}
