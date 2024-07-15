package com.thecodest.bigfiles.calculators;

import com.thecodest.bigfiles.DataRecord;
import com.thecodest.bigfiles.Params;
import com.thecodest.bigfiles.watcher.Child;
import java.nio.file.Path;
import java.util.List;

public interface AvgCalculator extends Child {

	List<DataRecord> getData(Path file, String cityName);

	default List<DataRecord> getData(Params.UserParams params) {
		return getData(params.file(), params.cityName());
	}

	@Override
	default void accept(Event event) {
	}
}
