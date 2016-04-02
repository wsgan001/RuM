package ee.ut.cs.rum.plugins.internal.ui.dialog;

import java.io.File;
import org.eclipse.rap.fileupload.DiskFileUploadReceiver;
import org.eclipse.rap.fileupload.FileUploadEvent;
import org.eclipse.rap.fileupload.FileUploadListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

import ee.ut.cs.rum.database.domain.Plugin;
import ee.ut.cs.rum.plugins.interfaces.RumPluginFactory;
import ee.ut.cs.rum.plugins.internal.Activator;

public class PluginUploadListener implements FileUploadListener {
	private DiskFileUploadReceiver receiver;
	private PluginUploadDialog pluginUploadDialog;
	
	private Bundle temporaryBundle;
	private boolean serviceCheck;
	
	public PluginUploadListener(DiskFileUploadReceiver receiver, PluginUploadDialog pluginUploadDialog) {
		this.receiver=receiver;
		this.pluginUploadDialog=pluginUploadDialog;
	}
	
	@Override
	public void uploadFailed(FileUploadEvent arg0) {}
	@Override
	public void uploadProgress(FileUploadEvent arg0) {}
	
	@Override
	public void uploadFinished(FileUploadEvent arg0) {
		File temporaryFile = receiver.getTargetFiles()[receiver.getTargetFiles().length-1];
		pluginUploadDialog.setTemporaryFile(temporaryFile);
		Activator.getLogger().info("Uploaded file: " + temporaryFile.getAbsolutePath());
		temporaryBundle = null;
		serviceCheck = false;
		try {
			temporaryBundle = Activator.getContext().installBundle("file:///" + temporaryFile.getAbsolutePath());
			Activator.getLogger().info("Temporary plugin loaded");

			if (temporaryBundle!=null && temporaryBundle.getSymbolicName()!=null) {
				temporaryBundle.start();
				serviceCheck = implementsRumPluginFactory(temporaryBundle);
				temporaryBundle.stop();
				Activator.getLogger().info("Temporary plugin initial start/stop done");
			} else {
				Activator.getLogger().error("Uploaded file is not a valid plugin");
			}
		} catch (BundleException e1) {
			Activator.getLogger().error("Temporary plugin loading failed");
		}

		//TODO: Check for duplicates
		if (temporaryBundle!=null && temporaryBundle.getSymbolicName()!=null && serviceCheck) {
			Plugin temporaryPlugin = new Plugin();
			
			temporaryPlugin.setBundleSymbolicName(temporaryBundle.getHeaders().get("Bundle-SymbolicName"));
			temporaryPlugin.setBundleVersion(temporaryBundle.getHeaders().get("Bundle-Version"));
			temporaryPlugin.setBundleName(temporaryBundle.getHeaders().get("Bundle-Name"));
			temporaryPlugin.setBundleVendor(temporaryBundle.getHeaders().get("Bundle-Vendor"));
			temporaryPlugin.setBundleDescription(temporaryBundle.getHeaders().get("Bundle-Description"));
			temporaryPlugin.setBundleActivator(temporaryBundle.getHeaders().get("Bundle-Activator"));
			temporaryPlugin.setBundleImportPackage(temporaryBundle.getHeaders().get("Import-Package"));
			
			temporaryPlugin.setOriginalFilename(temporaryFile.getName());
			pluginUploadDialog.setTemporaryPlugin(temporaryPlugin);
			
		} else {
			pluginUploadDialog.setTemporaryPlugin(null);
		}

		if (temporaryBundle!=null) {
			try {
				temporaryBundle.uninstall();
				Activator.getLogger().error("Temporary plugin uninstalled");
			} catch (BundleException e) {
				Activator.getLogger().error("Temporary plugin uninstalling failed");
			}
		}
	}

	private boolean implementsRumPluginFactory(Bundle temporaryBundle) {
		if (temporaryBundle.getRegisteredServices()!=null) {
			for (ServiceReference<?> serviceReference : temporaryBundle.getRegisteredServices()) {
				String[] objectClasses = (String[])serviceReference.getProperty("objectClass");
				for (String objectClass : objectClasses) {
					if (objectClass.equals(RumPluginFactory.class.getName())) {
						return true;
					}
				}	
			}
		}
		return false;
	}
}
