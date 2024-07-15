package com.thecodest.bigfiles.printers;

import com.thecodest.bigfiles.DataRecord;
import java.util.Locale;

class JsonPrinter implements DataPrinter {

	private boolean isOpen = false;

	@Override
	public void print(DataRecord dataRecord) {
		if (!isOpen) {
			System.out.println("[");
			isOpen = true;
		}
		System.out.printf(Locale.ENGLISH,
						  """
							  {"year": %s, "temp": "%.4g°C"}
							  """, dataRecord.year(), dataRecord.temp());
	}

	@Override
	public void close() {
		System.out.println("]");
	}
}
