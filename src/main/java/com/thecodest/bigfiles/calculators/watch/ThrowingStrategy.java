package com.thecodest.bigfiles.calculators.watch;

class ThrowingStrategy extends AbstractOnFileChangeStrategy {

	/**
	 * Stop processing when file changed or deleted
	 *
	 * @param currentElement of stream
	 * @return false if changed or deleted.
	 */
	@Override
	public boolean apply(String currentElement) {
		return !(delete.get() || change.get());
	}

	/**
	 * Throws @{@link IllegalStateException} when file change.
	 */
	@Override
	public void finisher() {
		if (delete.get() || change.get()) {
			throw new IllegalStateException("File changed during processing");
		}
	}
}
