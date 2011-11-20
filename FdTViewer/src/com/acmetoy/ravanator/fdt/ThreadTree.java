package com.acmetoy.ravanator.fdt;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadTree {

	private List<IndentMessageDTO> list;
	
	public ThreadTree(List<IndentMessageDTO> msgs, Long threadId) {
		// importante: ordinati per id !
		/*
		Collections.sort(msgs, new Comparator<IndentMessageDTO>() {
			public int compare(IndentMessageDTO nc1, IndentMessageDTO nc2) {
				return (int) (nc1.getId() - nc2.getId());
			}
		});
		*/
		
		Map<Long, TreeNode> tempMap = new HashMap<Long, TreeNode>();
		TreeNode rootNode = null;

		// build the tree
		for (IndentMessageDTO node : msgs) {
			if (node.getId() == node.getThreadId()) {
				rootNode = new TreeNode(node, 0);
				tempMap.put(node.getId(), rootNode);
				continue;
			}
			TreeNode parent = tempMap.get(node.getParentId());
			if (parent == null) {
				// fallback: BUG nel set del parentId :( ...
				parent = tempMap.get(node.getThreadId());
			}
			TreeNode treeNode = new TreeNode(node, parent.getContent().getIndent() + 1);
			tempMap.put(node.getId(), treeNode);
			parent.addChild(treeNode);
		}

		// traversa il tree
		list = flatternTree(rootNode);
	}
	
	public List<IndentMessageDTO> asList() {
		return list;
	}

	private List<IndentMessageDTO> flatternTree(TreeNode parent) {
		List<IndentMessageDTO> result = new ArrayList<IndentMessageDTO>();

		if (parent == null) {
			return result;
		}
		
		result.add(parent.getContent());
		if (parent.getChildren() != null) {
			for (TreeNode node : parent.getChildren()) {
				result.addAll(flatternTree(node));
			}
		}

		return result;

	}

	public static class TreeNode {
		private IndentMessageDTO content;
		// private TreeNode parent;
		private List<TreeNode> children;
		private boolean orderedChldren;

		public TreeNode(IndentMessageDTO content, int level) {
			this.content = content;
			this.content.setIndent(level);
			this.children = new ArrayList<TreeNode>();
		}

		public IndentMessageDTO getContent() {
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
