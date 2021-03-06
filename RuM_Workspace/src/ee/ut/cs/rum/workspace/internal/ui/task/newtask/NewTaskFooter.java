package ee.ut.cs.rum.workspace.internal.ui.task.newtask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

import ee.ut.cs.rum.controller.RumController;
import ee.ut.cs.rum.database.domain.SubTask;
import ee.ut.cs.rum.database.domain.Task;
import ee.ut.cs.rum.database.domain.UserAccount;
import ee.ut.cs.rum.database.domain.enums.SystemParametersEnum;
import ee.ut.cs.rum.database.domain.enums.SubTaskStatus;
import ee.ut.cs.rum.database.util.SystemParameterAccess;
import ee.ut.cs.rum.database.util.UserAccountAccess;
import ee.ut.cs.rum.database.util.exceptions.SystemParameterNotSetException;
import ee.ut.cs.rum.enums.ControllerEntityType;
import ee.ut.cs.rum.enums.ControllerUpdateType;
import ee.ut.cs.rum.plugins.configuration.ui.PluginConfigurationUi;
import ee.ut.cs.rum.scheduler.util.RumScheduler;
import ee.ut.cs.rum.workspace.internal.Activator;
import ee.ut.cs.rum.workspace.internal.ui.task.newtask.sidebar.SubTaskTableViewer;

public class NewTaskFooter extends Composite {
	private static final long serialVersionUID = -8265567504413682063L;

	private int subTaskNameCounter;
	private Button startTaskButton;
	private Button removeSubTaskButton;
	private Button removeAllSubTasksButton;

