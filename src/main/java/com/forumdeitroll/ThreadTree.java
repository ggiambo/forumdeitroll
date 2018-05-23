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

		public void subjectElision(String prevTitle) {
			if (content.getSubject().equals(prevTitle) || (content.getSubject().startsWith("Re: ") && content.getSubject().substring(4).equals(prevTitle))) {
				content.setSubject("&#9632;");
			} else {
				prevTitle = content.getSubject();
			}
			for (final TreeNode child: children) {
				child.subjectElision(prevTitle);
			}
		}

		public Date sortChildByMostRecentDescendant() {
			final Map<Long, Date> mrds = new HashMap<>();
			Date mrd = getContent().getDate();
			for (final TreeNode child: children) {
				final Date cur = child.sortChildByMostRecentDescendant();
				mrds.put(child.getContent().getId(), cur);
				if (cur.compareTo(mrd) > 0) {
					mrd = cur;
				}
			}
			children.sort((n1, n2) -> -mrds.get(n1.getContent().getId()).compareTo(mrds.get(n2.getContent().getId())));
			orderedChldren = true;
			return mrd;
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
