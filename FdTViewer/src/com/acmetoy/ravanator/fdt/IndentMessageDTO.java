package com.acmetoy.ravanator.fdt;

import com.acmetoy.ravanator.fdt.persistence.MessageDTO;

public class IndentMessageDTO extends MessageDTO {
	
	public IndentMessageDTO(MessageDTO dto) throws Exception {
		setId(dto.getId());
		setParentId(dto.getParentId());
		setThreadId(dto.getThreadId());
		setText(dto.getText());
		setSubject(dto.getSubject());
		setAuthor(dto.getAuthor());
		setForum(dto.getForum());
		setDate(dto.getDate());

	}

	private int indent;

	public void setIndent(int indent) {
		this.indent = indent;
	}

	public int getIndent() {
		return indent;
	}
}
