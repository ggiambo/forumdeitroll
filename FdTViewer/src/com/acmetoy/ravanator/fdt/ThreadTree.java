package com.acmetoy.ravanator.fdt;

import java.util.ArrayList;
import java.util.List;

import com.acmetoy.ravanator.fdt.persistence.MessageDTO;

public class ThreadTree {

	private TreeNode root;

	public ThreadTree(List<IndentMessageDTO> msgs, Long threadId) {
		msgs.get(0).setIndent(0);
		root = new TreeNode(msgs.get(0), null);
		for (int i = 1; i < msgs.size(); i++) {
			IndentMessageDTO msg = msgs.get(i);
			Long parentId = msg.getParentId();
			TreeNode parent = findNode(parentId, root);
			if (parent == null) {
				// sould not happen !
				MessageDTO fakeParent = new MessageDTO();
				fakeParent.setAuthor("");
				fakeParent.setDate(msg.getDate());
				fakeParent.setForum(msg.getForum());
				fakeParent.setId(msg.getParentId());
				fakeParent.setSubject(msg.getSubject());
				fakeParent.setText("<span style='color:red'>Inesistente</span>");
				fakeParent.setThreadId(threadId);
				try {
					parent = new TreeNode(new IndentMessageDTO(fakeParent), root);
				} catch (Exception e) {
					parent = root;
				}
			}
			new TreeNode(msg, parent);
		}
	}

	public List<IndentMessageDTO> asList() {
		return asList(root);
	}

	private List<IndentMessageDTO> asList(TreeNode node) {
		List<IndentMessageDTO> res = new ArrayList<IndentMessageDTO>();
		res.add(node.data);
		for (TreeNode n : node.children) {
			res.addAll(asList(n));
		}
		return res;
	}

	private TreeNode findNode(long nodeId, TreeNode node) {
		if (node.data.getId() == nodeId) {
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

	private static class TreeNode {

		private IndentMessageDTO data;
		private List<TreeNode> children;

		public TreeNode(IndentMessageDTO data, TreeNode parent) {
			this.data = data;
			this.children = new ArrayList<TreeNode>();
			if (parent != null) {
				parent.children.add(this);
				Integer indent = parent.data.getIndent();
				data.setIndent(indent + 1);
			}
		}

	}

}
