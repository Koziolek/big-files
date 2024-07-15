package com.thecodest.bigfiles.calculators;

import com.thecodest.bigfiles.DataRecord;
import com.thecodest.bigfiles.calculators.watch.OnFileChangeStrategy;
import java.nio.file.Path;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.Test;

abstract class AbstractFileStreamCalculatorTest {

	private static final Path PATH = Path.of("./src/test/resources/data.csv");
	private static final String WARSZAWA = "Warszawa";
	protected AbstractFileStreamCalculator sut;

	public AbstractFileStreamCalculatorTest() {
		Assumptions.assumeThat(PATH).exists();
	}

	@Test
	void shouldCalculateAvgs() {
		sut = getSut(false, new NoopOnFileChangeStrategy());
		var result = sut.getData(PATH, WARSZAWA);
		var expected = List.of(
			new DataRecord(2018, 0.0),
			new DataRecord(2019, 10.0),
			new DataRecord(2020, 15.0)
		);

		Assertions.assertThat(result).hasSameElementsAs(expected);
	}

	abstract AbstractFileStreamCalculator getSut(boolean parallel, OnFileChangeStrategy onFileChangeStrategy);
}

class NoopOnFileChangeStrategy implements OnFileChangeStrategy {

	@Override
	public void onChange() {

	}

	@Override
	public void onDelete() {

	}

	@Override
	public boolean apply(String currentElement) {
		return true;
	}

	@Override
	public void finisher() {

	}
}
