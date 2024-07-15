package com.thecodest.bigfiles.calculators.watch;

public interface OnFileChangeStrategy {

	/**
	 * Change state of strategy when file was changed.
	 */
	void onChange();

	/**
	 * Change state of strategy when file was deleted.
	 */
	void onDelete();

	/**
	 * Apply strategy on stream. It has access to current element to process.
	 *
	 * @param currentElement of stream
	 * @return true if could process, otherwise return false.
	 */
	boolean apply(String currentElement);

	/**
	 * Define what to do when processing of stream is interrupted. Could just log message, could throw unchecked exception, could restart job, etc.
	 */
	void finisher();
}
