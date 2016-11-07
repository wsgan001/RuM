package ee.ut.cs.rum.example.plugin.v1;

import ee.ut.cs.rum.example.plugin.v1.factory.RumPluginWorkerImpl;
import ee.ut.cs.rum.example.plugin.v1.utils.PluginConfiguration;
import ee.ut.cs.rum.plugins.development.interfaces.RumPluginFactory;
import ee.ut.cs.rum.plugins.development.interfaces.factory.RumPluginWorker;

public class RumPluginFactoryImpl implements RumPluginFactory {

	@Override
	public String getPluginInfoJSON() {
		String pluginInfoJSON = PluginConfiguration.generatePluginInfoJSON();
		Activator.getLogger().info(pluginInfoJSON);
		return pluginInfoJSON;
	}

	@Override
	public RumPluginWorker createRumPluginWorker() {
		return new RumPluginWorkerImpl();
	}

}
