package com.thecodest.bigfiles.calculators;

import com.thecodest.bigfiles.DataRecord;
import com.thecodest.bigfiles.calculators.watch.OnFileChangeStrategy;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

class NaiveAvgCalculator extends AbstractFileStreamCalculator<Map<Integer, Double>> {

	private static final Collector<DataRecord, ?, Map<Integer, Double>> AVG_BY_YEAR = Collectors.groupingBy(
		DataRecord::year,
		Collectors.averagingDouble(DataRecord::temp)
	);

	public NaiveAvgCalculator(boolean parallel, OnFileChangeStrategy onFileChangeStrategy) {
		super(parallel, onFileChangeStrategy);
	}

	@Override
	protected Map<Integer, Double> processFile(Path file, String cityName) {
		return fileAsStream(file)
			.filter(new CityNameFilter(cityName))
			.map(this::stringToRecord)
			.collect(AVG_BY_YEAR);
	}

	@Override
	protected List<DataRecord> prepareResponse(Map<Integer, Double> data) {
		return data.entrySet()
			.stream()
			.map(this::asRecord)
			.sorted()
			.toList();
	}

	private DataRecord asRecord(Map.Entry<Integer, Double> es) {
		return new DataRecord(es.getKey(), es.getValue());
	}
}
