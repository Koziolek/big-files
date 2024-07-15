package com.thecodest.bigfiles.calculators;

import com.thecodest.bigfiles.DataRecord;
import java.util.Objects;

class DataLineParser {

	/**
	 * Data line structure is <samp>city;yyyy-mm-dd HH:mm:ss.SSS;temp</samp>.
	 * We transform it to @{@link DataRecord} extracting year (<samp>yyyy</samp>) and temperature (<samp>temp</samp>).
	 *
	 * @param line single data line
	 * @return data record for year and temperature
	 */
	public DataRecord asRecord(String line) {
		Objects.requireNonNull(line);
		// DO NOT USE SPLIT! Because it uses regex that is expensive.
		final int yearStartAt = line.indexOf(';') + 1;
		final int tempStartAt = line.lastIndexOf(';') + 1;

		int year = Integer.valueOf(line.substring(yearStartAt, yearStartAt + 4));
		double temp = Double.valueOf(line.substring(tempStartAt));
		return new DataRecord(year, temp);
	}
}
