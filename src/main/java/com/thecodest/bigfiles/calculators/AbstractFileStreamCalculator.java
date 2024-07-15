package com.thecodest.bigfiles.calculators;

import com.thecodest.bigfiles.DataRecord;
import com.thecodest.bigfiles.calculators.watch.OnFileChangeStrategy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.SneakyThrows;

abstract class AbstractFileStreamCalculator<S extends Map<? extends Object, ? extends Object>> implements AvgCalculator {

	protected final boolean parallel;
	protected final OnFileChangeStrategy fileChangeStrategy;
	private final DataLineParser parser;

	AbstractFileStreamCalculator(boolean parallel, OnFileChangeStrategy fileChangeStrategy) {
		this.parallel = parallel;
		this.fileChangeStrategy = fileChangeStrategy;
		this.parser = new DataLineParser();
	}

	@Override
	public void accept(Event event) {
		switch (event) {
			case CHANGE -> fileChangeStrategy.onChange();
			case DELETE -> fileChangeStrategy.onDelete();
		}
	}

	@Override
	@SneakyThrows
	public List<DataRecord> getData(Path file, String cityName) {
		S data = processFile(file, cityName);
		fileChangeStrategy.finisher();
		return prepareResponse(data);
	}

	abstract protected S processFile(Path file, String cityName);

	abstract protected List<DataRecord> prepareResponse(S data);

	@SneakyThrows
	protected Stream<String> fileAsStream(Path file) {
		var lines = Files.lines(file)
			.takeWhile(fileChangeStrategy::apply);
		return parallel ? lines.parallel() : lines;
	}

	@SneakyThrows
	protected DataRecord stringToRecord(String line) {
		return parser.asRecord(line);
	}

	public boolean isParallel() {
		return parallel;
	}
}
