package com.thecodest.bigfiles.calculators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.thecodest.bigfiles.calculators.CalculatorFactory.CalculatorFactoryParameters;
import com.thecodest.bigfiles.calculators.CalculatorFactory.RuntimeApi.RuntimeData;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.Test;

class CalculatorFactoryTest {
	private static final Path PATH = Path.of("./src/test/resources/data.csv");
	private static final Path BIG_PATH = Path.of("./src/test/resources/big.csv");
	private static final NoopOnFileChangeStrategy NOOP = new NoopOnFileChangeStrategy();
	private static final CalculatorFactoryParameters DB = new CalculatorFactoryParameters("DB", false, false, PATH, NOOP);

	private static final CalculatorFactoryParameters NAIVE =
		new CalculatorFactoryParameters("NAIVE", false, false, PATH, NOOP);
	private static final CalculatorFactoryParameters INPLACE =
		new CalculatorFactoryParameters("INPLACE", false, false, PATH, NOOP);

	private static final int MEMORY256MB = 268435456;
	private static final int MEMORY64MB = 67108864;
	private CalculatorFactory sut;

	@Test
	void tryToByPinky() {
		sut = new CalculatorFactory();
		var inplace = sut.getCalculator(INPLACE);
		assertThat(inplace).isExactlyInstanceOf(InPlaceAvgCalculator.class);
		assertThat(((InPlaceAvgCalculator) inplace).isParallel()).isFalse();
		var naive = sut.getCalculator(NAIVE);
		assertThat(naive).isExactlyInstanceOf(NaiveAvgCalculator.class);
		assertThat(((NaiveAvgCalculator) naive).isParallel()).isFalse();
		assertThatThrownBy(() -> sut.getCalculator(DB)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void tryToBeBrain() {
		// Lot of memory one CPU small file
		sut = new CalculatorFactory(() -> new RuntimeData(MEMORY256MB, 1));
		var naiveST = sut.getCalculator(new CalculatorFactoryParameters("INPLACE", true, true, PATH, NOOP));
		assertThat(naiveST).isExactlyInstanceOf(NaiveAvgCalculator.class);
		assertThat(((NaiveAvgCalculator) naiveST).isParallel()).isFalse();

		// Lot of memory many CPU small file
		sut = new CalculatorFactory(() -> new RuntimeData(MEMORY256MB, 4));
		var naiveMT = sut.getCalculator(new CalculatorFactoryParameters("INPLACE", false, true, PATH, NOOP));
		assertThat(naiveMT).isExactlyInstanceOf(NaiveAvgCalculator.class);
		assertThat(((NaiveAvgCalculator) naiveMT).isParallel()).isTrue();

		// Lack of memory one CPU small file
		sut = new CalculatorFactory(() -> new RuntimeData(MEMORY64MB, 1));
		var inplaceST = sut.getCalculator(new CalculatorFactoryParameters("NAIVE", true, true, PATH, NOOP));
		assertThat(inplaceST).isExactlyInstanceOf(InPlaceAvgCalculator.class);
		assertThat(((InPlaceAvgCalculator) inplaceST).isParallel()).isFalse();

		// Lack of memory many CPU small file
		sut = new CalculatorFactory(() -> new RuntimeData(MEMORY64MB, 4));
		var inplaceMT = sut.getCalculator(new CalculatorFactoryParameters("NAIVE", true, true, PATH, NOOP));
		assertThat(inplaceMT).isExactlyInstanceOf(InPlaceAvgCalculator.class);
		assertThat(((InPlaceAvgCalculator) inplaceMT).isParallel()).isFalse();

		// Lot of memory many CPU big file
		try {
			final ByteBuffer buf = ByteBuffer.allocate(4).putInt(2);
			buf.rewind();

			final OpenOption[] options = {StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW, StandardOpenOption.SPARSE};
			try (final SeekableByteChannel channel = Files.newByteChannel(BIG_PATH, options);) {
				channel.position(MEMORY256MB * 4);
				channel.write(buf);
			}
			sut = new CalculatorFactory(() -> new RuntimeData(MEMORY256MB, 4));
			var inplaceMT2 = sut.getCalculator(new CalculatorFactoryParameters("NAIVE", false, true, BIG_PATH, NOOP));
			assertThat(inplaceMT2).isExactlyInstanceOf(InPlaceAvgCalculator.class);
			assertThat(((InPlaceAvgCalculator) inplaceMT2).isParallel()).isTrue();
		} catch (IOException e) {
			System.err.println("Cannot allocate big file in test. ");
			e.printStackTrace();
		} finally {
			try {
				Files.deleteIfExists(BIG_PATH);
			} catch (IOException e) {
				System.err.println("Cannot delete big file in test. Path is: " + BIG_PATH.toAbsolutePath());
				e.printStackTrace();
			}
		}

		// Lot of memory many CPU big file but not fit well in memory
		try {
			final ByteBuffer buf = ByteBuffer.allocate(4).putInt(2);
			buf.rewind();

			final OpenOption[] options = {StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW, StandardOpenOption.SPARSE};
			try (final SeekableByteChannel channel = Files.newByteChannel(BIG_PATH, options);) {
				channel.position(MEMORY64MB * 3);
				channel.write(buf);
			}
			sut = new CalculatorFactory(() -> new RuntimeData(MEMORY256MB, 4));
			var inplaceMT2 = sut.getCalculator(new CalculatorFactoryParameters("NAIVE", false, true, BIG_PATH, NOOP));
			assertThat(inplaceMT2).isExactlyInstanceOf(InPlaceAvgCalculator.class);
			assertThat(((InPlaceAvgCalculator) inplaceMT2).isParallel()).isTrue();
		} catch (IOException e) {
			System.err.println("Cannot allocate big file in test. ");
			e.printStackTrace();
		} finally {
			try {
				Files.deleteIfExists(BIG_PATH);
			} catch (IOException e) {
				System.err.println("Cannot delete big file in test. Path is: " + BIG_PATH.toAbsolutePath());
				e.printStackTrace();
			}
		}
	}
}