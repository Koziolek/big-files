package com.thecodest.bigfiles.calculators;

import com.thecodest.bigfiles.DataRecord;
import com.thecodest.bigfiles.calculators.watch.OnFileChangeStrategy;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

class InPlaceAvgCalculator extends AbstractFileStreamCalculator<Map<Integer, InPlaceAvgCalculator.LocalDataStore>> {

	private final Map<Integer, LocalDataStore> data;
	private final ReentrantLock lock;

	InPlaceAvgCalculator(boolean parallel, OnFileChangeStrategy onFileChangeStrategy) {
		super(parallel, onFileChangeStrategy);
		this.data = new HashMap<>();
		this.lock = new ReentrantLock();
	}

	@Override
	protected Map<Integer, LocalDataStore> processFile(Path file, String cityName) {
		fileAsStream(file)
			.filter(new CityNameFilter(cityName))
			.map(this::stringToRecord)
			.forEach(this::save);

		return data;
	}

	@Override
	protected List<DataRecord> prepareResponse(Map<Integer, LocalDataStore> __data) {
		return data.entrySet()
			.stream()
			.map(this::asRecord)
			.sorted()
			.toList();
	}

	private void save(DataRecord dataRecord) {
		if (super.parallel) {
			try {
				lock.lock();
				var current = data.getOrDefault(dataRecord.year(), new LocalDataStore(0, 0.));
				data.put(dataRecord.year(), new LocalDataStore(current.count() + 1, current.sum() + dataRecord.temp()));
			} finally {
				lock.unlock();
			}
		} else {
			var current = data.getOrDefault(dataRecord.year(), new LocalDataStore(0, 0.));
			data.put(dataRecord.year(), new LocalDataStore(current.count() + 1, current.sum() + dataRecord.temp()));
		}
	}

	private DataRecord asRecord(Map.Entry<Integer, LocalDataStore> es) {
		return new DataRecord(es.getKey(), es.getValue().avg());
	}

	record LocalDataStore(int count, double sum) {

		public double avg() {
			return sum / count;
		}
	}
}
