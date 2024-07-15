package com.thecodest.bigfiles.printers;

import com.thecodest.bigfiles.DataRecord;

class JsonPrinter implements DataPrinter {

	private boolean isOpen = false;

	@Override
	public void print(DataRecord dataRecord) {
		if (!isOpen) {
			System.out.println("[");
			isOpen = true;
		}
		System.out.printf("""
							  {"year": %s, "temp": "%.4g°C"}
							  """, dataRecord.year(), dataRecord.temp());
	}

	@Override
	public void close() {
		System.out.println("]");
	}
}
