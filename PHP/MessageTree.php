<?php
class MessageTree {
	
	private $root;
	
	public function __construct($messages, $threadId) {
		$this->root = new TreeNode($messages[0], null);
		for ($i = 1; $i < count($messages); $i++) {
			$msg = $messages[$i];
			$parentId = $msg->parentId;
			$parent = $this->findNode($parentId, $this->root);
			new TreeNode($msg, $parent);
		}
	}
	
	private function findNode($nodeId, $node) {
		if ($node->message->id == $nodeId) {
			return $node;
		}
		foreach ($node->children as $n) {
			$ret =  $this->findNode($nodeId, $n);
			if ($ret != null) {
				return $ret;
			}
		}
		return null;
	}
	
	public function asList() {
		return $this->asListWithNode($this->root);
	}

	private function asListWithNode($node) {
		$res = array();
		$res[] = $node->message;
		foreach ($node->children as $n) {
			$res = array_merge($res,  $this->asListWithNode($n));
		}
		return $res;
	}
	
}

class TreeNode {
	
	public $children;
	public $message;
	
	public function __construct($message, $parent) {
		$this->message = $message;
		$this->children = array();
		if ($parent != null && $parent->message->id != null) {
			$parent->children[] = $this;
			$ident = $parent->message->indent;
			$message->indent = $ident + 1;
		}
	}
	
}

?>
