package fr.univartois.sonargo.test;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.xml.sax.SAXException;

import fr.univartois.sonargo.GoLanguage;
import fr.univartois.sonargo.GoProperties;

public class TestSensor implements Sensor {
	private static final Logger LOGGER = Loggers.get(TestSensor.class);

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.onlyOnLanguage(GoLanguage.KEY).name("Go test JUnit loader sensor");

	}

	@Override
	public void execute(SensorContext context) {
		String reportPath = context.settings().getString(GoProperties.JUNIT_REPORT_PATH_KEY);
		if (reportPath == null) {
			LOGGER.info("no junit report: " + reportPath);
			return;
		}

		JunitParser junitParser = new JunitParser();
		try {
			junitParser.parse(reportPath);

			TestReportSaver.save(context, junitParser.getListTestSuite());

		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOGGER.error("Parse exception ", e);
		}
	}

}
