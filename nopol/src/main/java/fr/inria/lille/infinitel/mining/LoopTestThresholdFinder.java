package fr.inria.lille.infinitel.mining;

import static fr.inria.lille.commons.utils.library.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.utils.library.LoggerLibrary.newLoggerFor;
import static java.lang.String.format;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;
import org.slf4j.Logger;

import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.infinitel.instrumenting.CompoundLoopMonitor;
import fr.inria.lille.infinitel.loop.While;

public class LoopTestThresholdFinder {
	
	public LoopTestThresholdFinder(MonitoringTestExecutor testExecutor) {
		this.testExecutor = testExecutor;
	}

	public Map<TestCase, Integer> thresholdsByTest(While loop, Collection<TestCase> failedTests, Collection<TestCase> successfulTests) {
		Map<TestCase, Integer> thresholdMap = MapLibrary.newHashMap();
		int threshold = monitor().threshold();
		findThresholdsFromExecution(successfulTests, loop, thresholdMap, threshold);
		findThresholdsProbing(failedTests, loop, thresholdMap, threshold);
		return thresholdMap;
	}
	
	protected void findThresholdsFromExecution(Collection<TestCase> tests, While loop, Map<TestCase, Integer> thresholdMap, Integer threshold) {
		for (TestCase testCase : tests) {
			logDebug(logger, format("[Executing %s to get test threshold in %s]", testCase.toString(), loop.toString()));
			Result result = testExecutor().execute(testCase, loop);
			assertTrue(format("Could not find threshold for %s, it is a faling test", testCase), result.wasSuccessful());
			Integer lastRecord = monitor().lastRecordIn(loop);
			if (lastRecord.equals(threshold)) {
				probeTestThreshold(testCase, loop, thresholdMap, threshold);
			} else {
				thresholdMap.put(testCase, lastRecord);
			}
		}
	}
	
	protected void findThresholdsProbing(Collection<TestCase> tests, While loop, Map<TestCase, Integer> thresholdMap, Integer threshold) {
		for (TestCase testCase : tests) {
			logDebug(logger, format("[Finding test threshold of %s in %s]", testCase.toString(), loop.toString()));
			probeTestThreshold(testCase, loop, thresholdMap, threshold);
		}
	}

	protected void probeTestThreshold(TestCase testCase, While loop, Map<TestCase, Integer> thresholdMap, Integer threshold) {
		for (int testThreshold = 0; testThreshold <= threshold; testThreshold += 1) {
			Result result = testExecutor().execute(testCase, loop, testThreshold);
			if (result.wasSuccessful()) {
				thresholdMap.put(testCase, testThreshold);
				return;
			}
		}
		fail("Could not find test threshold for " + testCase);
	}
	
	private CompoundLoopMonitor monitor() {
		return testExecutor().monitor();
	}
	
	private MonitoringTestExecutor testExecutor() {
		return testExecutor;
	}
	
	private MonitoringTestExecutor testExecutor;
	private static Logger logger = newLoggerFor(LoopTestThresholdFinder.class);
}
