package com.thecodest.bigfiles;

public record DataRecord(int year, Double temp) implements Comparable<DataRecord> {
	@Override
	public int compareTo(DataRecord o) {
		return this.year() - o.year();
	}
}
