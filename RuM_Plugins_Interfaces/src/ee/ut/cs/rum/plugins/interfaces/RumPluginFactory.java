package ee.ut.cs.rum.plugins.interfaces;

import ee.ut.cs.rum.plugins.interfaces.factory.RumPluginResultsVisualizer;
import ee.ut.cs.rum.plugins.interfaces.factory.RumPluginWorker;

public interface RumPluginFactory {
	public String getConfigurationParameters();
	public RumPluginWorker createRumPluginWorker();
	public RumPluginResultsVisualizer createRumPluginResultsVisualizer();
}
