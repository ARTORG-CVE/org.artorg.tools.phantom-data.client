package org.artorg.tools.phantomData.client.commandPattern;

public class UndoRedoNode {
	private final Runnable redo;
	private final Runnable undo;
	private final Runnable save;
	private final long timestamp;
	
	public UndoRedoNode(Runnable redo, Runnable undo, Runnable save) {
		this.redo = redo;
		this.undo = undo;
		this.save = save;
		this.timestamp = System.currentTimeMillis();
	}
	
	public void redo() {
		redo.run();
	}
	
	public void undo() {
		undo.run();
	}
	
	
	public void save() {
		save.run();
	}
	
	public long getTimestamp() {
		return timestamp;
	}

}
