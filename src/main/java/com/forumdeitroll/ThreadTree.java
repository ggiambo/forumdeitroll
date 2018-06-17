package com.forumdeitroll;


import com.forumdeitroll.persistence.MessageDTO;

import java.util.*;

public class ThreadTree {

	private TreeNode rootNode;

	public ThreadTree(List<MessageDTO> msgs) {
		// importante: ordinati per id !
		msgs.sort((nc1, nc2) -> (int) (nc1.getId() - nc2.getId()));

		Map<Long, TreeNode> tempMap = new HashMap<>();
		rootNode = null;

		// build the tree
		for (MessageDTO node : msgs) {
			if (node.getId() == node.getThreadId()) {
				rootNode = new TreeNode(node);
				tempMap.put(node.getId(), rootNode);
				continue;
			}
			TreeNode parent = tempMap.get(node.getParentId());
			if (parent == null) {
				// fallback: BUG nel set del parentId :( ...
				parent = tempMap.get(node.getThreadId());
			}
			TreeNode treeNode = new TreeNode(node);
			tempMap.put(node.getId(), treeNode);
			parent.addChild(treeNode);
		}
	}

	public TreeNode getRoot() {
		return rootNode;
	}

	public static class TreeNode {
		private MessageDTO content;
		private List<TreeNode> children;
		private boolean orderedChldren;

		TreeNode(MessageDTO content) {
			this.content = content;
			this.children = new ArrayList<>();
		}

		public MessageDTO getContent() {
			return content;
		}

		void addChild(TreeNode child) {
			orderedChldren = false;
			children.add(child);
		}

		// called by .jsp
		public List<TreeNode> getChildren() {
			if (!orderedChldren) {
				children.sort(Comparator.comparing(tn -> tn.getContent().getDate()));
				orderedChldren = true;
			}
			return children;
		}

		public TreeNode setNext(final MessageDTO prev) {
			if (prev != null) {
				prev.setNextId(this.content.getId());
				content.setPrevId(prev.getId());
			}

			TreeNode curPrev = this;

			for (final TreeNode child: children) {
				curPrev = child.setNext(curPrev.content);
			}

			return curPrev;
		}
	}

}
