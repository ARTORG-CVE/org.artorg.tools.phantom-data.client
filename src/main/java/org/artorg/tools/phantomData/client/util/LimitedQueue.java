package org.artorg.tools.phantomData.client.util;

import java.util.LinkedList;

public class LimitedQueue<E> extends LinkedList<E> {
	private static final long serialVersionUID = -3310094858488433788L;
	private int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        super.add(o);
        while (size() > limit) { super.remove(); }
        return true;
    }
}