package com.thecodest.bigfiles.calculators.watch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ThrowingStrategyTest {

	private ThrowingStrategy sut;

	@Test
	void shouldThrowWhenFileChanged() {
		sut = new ThrowingStrategy();
		// processing element as usual
		assertThat(sut.apply("ANY")).isTrue();
		// File changed
		sut.onChange();
		// processing element is stopped
		assertThat(sut.apply("ANY")).isFalse();
		// finisher throws
		assertThatThrownBy(() -> sut.finisher()).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void shouldThrowWhenFileDeleted() {
		sut = new ThrowingStrategy();
		// processing element as usual
		assertThat(sut.apply("ANY")).isTrue();
		// File deleted
		sut.onDelete();
		// processing element is stopped
		assertThat(sut.apply("ANY")).isFalse();
		// finisher throws
		assertThatThrownBy(() -> sut.finisher()).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void shouldThrowWhenFileNotChangedOrDeleted() {
		sut = new ThrowingStrategy();
		// processing element as usual
		assertThat(sut.apply("ANY")).isTrue();
		// finisher doesn't throws
		assertThatNoException().isThrownBy(() -> sut.finisher());
	}
}