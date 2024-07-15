package com.thecodest.bigfiles.calculators;

import static org.junit.jupiter.api.Assertions.*;

import com.thecodest.bigfiles.calculators.watch.OnFileChangeStrategy;

class NaiveAvgCalculatorTest extends AbstractFileStreamCalculatorTest {

	@Override
	AbstractFileStreamCalculator getSut(boolean parallel, OnFileChangeStrategy onFileChangeStrategy) {
		return new InPlaceAvgCalculator(parallel, onFileChangeStrategy);
	}
}