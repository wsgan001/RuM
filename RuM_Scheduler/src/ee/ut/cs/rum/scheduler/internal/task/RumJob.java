package ee.ut.cs.rum.scheduler.internal.task;

import java.util.Date;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import ee.ut.cs.rum.database.domain.Plugin;
import ee.ut.cs.rum.database.domain.Task;
import ee.ut.cs.rum.database.domain.TaskStatusEnum;
import ee.ut.cs.rum.database.util.PluginAccess;
import ee.ut.cs.rum.plugins.development.interfaces.RumPluginFactory;
import ee.ut.cs.rum.plugins.development.interfaces.factory.RumPluginWorker;
import ee.ut.cs.rum.scheduler.internal.Activator;
import ee.ut.cs.rum.scheduler.internal.util.TasksData;

public class RumJob implements Job {
	public static final String TASK_ID = "taskId";
	
	public RumJob() {
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobKey jobKey = context.getJobDetail().getKey();
		Long taskId = context.getJobDetail().getJobDataMap().getLong(TASK_ID);
		Task rumJobTask = TasksData.updateTaskStatusInDb(taskId, TaskStatusEnum.STARTING);
		
		Plugin rumJobPlugin = PluginAccess.getPluginDataFromDb(rumJobTask.getPluginId());
		Bundle rumJobPluginBundle = findSelectedPluginBundle(rumJobPlugin);
		if (rumJobPluginBundle==null) {
			rumJobPluginBundle = installSelectedPluginBundle(rumJobPlugin);
		}
		
		RumPluginFactory rumJobPluginFactory = findRumPluginFactoryService(rumJobPluginBundle);
		RumPluginWorker rumJobPluginWorker = rumJobPluginFactory.createRumPluginWorker();
		
		TasksData.updateTaskStatusInDb(taskId, TaskStatusEnum.RUNNING);
		Activator.getLogger().info("RumJob started: " + jobKey + " executing at " + new Date());
		
		Object rumJobResult = rumJobPluginWorker.runWork(rumJobTask.getConfigurationValues());
		if (rumJobResult!=null) {
			Activator.getLogger().info("RumJobResult toString: " + rumJobResult.toString());
		}
		
		TasksData.updateTaskStatusInDb(taskId, TaskStatusEnum.DONE);
		Activator.getLogger().info("RumJob done: " + jobKey + " executing at " + new Date());
	}
	
	
	private Bundle findSelectedPluginBundle(Plugin rumJobPlugin) {
		for (Bundle bundle : Activator.getContext().getBundles()) {
			if (bundle.getLocation().equals("file:///" + rumJobPlugin.getFileLocation())) {
				return bundle;
			}
		}
		return null;
	}

	private Bundle installSelectedPluginBundle(Plugin rumJobPlugin) {
		Bundle selectedPluginBundle = null;
		try {
			selectedPluginBundle = Activator.getContext().installBundle("file:///" + rumJobPlugin.getFileLocation());
			selectedPluginBundle.start();
		} catch (BundleException e) {
			Activator.getLogger().info("Failed loading plugin: " + rumJobPlugin.toString());
		}
		return selectedPluginBundle;
	}
	
	private RumPluginFactory findRumPluginFactoryService(Bundle rumJobPluginBundle) {
		for (ServiceReference<?> serviceReference : rumJobPluginBundle.getRegisteredServices()) {
			String[] objectClasses = (String[])serviceReference.getProperty("objectClass");
			for (String objectClass : objectClasses) {
				if (objectClass.equals(RumPluginFactory.class.getName())) {
					return (RumPluginFactory) rumJobPluginBundle.getBundleContext().getService(serviceReference);
				}
			}
		}
		return null;
	}
}
