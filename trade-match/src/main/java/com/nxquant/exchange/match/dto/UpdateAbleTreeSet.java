package com.nxquant.exchange.match.dto;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author shilf
 * 更新排序的TreeSet
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

	private DirectionType direction;

	public boolean updateSort(E element) {
		if (remove(element)) {
			return add(element);
		}
		return false;
	}

	public DirectionType getDirection() {
		return direction;
	}

	public void setDirection(DirectionType direction) {
		this.direction = direction;
	}
}
