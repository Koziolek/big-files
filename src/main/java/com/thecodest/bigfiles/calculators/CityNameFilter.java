package com.thecodest.bigfiles.calculators;

import java.util.Objects;
import java.util.function.Predicate;

class CityNameFilter implements Predicate<String> {
	private final String cityName;

	CityNameFilter(String cityName) {
		Objects.requireNonNull(cityName);
		this.cityName = cityName;
	}

	@Override
	public boolean test(String s) {
		if (s == null) {
			return false;
		}
		return s.startsWith(cityName);
	}
}
