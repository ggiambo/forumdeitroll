package com.acmetoy.ravanator.fdt;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.DBObject;

public class ThreadTree {

	private TreeNode root;

	public ThreadTree(List<DBObject> msgs, Long threadId) {
		msgs.get(0).put("indent", 0);
		root = new TreeNode(msgs.get(0), null);
		for (int i = 1; i < msgs.size(); i++) {
			DBObject msg = msgs.get(i);
			Long parentId = (Long) msg.get("parentId");
			TreeNode parent = findNode(parentId, root);
			new TreeNode(msg, parent);
		}
	}

	public List<DBObject> asList() {
		return asList(root);
	}

	private List<DBObject> asList(TreeNode node) {
		List<DBObject> res = new ArrayList<DBObject>();
		res.add(node.data);
		for (TreeNode n : node.children) {
			res.addAll(asList(n));
		}
		return res;
	}

	private TreeNode findNode(Long nodeId, TreeNode node) {
		if (node.data.get("id").equals(nodeId)) {
			return node;
		}
		for (TreeNode n : node.children) {
			TreeNode ret = findNode(nodeId, n);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}

	private class TreeNode {
		
		private DBObject data;
		private List<TreeNode> children;

		public TreeNode(DBObject data, TreeNode parent) {
			this.data = data;
			this.children = new ArrayList<TreeNode>();
			if (parent != null) {
				parent.children.add(this);
				Integer indent = (Integer) parent.data.get("indent");
				data.put("indent", indent + 1);
			}
		}

	}

}
