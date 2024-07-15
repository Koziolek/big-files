package com.thecodest.bigfiles.calculators.watch;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OnFileChangeStrategyFactoryTest {

	private OnFileChangeStrategyFactory sut = new OnFileChangeStrategyFactory();

	@Test
	void shouldReturnStrategyByName() {
		Assertions.assertThat(sut.getOnFileChangeStrategy("ERROR")).isExactlyInstanceOf(ThrowingStrategy.class);
		Assertions.assertThat(sut.getOnFileChangeStrategy("LOG")).isExactlyInstanceOf(LogWarnStrategy.class);
		Assertions.assertThat(sut.getOnFileChangeStrategy("ANY")).isExactlyInstanceOf(LogWarnStrategy.class);
	}
}