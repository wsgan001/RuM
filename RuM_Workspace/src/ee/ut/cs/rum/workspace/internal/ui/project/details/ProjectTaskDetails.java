package ee.ut.cs.rum.workspace.internal.ui.project.details;

import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ee.ut.cs.rum.controller.RumController;
import ee.ut.cs.rum.database.domain.Task;
import ee.ut.cs.rum.enums.ControllerEntityType;
import ee.ut.cs.rum.enums.ControllerUpdateType;
import ee.ut.cs.rum.interfaces.RumUpdatableView;
import ee.ut.cs.rum.workspace.internal.ui.project.ProjectDetailsContainer;
import ee.ut.cs.rum.workspace.internal.ui.project.ProjectTabFolder;
import ee.ut.cs.rum.workspace.internal.ui.task.details.TaskDetailsComposite;

public class ProjectTaskDetails extends Composite implements RumUpdatableView {
	private static final long serialVersionUID = 6334776047604763250L;

	private Display display;
	private RumController rumController;

	private ProjectTabFolder projectTabFolder;
	private Task task;
	private Label taskName;
	private Label taskDescription;
	private Label taskSubTasks;
	private Label taskStatus;
	private Label taskCreatedAt;
	private Label taskLastModifiedAt;


	public ProjectTaskDetails(ProjectDetailsContainer projectDetailsContainer, Task task, RumController rumController) {
		super(projectDetailsContainer, SWT.NONE);

		this.display=Display.getCurrent();
		this.rumController=rumController;
		rumController.registerView(this, ControllerEntityType.TASK);

		this.projectTabFolder = projectDetailsContainer.getProjectOverview().getProjectTabFolder();
		this.task=task;

		this.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true));
		this.setLayout(new GridLayout(3, false));

		Label label;
		label = new Label(this, SWT.NONE);
		label.setText("Task name:");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		taskName = new Label(this, SWT.NONE);
		taskName.setText(task.getName());
		taskName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		FilesTableViewer filesTableViewer = new FilesTableViewer(this, rumController);
		filesTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		((GridData) filesTableViewer.getTable().getLayoutData()).verticalSpan = 6;

		label = new Label(this, SWT.NONE);
		label.setText("Task description:");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		taskDescription = new Label(this, SWT.NONE);
		taskDescription.setText(task.getDescription());
		taskDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		label = new Label(this, SWT.NONE);
		label.setText("Sub-tasks:");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		taskSubTasks = new Label(this, SWT.NONE);
		taskSubTasks.setText("TODO");
		taskSubTasks.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		label = new Label(this, SWT.NONE);
		label.setText("Task status:");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		taskStatus = new Label(this, SWT.NONE);
		taskStatus.setText(task.getStatus().toString());
		taskStatus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		label = new Label(this, SWT.NONE);
		label.setText("Created at:");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		taskCreatedAt = new Label(this, SWT.NONE);
		taskCreatedAt.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(task.getCreatedAt()));
		taskCreatedAt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		label = new Label(this, SWT.NONE);
		label.setText("Last change at:");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		taskLastModifiedAt = new Label(this, SWT.NONE);
		taskLastModifiedAt.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(task.getLastModifiedAt()));
		taskLastModifiedAt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));


		Label subTaskGraph = new Label(this, SWT.NONE);
		subTaskGraph.setText("Sub-task graph (TODO)");
		subTaskGraph.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		((GridData) subTaskGraph.getLayoutData()).horizontalSpan = ((GridLayout) this.getLayout()).numColumns;

		Button openTaskButton = new Button(this, SWT.BORDER);
		openTaskButton.setText("Open task");
		openTaskButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		((GridData) openTaskButton.getLayoutData()).horizontalSpan = ((GridLayout) this.getLayout()).numColumns;
		openTaskButton.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = 5512667422781634707L;

			public void handleEvent(Event e) {
				Long taskId = task.getId();
				CTabItem cTabItem = null;
				
				//Checking if the tab is already open
				for (CTabItem c : projectTabFolder.getItems()) {
					if (c.getControl().getClass() == TaskDetailsComposite.class) {
						if (((TaskDetailsComposite)c.getControl()).getTask().getId() == taskId) {
							cTabItem = c;
							projectTabFolder.setSelection(c);
						}
					}
				}

				if (cTabItem == null) {
					cTabItem = new CTabItem (projectTabFolder, SWT.CLOSE);
					cTabItem.setText ("Task " + taskId.toString());
					cTabItem.setControl(new TaskDetailsComposite(projectTabFolder, rumController, taskId));
					projectTabFolder.setSelection(cTabItem);	
				}
			}
		});
	}

	@Override
	public void controllerUpdateNotify(ControllerUpdateType updateType, Object updatedEntity) {
		if (updatedEntity instanceof Task) {
			Task updatedTask=(Task)updatedEntity;
			if (updatedTask.getId()==task.getId()) {
				switch (updateType) {
				case MODIFIY:
					display.asyncExec(new Runnable() {
						public void run() {
							taskName.setText(updatedTask.getName());
							taskDescription.setText(updatedTask.getDescription());
							taskStatus.setText(updatedTask.getStatus().toString());
							taskLastModifiedAt.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(updatedTask.getLastModifiedAt()));
						}
					});
					break;
				case DELETE:
					display.asyncExec(new Runnable() {
						public void run() {
							//TODO: Display a message to user
							ProjectTaskDetails.this.dispose();
						}
					});
					break;
				default:
					break;
				}
			}
		}

	}
	
	public Task getTask() {
		return task;
	}

	@Override
	public void dispose() {
		rumController.unregisterView(this, ControllerEntityType.TASK);
		super.dispose();
	}

}