package com.thecodest.bigfiles.printers;

import com.thecodest.bigfiles.DataRecord;

public interface DataPrinter extends AutoCloseable {
	void print(DataRecord dataRecord);

	@Override
	default void close() {
	}
}