	public NewTaskFooter(NewTaskComposite newTaskComposite, RumController rumController) {
		super(newTaskComposite, SWT.NONE);

		subTaskNameCounter=1;

		this.setLayout(new GridLayout(4, false));
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		startTaskButton = new Button(this, SWT.PUSH);
		startTaskButton.setText("Start task");
		startTaskButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		startTaskButton.setEnabled(false);
		startTaskButton.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = -2573553526165067810L;

			@Override
			public void handleEvent(Event event) {
				NewTaskStartFeedbackDialog taskStartFeedbackDialog = new NewTaskStartFeedbackDialog(Display.getCurrent().getActiveShell(), rumController, newTaskComposite);
				taskStartFeedbackDialog.open();
				Task task = newTaskComposite.getTask();
				
				try {
					SystemParameterAccess.getSystemParameterValue(SystemParametersEnum.TASK_RESULTS_ROOT);
					List<SubTask> subTasks = new ArrayList<SubTask>();
					task.setSubTasks(subTasks);

					List<NewTaskSubTaskInfo> newTaskSubTaskInfoList = newTaskComposite.getNewTaskDetailsContainer().getNewTaskSubTaskInfoList();
					for (NewTaskSubTaskInfo newTaskSubTaskInfo : newTaskSubTaskInfoList) {
						Date createdAt = new Date();
						UserAccount systemUserAccount = UserAccountAccess.getSystemUserAccount();
						
						newTaskSubTaskInfo.updateAndCheckSubTask();
						SubTask subTask = newTaskSubTaskInfo.getSubTask();
						subTask.setCreatedBy(systemUserAccount);
						subTask.setCreatedAt(createdAt);
						subTask.setLastModifiedBy(systemUserAccount);
						subTask.setLastModifiedAt(createdAt);
						subTask.setTask(task);
						subTasks.add(subTask);
					}

					task = (Task)rumController.changeData(ControllerUpdateType.CREATE, ControllerEntityType.TASK, task, UserAccountAccess.getGenericUserAccount());

					RumScheduler.scheduleTask(task.getId());
					taskStartFeedbackDialog.setQueingSuccessful();
					Activator.getLogger().info("Queued task: " + task.toString());
				} catch (SubTaskUpdateException e) {
					task.getSubTasks().clear();
					taskStartFeedbackDialog.setQueingFailure(e.getMessage());
				} catch (SystemParameterNotSetException e) {
					task.getSubTasks().clear();
					taskStartFeedbackDialog.setQueingFailure(SystemParametersEnum.TASK_RESULTS_ROOT + " not set");
					Activator.getLogger().info("Can not queue task " + e.toString());
				}
			}
		});

		Button addSubTaskButton = new Button(this, SWT.PUSH);
		addSubTaskButton.setText("Add sub-task");
		addSubTaskButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		addSubTaskButton.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = 5479827915459796014L;

			@Override
			public void handleEvent(Event event) {
				SubTaskTableViewer subTaskTableViewer = newTaskComposite.getDetailsSideBar().getSubTaskTableViewer();
				SubTask subTask = new SubTask();
				subTask.setName("(Sub-task " + subTaskNameCounter++ + ")");
				subTask.setDescription("");
				subTask.setStatus(SubTaskStatus.NEW);
				subTaskTableViewer.add(subTask);
				subTaskTableViewer.getTable().select(subTaskTableViewer.getTable().getItemCount()-1);

				NewTaskDetailsContainer newTaskDetailsContainer = newTaskComposite.getNewTaskDetailsContainer();
				NewTaskSubTaskInfo newTaskSubTaskInfo = new NewTaskSubTaskInfo(newTaskDetailsContainer, subTask, rumController);
				newTaskDetailsContainer.getNewTaskSubTaskInfoList().add(newTaskSubTaskInfo);
				newTaskDetailsContainer.getNewTaskGeneralInfo().getNewTaskDependenciesScrolledComposite().addSubTask(subTask);
				newTaskDetailsContainer.showSubTaskInfo(subTaskTableViewer.getTable().getItemCount()-1);

				if (!startTaskButton.getEnabled()) {
					startTaskButton.setEnabled(true);
				}
			}
		});

		removeSubTaskButton = new Button(this, SWT.PUSH);
		removeSubTaskButton.setText("Remove sub-task");
		removeSubTaskButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		removeSubTaskButton.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = 1736754942801222766L;

			@Override
			public void handleEvent(Event event) {
				Table table = newTaskComposite.getDetailsSideBar().getSubTaskTableViewer().getTable();
				NewTaskDetailsContainer newTaskDetailsContainer = newTaskComposite.getNewTaskDetailsContainer();
				NewTaskSubTaskInfo newTaskSubTaskInfo = newTaskDetailsContainer.getNewTaskSubTaskInfoList().get(table.getSelectionIndex());
				PluginConfigurationUi pluginConfigurationUi = newTaskSubTaskInfo.getPluginConfigurationEnabledContainer().getPluginConfigurationUi();

				if (pluginConfigurationUi!=null) {
					newTaskDetailsContainer.notifyTaskOfPluginDeselect(pluginConfigurationUi.getOutputUserFiles(), newTaskDetailsContainer.getNewTaskSubTaskInfoList().get(table.getSelectionIndex()));
				}

				newTaskDetailsContainer.getNewTaskSubTaskInfoList().remove(table.getSelectionIndex());
				table.remove(table.getSelectionIndex());
				newTaskDetailsContainer.getNewTaskGeneralInfo().getNewTaskDependenciesScrolledComposite().removeSubtask(newTaskSubTaskInfo.getSubTask());
				newTaskDetailsContainer.showGeneralInfo();

				if (table.getItemCount()==0 && startTaskButton.getEnabled()) {
					startTaskButton.setEnabled(false);
				}
			}
		});
		removeSubTaskButton.setEnabled(false);
		
		removeAllSubTasksButton = new Button(this, SWT.PUSH);
		removeAllSubTasksButton.setText("Remove all sub-tasks");
		removeAllSubTasksButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		removeAllSubTasksButton.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = 6019579109943293915L;

			@Override
			public void handleEvent(Event arg0) {
				Table table = newTaskComposite.getDetailsSideBar().getSubTaskTableViewer().getTable();
				NewTaskDetailsContainer newTaskDetailsContainer = newTaskComposite.getNewTaskDetailsContainer();
				
				newTaskDetailsContainer.getNewTaskSubTaskInfoList().clear();
				table.removeAll();
				newTaskDetailsContainer.getNewTaskGeneralInfo().getExpectedOutputsTableComposite().clearOutputsTable();
				newTaskDetailsContainer.getNewTaskGeneralInfo().getNewTaskDependenciesScrolledComposite().clearContents();
				newTaskDetailsContainer.showGeneralInfo();

				startTaskButton.setEnabled(false);
				removeAllSubTasksButton.setEnabled(false);
			}
		});
		removeAllSubTasksButton.setEnabled(false);
	}
	
	public void setStartTaskButtonEnabled(boolean enabled) {
		startTaskButton.setEnabled(enabled);
	}
	
	public void setRemoveAllSubTasksButtonEnabled(boolean enabled) {
		removeAllSubTasksButton.setEnabled(enabled);
	}

	public void setRemoveSubTaskButtonEnabled(boolean enabled) {
		removeSubTaskButton.setEnabled(enabled);
	}
}
