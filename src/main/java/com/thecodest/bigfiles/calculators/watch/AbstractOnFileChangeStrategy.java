package com.thecodest.bigfiles.calculators.watch;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class AbstractOnFileChangeStrategy implements OnFileChangeStrategy {
	protected final AtomicBoolean change = new AtomicBoolean(false);
	protected final AtomicBoolean delete = new AtomicBoolean(false);

	@Override
	public void onChange() {
		change.set(true);
	}

	@Override
	public void onDelete() {
		delete.set(true);
	}
}
