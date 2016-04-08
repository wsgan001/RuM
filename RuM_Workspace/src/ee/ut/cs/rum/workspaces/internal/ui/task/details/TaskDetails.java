package ee.ut.cs.rum.workspaces.internal.ui.task.details;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.google.gson.Gson;
import ee.ut.cs.rum.database.domain.Plugin;
import ee.ut.cs.rum.database.domain.Task;
import ee.ut.cs.rum.database.util.PluginAccess;
import ee.ut.cs.rum.database.util.TaskAccess;
import ee.ut.cs.rum.plugins.development.description.PluginInfo;
import ee.ut.cs.rum.plugins.development.ui.PluginConfigurationComposite;
import ee.ut.cs.rum.workspaces.internal.ui.task.PluginInfoComposite;
import ee.ut.cs.rum.workspaces.internal.ui.workspace.WorkspaceTabFolder;
import ee.ut.cs.rum.workspaces.internal.util.PluginUtils;

public class TaskDetails extends Composite {
	private static final long serialVersionUID = 5855252537558430818L;

	private Long taskId;
	private PluginInfoComposite pluginInfoComposite;
	PluginConfigurationComposite pluginConfigurationComposite;

	public TaskDetails(WorkspaceTabFolder workspaceTabFolder, Long taskId) {
		super(workspaceTabFolder, SWT.CLOSE);

		this.taskId=taskId;
		this.setLayout(new GridLayout(2, false));

		Task task = TaskAccess.getTaskDataFromDb(taskId);

		createContents(task);

		//TODO: Should look into a better way of doing this (maybe a feature request for Eclipse RAP)
		this.addListener(SWT.Resize, new Listener() {
			private static final long serialVersionUID = -8815015925218184274L;

			public void handleEvent(Event e) {
				int pluginInfoCompositeSizeX = pluginInfoComposite.getContent().getSize().x;
				int pluginConfigurationCompositeSizeX = pluginConfigurationComposite.getSize().x;

				if (TaskDetails.this.getSize().x > pluginInfoCompositeSizeX+pluginConfigurationCompositeSizeX) {
					if (((GridData)pluginInfoComposite.getLayoutData()).grabExcessHorizontalSpace) {
						((GridData)pluginInfoComposite.getLayoutData()).grabExcessHorizontalSpace=false;
					}
				} else {
					if (!((GridData)pluginInfoComposite.getLayoutData()).grabExcessHorizontalSpace) {
						((GridData)pluginInfoComposite.getLayoutData()).grabExcessHorizontalSpace=true;
					}
				}
				TaskDetails.this.layout();
			}
		});

	}

	@SuppressWarnings("unchecked")
	private void createContents(Task task) {
		pluginInfoComposite = new PluginInfoComposite(this);
		Plugin plugin = PluginAccess.getPluginDataFromDb(task.getPluginId());
		pluginInfoComposite.updateSelectedPluginInfo(plugin);
		pluginInfoComposite.getContent().setSize(pluginInfoComposite.getContent().computeSize(SWT.DEFAULT, SWT.DEFAULT));

		PluginInfo pluginInfo = PluginUtils.deserializePluginInfo(plugin);

		ScrolledComposite scrolledPluginConfigurationComposite = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledPluginConfigurationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		pluginConfigurationComposite = new PluginConfigurationComposite(scrolledPluginConfigurationComposite, pluginInfo);
		pluginConfigurationComposite.setEnabled(false);
		scrolledPluginConfigurationComposite.setContent(pluginConfigurationComposite);
		pluginConfigurationComposite.setSize(pluginConfigurationComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Gson gson = new Gson();
		Map<String,String> configurationValues = new HashMap<String,String>();
		configurationValues = gson.fromJson(task.getConfigurationValues(), configurationValues.getClass());
		pluginConfigurationComposite.setConfigurationValues(configurationValues);
	}

	public Long getTaskId() {
		return taskId;
	}
}
