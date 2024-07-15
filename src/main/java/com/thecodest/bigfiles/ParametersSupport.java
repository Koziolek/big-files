package com.thecodest.bigfiles;

import com.thecodest.bigfiles.calculators.CalculatorFactory;
import com.thecodest.bigfiles.calculators.CalculatorFactory.CalculatorFactoryParameters;
import com.thecodest.bigfiles.calculators.watch.OnFileChangeStrategyFactory;
import com.thecodest.bigfiles.printers.DataPrinterFactory;
import com.thecodest.bigfiles.watcher.WatcherFactory;
import java.nio.file.Path;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

class ParametersSupport {

	private final CalculatorFactory calculatorFactory = new CalculatorFactory();
	private final DataPrinterFactory dataPrinterFactory = new DataPrinterFactory();
	private final OnFileChangeStrategyFactory onFileChangeStrategyFactory = new OnFileChangeStrategyFactory();
	private final HelpFormatter helpFormatter = new HelpFormatter();

	public Params parseCmdToParams(String[] args) {
		Options options = new Options();

		Option file = Option.builder("f").longOpt("file")
			.argName("file")
			.hasArg()
			.required(true)
			.desc("Path to data file").build();

		Option city = Option.builder("c").longOpt("city")
			.argName("city")
			.hasArg()
			.required(true)
			.desc("Name of city that you interested in").build();

		Option calc = Option.builder("C").longOpt("calculator")
			.argName("calculator")
			.hasArg()
			.required(false)
			.desc("Type of calculator NAIVE or INPLACE. Default is INPLACE").build();

		Option para = Option.builder("p").longOpt("parallel")
			.argName("parallel")
			.hasArg(false)
			.required(false)
			.desc("Work in parallel mode (faster but need more memory). Default false.").build();

		Option smrt = Option.builder("s").longOpt("smart")
			.argName("smart")
			.hasArg(false)
			.required(false)
			.desc("If set ignores -C and -p. Calculator is picked in smart way based on number of CPU and memory.").build();

		Option printer = Option.builder("P").longOpt("printer")
			.argName("printer")
			.hasArg(true)
			.required(false)
			.desc("If set prints data in given format: JSON or CSV. Default format is plain text").build();

		Option onFileChangeStrategyOpt = Option.builder("w").longOpt("watcher")
			.argName("watcher")
			.hasArg(true)
			.required(false)
			.desc("What to do when file change during processing. Default is just log. Use ERROR to stop processing and throw.").build();

		Option help = Option.builder("h").longOpt("help")
			.argName("help")
			.hasArg(false)
			.required(false)
			.desc("Prints this help").build();

		options.addOption(city)
			.addOption(file)
			.addOption(calc)
			.addOption(para)
			.addOption(smrt)
			.addOption(printer)
			.addOption(onFileChangeStrategyOpt)
			.addOption(help);

		CommandLine cmd = null;
		try {
			cmd = new BasicParser().parse(options, args);
		} catch (ParseException e) {
			printHelpAndExit(options, 1);
		}
		if (cmd.hasOption(help)) {
			printHelpAndExit(options, 0);
		}

		var path = Path.of(cmd.getOptionValue(file));
		var cityName = cmd.getOptionValue(city);
		var calculatorName = cmd.getOptionValue(calc, "INPLACE");
		var parallel = cmd.hasOption(para);
		var smart = cmd.hasOption(smrt);
		var printerType = cmd.getOptionValue(printer, "TEXT");
		var onFileChangeStrategyType = cmd.getOptionValue(onFileChangeStrategyOpt, "LOG");

		validatePath(path);
		validateCity(cityName);

		var onFileChangeStrategy = onFileChangeStrategyFactory.getOnFileChangeStrategy(onFileChangeStrategyType);

		var calculatorFactoryParameters = new CalculatorFactoryParameters(calculatorName, parallel, smart, path, onFileChangeStrategy);
		var calculator = calculatorFactory.getCalculator(calculatorFactoryParameters);

		var dataPrinter = dataPrinterFactory.getPrinter(printerType);

		var watcher = new WatcherFactory().getWatcher();
		watcher.link(calculator);
		watcher.watch(path);

		return new Params(new Params.UserParams(path, cityName),
						  new Params.RunParams(calculator, dataPrinter, watcher)
		);
	}

	private void printHelpAndExit(Options options, int status) {
		helpFormatter.printHelp("Use those options: ", options);
		System.exit(status);
	}

	private void validateCity(String cityName) {
		if (cityName.isBlank()) {
			System.out.println("City calculatorName cannot be empty!");
			System.exit(1);
		}
	}

	private void validatePath(Path path) {
		if (!path.toFile().exists()) {
			System.out.println("File does not exists!");
			System.exit(1);
		}
	}
}
