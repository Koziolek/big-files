package com.thecodest.bigfiles.printers;

import static org.junit.jupiter.api.Assertions.*;

import com.thecodest.bigfiles.DataRecord;
import com.thecodest.bigfiles.OutputHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonPrinterTest {

	private final JsonPrinter sut = new JsonPrinter();
	private final OutputHelper helper = new OutputHelper();

	@BeforeEach
	void setUp() {
		helper.setUpStreams();
	}

	@AfterEach
	void tearDown() {
		helper.restoreStreams();
	}

	@Test
	void shouldPrintRecord() {
		sut.print(new DataRecord(2023, 0.0));
		sut.print(new DataRecord(2024, 0.0));
		sut.close();
		Assertions.assertThat(helper.getOut()).isEqualTo("""
															 [
															 {"year": 2023, "temp": "0.000°C"}
															 {"year": 2024, "temp": "0.000°C"}
															 ]
															 """);
	}
}