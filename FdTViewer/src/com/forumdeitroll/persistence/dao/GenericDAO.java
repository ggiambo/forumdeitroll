package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.persistence.GenericDTO;
import org.jooq.DSLContext;
import org.jooq.impl.TableRecordImpl;

import java.io.Serializable;

public abstract class GenericDAO<R extends TableRecordImpl<R>, D> {

	protected DSLContext jooq;

	public GenericDAO(DSLContext jooq) {
		this.jooq = jooq;
	}

	protected abstract D recordToDto(R record);

	protected abstract R dtoToRecord(D dto);

}
