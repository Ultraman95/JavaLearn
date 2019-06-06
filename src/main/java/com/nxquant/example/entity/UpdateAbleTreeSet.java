package com.nxquant.example.entity;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author shilf
 * 更新排序TreeSet
 */
public class UpdateAbleTreeSet<E> extends TreeSet<E>
{
	public UpdateAbleTreeSet() {
		super();
	}

	public UpdateAbleTreeSet(Collection<? extends E> c) {
		super(c);
	}

	public UpdateAbleTreeSet(Comparator<? super E> comparator) {
		super(comparator);
	}

	public UpdateAbleTreeSet(SortedSet<E> s) {
		super(s);
	}

	public boolean updateSort(E element) {
		if (remove(element)) {
			return add(element);
		}
		return false;
	}
}
