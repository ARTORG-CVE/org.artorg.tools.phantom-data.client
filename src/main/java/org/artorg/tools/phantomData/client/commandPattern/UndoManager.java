package org.artorg.tools.phantomData.client.commandPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UndoManager {
	private final List<UndoRedoNode> nodes;
	private final List<UndoManager> children;
	private int currentDoneIndex;
	private int lastSavedIndex;
	
	{
		nodes = new ArrayList<UndoRedoNode>();
		currentDoneIndex = -1;
		lastSavedIndex = -1;
		children = new ArrayList<UndoManager>();
	}
	
	public void run() {
		int lastIndex = nodes.size();
		for(int i=currentDoneIndex+1; i<lastIndex; i++)
			nodes.get(i).getRedo().run();
		currentDoneIndex = lastIndex;
	}
	
	public void run(int index) {
		if (currentDoneIndex < index) {
			for(int i=currentDoneIndex+1; i<index; i++)
				nodes.get(i).getRedo().run();
		} else if (currentDoneIndex > index) {
			for(int i=currentDoneIndex; i<index; i--)
				nodes.get(i).getUndo().run();
		} else if (currentDoneIndex == index)
			return;
		currentDoneIndex = index;
	}
	
	public List<UndoManager> getChildrens() {
		return Collections.unmodifiableList(this.children);
	}
	
	public boolean addChild(UndoManager manager) {
		if (this.children.add(manager) == false) return false;
		if (this.nodes.addAll(manager.getNodes()) == false) return false;
		sortNodes();
		return true;
	}
	
	public void sortNodes() {
		this.nodes.sort((n1, n2) -> ((Long)n1.getTimestamp()).compareTo(n2.getTimestamp()));
	}
	
	public boolean removeChild(UndoManager manager) {
		if (this.children.remove(manager) == false) return false;
		if (this.nodes.removeAll(manager.getNodes()) == false) return false;
		return false;
	}
	
	public int getCurrentDoneIndex() {
		return currentDoneIndex;
	}

	public void setCurrentDoneIndex(int currentDoneIndex) {
		if (currentDoneIndex > nodes.size())
			throw new IllegalArgumentException();
		this.currentDoneIndex = currentDoneIndex;
	}

	public void save() {
		if (lastSavedIndex < currentDoneIndex)
			for (int i=lastSavedIndex+1; i < currentDoneIndex; i++ )
				nodes.get(i).save();
		else 
			for (int i=currentDoneIndex; i > lastSavedIndex; i-- )
				nodes.get(i).save();
		lastSavedIndex = currentDoneIndex;
	}
	
	public void setIndex(int currentDoneIndex) {
		
	}
	
	public boolean redoAll() {
		if (currentDoneIndex != nodes.size()) {
			nodes.get(currentDoneIndex++).getRedo().run();
			return true;
		}
		return false;
	}
	
	public boolean undoAll() {
		if (currentDoneIndex>-1) {
			nodes.get(currentDoneIndex).getUndo().run();
			currentDoneIndex--;
			return true;
		}
		return false;
	}
	
	public void redo(int nActions) {
		if (currentDoneIndex + nActions < nodes.size()) nActions = nodes.size()-currentDoneIndex-1;
		while (nActions > 0) {
			nodes.get(currentDoneIndex).getRedo().run();
			currentDoneIndex++;
			nActions--;
		}
	}
	
	public void undo(int nActions) {
		if (currentDoneIndex - nActions < -1) nActions = currentDoneIndex+1;
		while (nActions > 0) {
			nodes.get(currentDoneIndex).getUndo().run();
			currentDoneIndex--;
			nActions--;
		}
	}
	
	public void undo() {
		undo(1);
	}
	
	public void addAndRun(UndoRedoNode node) {
		add(node);
		run(nodes.indexOf(node));
	}
	
	public void add(UndoRedoNode node) {
		nodes.add(node);
		sortNodes();
		int index = nodes.indexOf(node);
		for (int i=index+1; i<nodes.size(); i++) nodes.remove(i);
	}
	
	public void deleteNode(int index) {
		nodes.remove(index);
		if (index < currentDoneIndex)
			currentDoneIndex--;
	}
	
	public void deleteNode(UndoRedoNode n) {
		int index = nodes.indexOf(n);
		deleteNode(index);
	}
	
	public List<UndoRedoNode> getNodes() {
		return nodes;
	}

}
