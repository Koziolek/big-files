package com.thecodest.bigfiles.printers;

import com.thecodest.bigfiles.DataRecord;
import com.thecodest.bigfiles.OutputHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleDataPrinterTest {

	private final SimpleDataPrinter sut = new SimpleDataPrinter();
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
		sut.print(new DataRecord(2024, 0.0));
		sut.close();
		Assertions.assertThat(helper.getOut()).isEqualTo("2024 » 0.000°C\n");
	}
}