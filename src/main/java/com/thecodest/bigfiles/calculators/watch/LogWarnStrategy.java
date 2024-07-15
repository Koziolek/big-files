package com.thecodest.bigfiles.calculators.watch;

class LogWarnStrategy extends AbstractOnFileChangeStrategy {

	/**
	 * Always process to the end.
	 *
	 * @param currentElement of stream
	 * @return
	 */
	@Override
	public boolean apply(String currentElement) {
		return true;
	}

	/**
	 * Log information when file change
	 */
	@Override
	public void finisher() {
		if (change.get()) {
			System.out.println(
				"""
					File was changed during processing. You need to manually restart program.
					Returned result could be inconsistent with current state of file.
					""");
		}
		if (delete.get()) {
			System.out.println(
				"""
					File was deleted during processing. You need to manually restart program.
					Returned result could be inconsistent with current state of file.
					""");
		}
	}
}
