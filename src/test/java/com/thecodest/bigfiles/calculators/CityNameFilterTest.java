package com.thecodest.bigfiles.calculators;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CityNameFilterTest {

	private CityNameFilter sut;

	@Test
	void shouldFilterOutCitiesByName() {
		sut = new CityNameFilter("Warszawa");

		var expected = List.of("Warszawa");

		var result = List.of("Kraków", "Warszawa", "Łódź", "", "warszawa")
			.stream()
			.filter(sut)
			.toList();

		Assertions.assertThat(result).hasSize(1).hasSameElementsAs(expected);
	}

	@Test
	void shouldBeFalseOnNull() {
		sut = new CityNameFilter("Warszawa");
		Assertions.assertThat(sut.test(null)).isFalse();
	}
}