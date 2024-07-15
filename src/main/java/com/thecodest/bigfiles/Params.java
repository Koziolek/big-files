package com.thecodest.bigfiles;

import com.thecodest.bigfiles.calculators.AvgCalculator;
import com.thecodest.bigfiles.printers.DataPrinter;
import java.nio.file.Path;

public record Params(UserParams userParams, RunParams runParams) {

	public record UserParams(Path file, String cityName) {
	}

	public record RunParams(AvgCalculator calculator, DataPrinter dataPrinter, com.thecodest.bigfiles.watcher.Watcher watcher) {
	}
}