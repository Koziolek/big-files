package com.thecodest.bigfiles;

import java.util.List;
import java.util.stream.Stream;

/**
 * Lot of simplification here. You have to pass two arguments on startup:
 *
 * <ul>
 *     <li>{@code -c,--city <city>} – calculatorName of city</li>
 *     <li>{@code -f,--file <file>} – the path to file with data</li>
 * </ul>
 * <p>
 * You could pass additional arguments that control program execution:
 *
 * <ul>
 *     <li>{@code -C, --calculator <calculator>} – to select calculator implementation:
 *     	<ul>
 *     	    <li>{@code NAIVE} – simple but fast calculator that use lot of memory if you combine it with big files. Stores intermediate calculations in memory.</li>
 *     	    <li>{@code INPLACE} – calculates the average as the file processing progresses. Need less memory. Good if you need to process big files.</li>
 *     	</ul>
 *     </li>
 *     <li>{@code -p, --parallel} – if you want to work in multithreading manner. Need more memory and will try to use all available cores.</li>
 *     <li>{@code -s, --smart} – before start processing will try to pick best matching calculator. Take a look at {@link com.thecodest.bigfiles.calculators.CalculatorFactory CalculatorFactory}</li>d
 *     <li>{@code --csv} – Print data in CSV format</li>d
 *     <li>{@code --json} – Print data in JSON format</li>d
 * </ul>
 *
 * @see ParametersSupport
 */
public class Application {

	public static void main(String[] args) {
		var parametersSupport = new ParametersSupport();
		var parameters = parametersSupport.parseCmdToParams(args);
		var userParams = parameters.userParams();
		var runParams = parameters.runParams();
		var dataPrinter = runParams.dataPrinter();
		var calculator = runParams.calculator();
		var watcher = runParams.watcher();

		Stream.of(userParams)
			.limit(1)
			.map(calculator::getData)
			.flatMap(List::stream)
			.forEach(dataPrinter::print);
		dataPrinter.close();
		watcher.close();
	}
}
