package com.thecodest.bigfiles.printers;

public class DataPrinterFactory {

	public DataPrinter getPrinter(String printerType) {
		if (printerType == null) {
			return new SimpleDataPrinter();
		}
		return switch (printerType.toUpperCase()) {
			case "JSON" -> new JsonPrinter();
			case "CSV" -> new CsvPrinter();
			default -> new SimpleDataPrinter();
		};
	}
}
