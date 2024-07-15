package com.thecodest.bigfiles.printers;

import com.thecodest.bigfiles.DataRecord;
import java.util.Locale;

class SimpleDataPrinter implements DataPrinter {

	@Override
	public void print(DataRecord dataRecord) {
		System.out.printf(Locale.ENGLISH, "%s » %.4g°C%n", dataRecord.year(), dataRecord.temp());
	}
}
