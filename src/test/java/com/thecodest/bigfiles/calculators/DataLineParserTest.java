package com.thecodest.bigfiles.calculators;

import com.thecodest.bigfiles.DataRecord;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DataLineParserTest {

	private DataLineParser sut = new DataLineParser();

	public static Stream<Arguments> validTestData() {
		return Stream.of(
			Arguments.of("Warszawa;2018-09-19 05:17:32.619;9.97", new DataRecord(2018, 9.97)),
			Arguments.of("Warszawa;2018-09-23 08:56:57.560;-9.44", new DataRecord(2018, -9.44)),
			Arguments.of("Kraków;2021-04-29 15:28:46.798;16.8", new DataRecord(2021, 16.8))
		);
	}

	public static Stream<Arguments> invalidInput() {
		return Stream.of(
			Arguments.of(null, NullPointerException.class),
			Arguments.of("", IndexOutOfBoundsException.class),
			Arguments.of("    ", NumberFormatException.class),
			Arguments.of("Kraków;2021-04-29 15:28:46.798;", NumberFormatException.class),
			Arguments.of("Kraków2021-04-29 15:28:46.798;16.8", NumberFormatException.class),
			Arguments.of("Kraków;2021-04-29 15:28:46.79816.8", NumberFormatException.class)
		);
	}

	@ParameterizedTest
	@MethodSource("validTestData")
	void shouldSplitLine(String line, DataRecord expected) {
		Assertions.assertThat(sut.asRecord(line)).isEqualTo(expected);
	}

	@ParameterizedTest
	@MethodSource("invalidInput")
	void emptyLine(String line, Class<Throwable> expected) {
		Assertions.assertThatThrownBy(() -> sut.asRecord(line)).isInstanceOf(expected);
	}
}