package com.thecodest.bigfiles.watcher;

import java.nio.file.Path;

public interface Watcher extends AutoCloseable {

	@Override
	void close();

	void watch(Path path);

	boolean link(Child child);

	boolean unlink(Child child);
}
