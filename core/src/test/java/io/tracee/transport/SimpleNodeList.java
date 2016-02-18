package io.tracee.transport;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SimpleNodeList implements NodeList, Iterable<Node> {

	private final List<Node> backedList = new ArrayList<>();

	public SimpleNodeList() {}

	public SimpleNodeList(Collection<Node> fromNodes) {
		backedList.addAll(fromNodes);
	}

	@Override
	public Node item(int index) {
		return backedList.get(index);
	}

	@Override
	public int getLength() {
		return backedList.size();
	}

	@Override
	public Iterator<Node> iterator() {
		return backedList.iterator();
	}

	public boolean add(Node node) {
		return backedList.add(node);
	}

	public boolean addAll(Collection<? extends Node> c) {
		return backedList.addAll(c);
	}
}
