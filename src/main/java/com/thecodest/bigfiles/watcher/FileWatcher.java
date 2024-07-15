package com.thecodest.bigfiles.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import lombok.SneakyThrows;

class FileWatcher implements Watcher {

	private final Set<Child> children;
	private final ReentrantLock childrenLock;
	private boolean closed = false;

	public FileWatcher() {
		this.children = new HashSet<>();
		this.childrenLock = new ReentrantLock();
	}

	@Override
	@SneakyThrows
	public void watch(Path path) {
		Thread watchThread = new Thread(() -> this._watchInternals(path));
		watchThread.setDaemon(true); // if die, then die
		watchThread.start();
	}

	@Override
	public boolean link(Child child) {
		try {
			childrenLock.lock();
			return children.add(child);
		} finally {
			childrenLock.unlock();
		}
	}

	@Override
	public boolean unlink(Child child) {
		try {
			childrenLock.lock();
			return children.remove(child);
		} finally {
			childrenLock.unlock();
		}
	}

	@SneakyThrows
	private void _watchInternals(Path file) {
		WatchService watchService = FileSystems.getDefault().newWatchService();
		register(file, watchService);

		WatchKey key;
		while ((key = watchService.take()) != null && !isClosed()) {
			for (WatchEvent<?> event : key.pollEvents()) {
				final Path context = (Path) event.context();
				if (context.equals(file.getFileName())) {
					if (ENTRY_MODIFY.name().equals(event.kind().name())) {
						send(Child.Event.CHANGE);
					}
					if (ENTRY_DELETE.name().equals(event.kind().name())) {
						send(Child.Event.DELETE);
					}
				}
			}
			key.reset();
		}
	}

	private void send(Child.Event event) {
		try {
			childrenLock.lock();
			children.forEach(child -> child.accept(event));
		} finally {
			childrenLock.unlock();
		}
	}

	@SneakyThrows
	private void register(Path file, WatchService watchService) {
		(file.toFile().isFile() ? file.getParent() : file)
			.register(watchService, ENTRY_MODIFY, ENTRY_DELETE);
	}

	private boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		this.closed = true;
	}
}
