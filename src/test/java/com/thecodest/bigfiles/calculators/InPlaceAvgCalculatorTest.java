package com.thecodest.bigfiles.calculators;

import com.thecodest.bigfiles.calculators.watch.OnFileChangeStrategy;

class InPlaceAvgCalculatorTest extends AbstractFileStreamCalculatorTest {

	@Override
	AbstractFileStreamCalculator getSut(boolean parallel, OnFileChangeStrategy onFileChangeStrategy) {
		return new InPlaceAvgCalculator(parallel, onFileChangeStrategy);
	}
}