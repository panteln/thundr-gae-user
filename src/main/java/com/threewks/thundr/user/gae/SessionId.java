package com.threewks.thundr.user.gae;

import java.util.UUID;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class SessionId {
	@Id
	private String id;
	@Index
	private Ref<SessionGae> session;

	public SessionId() {

	}

	public SessionId(SessionGae session) {
		this.id = UUID.randomUUID().toString();
		this.session = Ref.create(session);
	}

	public String getId() {
		return id;
	}

	public SessionGae getSession() {
		return session == null ? null : session.get();
	}
}
