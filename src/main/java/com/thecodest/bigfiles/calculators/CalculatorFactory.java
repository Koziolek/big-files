package com.thecodest.bigfiles.calculators;

import com.thecodest.bigfiles.calculators.watch.OnFileChangeStrategy;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.SneakyThrows;
import org.assertj.core.util.VisibleForTesting;

/**
 * Based on parameters passed to cmd produce @{@link AvgCalculator} instance. Will use "smart" algorithm to select calculator if parameter
 * <samp>smart</samp> is set. In that case parameters like <samp>parallel</samp> or explicite calculator calculatorName should be ignored.
 */
public class CalculatorFactory {

	private static final int MEMORY128MB = 134217728;
	private final RuntimeApi runtimeApi;

	public CalculatorFactory() {
		this(new RuntimeApi(){
			@Override
			public RuntimeData get() {
				return this.getDefault();
			}
		});
	}

	@VisibleForTesting
	CalculatorFactory(RuntimeApi runtimeApi) {
		this.runtimeApi = runtimeApi;
	}

	public AvgCalculator getCalculator(CalculatorFactoryParameters parameters) {
		return parameters.smart() ? beBrain(parameters) : bePinky(parameters);
	}

	/**
	 * Try to be smart (like Brain). This is simple ifology based algorithm:
	 *
	 * <ul>
	 * <li>if memory is less than 128 MB use INPLACE</li>
	 * <li>if file size is bigger than available memory use INPLACE</li>
	 * <li>if 4x file size is smaller than available memory use NAIVE</li>
	 * <li>otherwise use INPLACE</li>
	 * </ul>
	 *
	 * @param parameters
	 * @return
	 */
	@SneakyThrows
	private AvgCalculator beBrain(CalculatorFactoryParameters parameters) {
		var runtime = runtimeApi.get();
		var maxMemory = runtime.memory();
		var cpu = runtime.cpu();
		var fileSize = Files.size(parameters.file());

		var parallel = cpu > 1 && maxMemory > MEMORY128MB; // you don't need parallel if you have only one cpu or not enough memory
		// file is bigger than memory we need to limit memory usage. One thread in place is OK
		if (maxMemory < MEMORY128MB || fileSize > maxMemory) {
			return new InPlaceAvgCalculator(parallel, parameters.onFileChangeStrategy());
		} else if (fileSize * 4 < maxMemory) { // probably small file, multithreading depends on numer of CPU
			return new NaiveAvgCalculator(parallel, parameters.onFileChangeStrategy());
		} else { // all other cases
			return new InPlaceAvgCalculator(parallel, parameters.onFileChangeStrategy());
		}
	}

	/**
	 * Just produce calculator from parameters.
	 *
	 * @param parameters run parameters
	 * @return calculator by calculatorName.
	 */
	private AvgCalculator bePinky(CalculatorFactoryParameters parameters) {
		return switch (parameters.calculatorName().toUpperCase()) {
			case "NAIVE" -> new NaiveAvgCalculator(parameters.parallel(), parameters.onFileChangeStrategy());
			case "INPLACE" -> new InPlaceAvgCalculator(parameters.parallel(), parameters.onFileChangeStrategy());
			default -> throw new IllegalArgumentException("Invalid calculator");
		};
	}

	/**
	 * Helper interface to wrap static @{@link Runtime} class.
	 */
	@FunctionalInterface
	public interface RuntimeApi {

		RuntimeData get();

		default RuntimeData getDefault() {
			var runtime = Runtime.getRuntime();
			var maxMemory = runtime.maxMemory();
			var cpu = runtime.availableProcessors();

			return new RuntimeData(maxMemory, cpu);
		}

		record RuntimeData(long memory, int cpu) {
		}
	}

	/**
	 * @param calculatorName     calculatorName of calculator
	 * @param parallel should use parallel mode
	 * @param smart    should be smart
	 * @param file     path to file (used only by smart calculation)
	 */
	public record CalculatorFactoryParameters(
		String calculatorName,
		boolean parallel,
		boolean smart,
		Path file,
		OnFileChangeStrategy onFileChangeStrategy
	) {
	}
}
