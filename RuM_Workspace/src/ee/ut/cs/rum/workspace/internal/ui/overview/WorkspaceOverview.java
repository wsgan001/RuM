package ee.ut.cs.rum.workspace.internal.ui.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ee.ut.cs.rum.controller.RumController;
import ee.ut.cs.rum.workspace.internal.ui.overview.projectstable.ProjectsTableComposite;
import ee.ut.cs.rum.workspace.internal.ui.project.dialog.NewProjectDialog;
import ee.ut.cs.rum.workspace.ui.WorkspaceUI;

public class WorkspaceOverview extends Composite {
	private static final long serialVersionUID = -2991325315513334549L;

	private WorkspaceUI workspaceUI;
	private WorkspaceDetailsContainer workspaceDetailsContainer;

	public WorkspaceOverview(Composite workspaceContainer, WorkspaceUI workspaceUI, RumController rumController) {
		super(workspaceContainer, SWT.NONE);
		this.workspaceUI=workspaceUI;

		this.setLayout(new GridLayout(2, false));
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		new ProjectsTableComposite(this, rumController);
		
		workspaceDetailsContainer = new WorkspaceDetailsContainer(this, rumController);
		
		Button addProjectDialogueButton = new Button(this, SWT.PUSH);
		addProjectDialogueButton.setText("New project");
		addProjectDialogueButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		((GridData) addProjectDialogueButton.getLayoutData()).horizontalSpan = ((GridLayout) this.getLayout()).numColumns;

		addProjectDialogueButton.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = 5383804225331390829L;

			public void handleEvent(Event arg0) {
				NewProjectDialog newProjectDialog = new NewProjectDialog(Display.getCurrent().getActiveShell(), rumController);
				newProjectDialog.open();
			}
		});
		
	}

	public WorkspaceDetailsContainer getWorkspaceDetailsContainer() {
		return workspaceDetailsContainer;
	}

	public WorkspaceUI getWorkspaceUI() {
		return workspaceUI;
	}
}
