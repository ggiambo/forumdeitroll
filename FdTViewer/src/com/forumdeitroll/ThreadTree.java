package com.forumdeitroll;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	}

}
