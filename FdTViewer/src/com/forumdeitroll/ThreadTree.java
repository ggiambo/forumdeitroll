package com.forumdeitroll;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import com.forumdeitroll.persistence.MessageDTO;

public class ThreadTree {

	private TreeNode rootNode;

	public ThreadTree(List<MessageDTO> msgs) {
		// importante: ordinati per id !
		Collections.sort(msgs, new Comparator<MessageDTO>() {
			public int compare(MessageDTO nc1, MessageDTO nc2) {
				return (int) (nc1.getId() - nc2.getId());
			}
		});

		Map<Long, TreeNode> tempMap = new HashMap<Long, TreeNode>();
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

		public TreeNode(MessageDTO content) {
			this.content = content;
			this.children = new ArrayList<TreeNode>();
		}

		public MessageDTO getContent() {
			return content;
		}

		public void addChild(TreeNode child) {
			orderedChldren = false;
			children.add(child);
		}

		public List<TreeNode> getChildren() {
			if (!orderedChldren) {
				Collections.sort(children, new Comparator<TreeNode>() {
					public int compare(TreeNode tn1, TreeNode tn2) {
						return tn1.getContent().getDate().compareTo(
							tn2.getContent().getDate());
					}
				});
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
			final Map<Long, Date> mrds = new HashMap<Long, Date>();
			Date mrd = getContent().getDate();
			for (final TreeNode child: children) {
				final Date cur = child.sortChildByMostRecentDescendant();
				mrds.put(child.getContent().getId(), cur);
				if (cur.compareTo(mrd) > 0) {
					mrd = cur;
				}
			}
			Collections.sort(children, new Comparator<TreeNode>() {
				public int compare(final TreeNode n1, final TreeNode n2) {
					return -mrds.get(n1.getContent().getId()).compareTo(mrds.get(n2.getContent().getId()));
				}
			});
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
