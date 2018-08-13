package org.artorg.tools.phantomData.client.commandPattern;

public class UndoRedoNode {
	private final Runnable undo;
	private final Runnable redo;
	private final Runnable save;
	private final long timestamp;
	
	public UndoRedoNode(Runnable redo, Runnable undo, Runnable save) {
		this.undo = undo;
		this.redo = redo;
		this.save = save;
		this.timestamp = System.currentTimeMillis();
	}
	
	public void undo() {
		undo.run();
	}
	
	public void redo() {
		redo.run();
		save();
	}
	
	public void save() {
		System.out.println("Saving value in database");
		save.run();
	}
	
	public long getTimestamp() {
		return timestamp;
	}

}
