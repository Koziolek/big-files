package com.thecodest.bigfiles.printers;

import com.thecodest.bigfiles.DataRecord;

class CsvPrinter implements DataPrinter {
	@Override
	public void print(DataRecord dataRecord) {
		System.out.printf("%s;%.4g°C%n", dataRecord.year(), dataRecord.temp());
	}
}
