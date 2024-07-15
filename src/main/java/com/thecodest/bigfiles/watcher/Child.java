package com.thecodest.bigfiles.watcher;

public interface Child {

	void accept(Event event);

	enum Event {
		CHANGE, DELETE
	}
}
