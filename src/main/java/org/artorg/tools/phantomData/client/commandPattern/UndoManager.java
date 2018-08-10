package org.artorg.tools.phantomData.client.commandPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UndoManager {
	private final List<UndoRedoNode> nodes;
	private final List<UndoManager> children;
	private int currentDoneIndex;
	
	{
		nodes = new ArrayList<UndoRedoNode>();
		currentDoneIndex = -1;
		children = new ArrayList<UndoManager>();
	}
	
	public void run() {
		int lastIndex = nodes.size();
		for(int i=currentDoneIndex++; i<lastIndex; i++)
			nodes.get(i).redo();
		currentDoneIndex = lastIndex;
	}
	
	public void run(int index) {
		if (currentDoneIndex < index) {
			for(int i=currentDoneIndex++; i<index; i++)
				nodes.get(i).redo();
		} else if (currentDoneIndex > index) {
			for(int i=currentDoneIndex; i<index; i--)
				nodes.get(i).undo();
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
		this.nodes.sort((n1, n2) -> ((Long)n1.getTimestamp()).compareTo(n2.getTimestamp()));
		return true;
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

	public void setIndex(int currentDoneIndex) {
		
	}
	
	public boolean redo() {
		if (currentDoneIndex != nodes.size()) {
			nodes.get(currentDoneIndex++).redo();
			return true;
		}
		return false;
	}
	
	public boolean undo() {
		if (currentDoneIndex>-1) {
			nodes.get(currentDoneIndex).undo();
			currentDoneIndex--;
			return true;
		}
		return false;
	}
	
	public boolean addNode(UndoRedoNode node) {
		return nodes.add(node);
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
