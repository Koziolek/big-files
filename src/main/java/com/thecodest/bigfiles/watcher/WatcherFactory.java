package com.thecodest.bigfiles.watcher;

public class WatcherFactory {

	public Watcher getWatcher() {
		return new FileWatcher();
	}
}
