package ee.ut.cs.rum.workspace.internal.ui.task.newtask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.gson.Gson;

import ee.ut.cs.rum.controller.RumController;
import ee.ut.cs.rum.database.domain.Plugin;
import ee.ut.cs.rum.database.domain.Task;
import ee.ut.cs.rum.database.domain.enums.SystemParameterName;
import ee.ut.cs.rum.database.domain.enums.TaskStatus;
import ee.ut.cs.rum.database.util.SystemParameterAccess;
import ee.ut.cs.rum.enums.ControllerEntityType;
import ee.ut.cs.rum.enums.ControllerUpdateType;
import ee.ut.cs.rum.plugins.configuration.ui.PluginConfigurationComposite;
import ee.ut.cs.rum.scheduler.util.RumScheduler;
import ee.ut.cs.rum.workspace.internal.Activator;
import ee.ut.cs.rum.workspace.internal.ui.task.details.TaskDetails;

public class FooterButtonsCompositeOld extends Composite {
	private static final long serialVersionUID = 688156596045927568L;

	private RumController rumController;
	
	private NewTaskDetailsOld newTaskDetails;
	private File task_results_root;

	public FooterButtonsCompositeOld(Composite scrolledfooterButtonsComposite, NewTaskDetailsOld newTaskDetails, RumController rumController) {
		super(scrolledfooterButtonsComposite, SWT.NONE);
		
		this.rumController=rumController;
		
		String task_results_root_asString = SystemParameterAccess.getSystemParameterValue(SystemParameterName.TASK_RESULTS_ROOT);
		if (task_results_root_asString!=null) {
			task_results_root = new File(task_results_root_asString);
		}

		this.newTaskDetails=newTaskDetails;
		this.setLayout(new GridLayout(3, false));
		
		if (task_results_root==null) {
			Label label = new Label(this, SWT.NONE);
			label.setText("Adding tasks disabled!");
		} else {
			createContents();
			this.setEnabled(false);
		}
	}
	
	private void createContents() {
		Button button;
		
		button = new Button(this, SWT.PUSH);
		button.setText("Start and show tasks");
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true));
		button.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 5694975289507094763L;
			
			public void widgetSelected(SelectionEvent event) {
				Task task = createNewTask();
				if (task!=null) {
					newTaskDetails.getProjectTabFolder().getSelection().dispose();
					newTaskDetails.getProjectTabFolder().setSelection(0);					
				}
			}
		});
		
		button = new Button(this, SWT.PUSH);
		button.setText("Start and add next");
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
		button.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 5694975289507094763L;
			
			public void widgetSelected(SelectionEvent event) {
				Task task = createNewTask();
				if (task!=null) {
					newTaskDetails.getPluginsTableComposite().getPluginsTableViewer().getTable().deselectAll();
					newTaskDetails.getPluginsTableComposite().getPluginSelectionChangedListener().selectionChanged(null);
				}
			}
		});
		
		button = new Button(this, SWT.PUSH);
		button.setText("Start and show details");
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
		button.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 5694975289507094763L;
			
			public void widgetSelected(SelectionEvent event) {
				Task task = createNewTask();
				if (task!=null) {
					newTaskDetails.getProjectTabFolder().getSelection().dispose();
					
					CTabItem cTabItem = new CTabItem (newTaskDetails.getProjectTabFolder(), SWT.CLOSE);
					cTabItem.setText ("Task " + task.getId().toString());
					cTabItem.setControl(new TaskDetails(newTaskDetails.getProjectTabFolder(), task.getId()));
					newTaskDetails.getProjectTabFolder().setSelection(cTabItem);
				}
			}
		});
		
	}

	//TODO: Feedback to user if task creation fails
	private Task createNewTask(){
		Task task = null;
		
		IStructuredSelection selection = (IStructuredSelection) newTaskDetails.getPluginsTableComposite().getPluginsTableViewer().getSelection();

		PluginConfigurationComposite pluginConfigurationUi = (PluginConfigurationComposite)newTaskDetails.getScrolledPluginConfigurationComposite().getContent();
		Map<String, String> configurationValues = pluginConfigurationUi.getConfigurationValues();
		Gson gson = new Gson();
		String configurationValuesString = gson.toJson(configurationValues);

		Date createdAt = new Date();
		File taskResultsPath = new File(task_results_root, new SimpleDateFormat("ddMMyyyy_HHmmssSSS").format(createdAt));
		
		if (taskResultsPath.mkdir()) {
			Plugin selectedPlugin = (Plugin) selection.getFirstElement();
			task = new Task();
			task.setName("TODO");
			task.setStatus(TaskStatus.NEW);
			task.setPlugin(selectedPlugin);
			task.setDescription("TODO");
			task.setConfigurationValues(configurationValuesString);
			task.setCreatedBy("TODO");
			task.setCreatedAt(createdAt);
			task.setProjectId(newTaskDetails.getProjectTabFolder().getProject());
			task.setOutputPath(taskResultsPath.getPath());
			
			task = (Task)rumController.changeData(ControllerUpdateType.CREATE, ControllerEntityType.TASK, task);
			RumScheduler.scheduleTask(task.getId());			
		} else {
			Activator.getLogger().info("Failed creating task output folder: " + taskResultsPath.getPath());
		}
		
		return task;
	}
}