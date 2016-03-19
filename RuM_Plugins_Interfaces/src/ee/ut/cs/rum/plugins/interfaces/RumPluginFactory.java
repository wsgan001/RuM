package ee.ut.cs.rum.plugins.interfaces;

import ee.ut.cs.rum.plugins.interfaces.factory.RumPluginConfiguration;
import ee.ut.cs.rum.plugins.interfaces.factory.RumPluginResultsVisualizer;
import ee.ut.cs.rum.plugins.interfaces.factory.RumPluginWorker;

public interface RumPluginFactory {
	public RumPluginConfiguration getRumPluginConfiguration();
	public RumPluginWorker getRumPluginWorker();
	public RumPluginResultsVisualizer getRumPluginResultsVisualizer();
}