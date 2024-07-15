package com.thecodest.bigfiles;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import java.lang.management.ManagementFactory;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ApplicationTest {

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
	void shouldRunApplication() {
		Application.main(new String[] {
			"-c",
			"Warszawa", "-f", "./src/test/resources/example_file.csv", "-p", "--printer", "CSV"
		});
		Assertions.assertThat(helper.getOut()).isEqualTo("""
															 2018;13,52°C
															 2019;13,81°C
															 2020;16,12°C
															 2021;15,61°C
															 2022;14,68°C
															 2023;15,46°C
															 """);
	}

	@SneakyThrows
	@Test
	@Disabled("Not work after java 17!")
	void shouldShowHelp() {
		final int exitCode = SystemLambda.catchSystemExit(() -> Application.main(new String[] {"-p", "--printer", "CSV"}));
		Assertions.assertThat(helper.getOut()).contains("Use those options");
		Assertions.assertThat(exitCode).isEqualTo(1);
	}
}
