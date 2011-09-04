<?php
class Message {
	
	public $id;
	public $text;
	public $date;
	public $subject;
	public $threadId;
	public $parentId;
	public $author;
	public $forum;
	
	public $indent;
	
	public function __construct($databaseRow) {
        $this->id = $databaseRow['id'];
        $this->text = $databaseRow['text'];
        $this->date = $databaseRow['date'];
        $this->subject = $databaseRow['subject'];
        $this->threadId = $databaseRow['threadId'];
        $this->parentId = $databaseRow['parentId'];
        $this->author = $databaseRow['author'];
        $this->forum = $databaseRow['forum'];
        $this->indent = 0;
    }

}
?>

