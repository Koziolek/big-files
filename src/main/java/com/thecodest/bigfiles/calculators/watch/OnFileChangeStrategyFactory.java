package com.thecodest.bigfiles.calculators.watch;

public class OnFileChangeStrategyFactory {

	public OnFileChangeStrategy getOnFileChangeStrategy(String strategy) {
		return switch (strategy) {
			case "ERROR" -> new ThrowingStrategy();
			//case "LOG" -> new LogWarnStrategy();
			default -> new LogWarnStrategy();
		};
	}
}
