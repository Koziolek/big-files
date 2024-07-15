package com.thecodest.bigfiles.calculators.watch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.thecodest.bigfiles.OutputHelper;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogWarnStrategyTest {

	private final OutputHelper helper = new OutputHelper();
	private LogWarnStrategy sut;

	@BeforeEach
	public void setUp() {
		helper.setUpStreams();
	}

	@AfterEach
	public void tearDown() {
		helper.restoreStreams();
	}

	@Test
	void shouldThrowWhenFileChanged() {
		sut = new LogWarnStrategy();
		// processing element as usual
		assertThat(sut.apply("ANY")).isTrue();
		// File changed
		sut.onChange();
		// processing element continues
		assertThat(sut.apply("ANY")).isTrue();
		// finisher do nothing
		assertThatNoException().isThrownBy(() -> sut.finisher());
		assertThat(helper.getOut()).contains("File was changed during processing.");
	}

	@Test
	void shouldThrowWhenFileDeleted() {
		sut = new LogWarnStrategy();
		// processing element as usual
		assertThat(sut.apply("ANY")).isTrue();
		// File deleted
		sut.onDelete();
		// processing element continues
		assertThat(sut.apply("ANY")).isTrue();
		// finisher do nothing
		assertThatNoException().isThrownBy(() -> sut.finisher());
		assertThat(helper.getOut()).contains("File was deleted during processing.");
	}

	@Test
	void shouldThrowWhenFileNotChangedOrDeleted() {
		sut = new LogWarnStrategy();
		// processing element as usual
		assertThat(sut.apply("ANY")).isTrue();
		// finisher doesn't throws
		assertThatNoException().isThrownBy(() -> sut.finisher());
		assertThat(helper.getOut()).isEmpty();
	}
}