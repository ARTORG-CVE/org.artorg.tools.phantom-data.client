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
	
//	public void undo(UndoManager undoManager) {
//		undoManager.
//		
//		undo.run();
//	}
//	
//	public void redo() {
//		redo.run();
//	}
	
	

	public void save() {
		System.out.println("Saving value in database");
		save.run();
	}
	
	public Runnable getUndo() {
		return undo;
	}

	public Runnable getRedo() {
		return redo;
	}

	public Runnable getSave() {
		return save;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

}
