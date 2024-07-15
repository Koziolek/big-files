package com.thecodest.bigfiles.printers;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DataPrinterFactoryTest {

	private final DataPrinterFactory sut = new DataPrinterFactory();

	public static Stream<Arguments> testData() {
		return Stream.of(
			Arguments.of("JSON", JsonPrinter.class),
			Arguments.of("json", JsonPrinter.class),
			Arguments.of("CSV", CsvPrinter.class),
			Arguments.of("csv", CsvPrinter.class),
			Arguments.of("TEXT", SimpleDataPrinter.class),
			Arguments.of("", SimpleDataPrinter.class),
			Arguments.of(null, SimpleDataPrinter.class)
		);
	}

	@ParameterizedTest
	@MethodSource("testData")
	void shouldReturnCorrectPrinter(String type, Class<DataPrinter> expected) {
		Assertions.assertThat(
			sut.getPrinter(type)).isExactlyInstanceOf(expected);
	}
}