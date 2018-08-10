package org.artorg.tools.phantomData.client.commandPattern;

public class UndoRedoNode {
	private final Runnable undo;
	private final Runnable redo;
	private final long timestamp;
	
	public UndoRedoNode(Runnable redo, Runnable undo) {
		this.undo = undo;
		this.redo = redo;
		this.timestamp = System.currentTimeMillis();
	}
	
	public void undo() {
		undo.run();
	}
	
	public void redo() {
		redo.run();
	}

	public long getTimestamp() {
		return timestamp;
	}

}
