package ee.ut.cs.rum.workspace.internal.ui.project;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ee.ut.cs.rum.workspace.internal.ui.project.dialog.NewProjectDialog;
import ee.ut.cs.rum.workspace.ui.WorkspaceUI;

public class ProjectsOverview extends Composite {
	private static final long serialVersionUID = -2991325315513334549L;
	
	private WorkspaceUI workspaceUI;
	private ProjectsTableViewer projectsTableViewer;
	
	public ProjectsOverview(WorkspaceUI workspaceUI) {
		super(workspaceUI, SWT.NONE);
		this.workspaceUI=workspaceUI;

		this.setLayout(new GridLayout());
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		this.projectsTableViewer = new ProjectsTableViewer(this);
		
		Button addProjectDialogueButton = new Button(this, SWT.PUSH);
		addProjectDialogueButton.setText("Add Project");
		addProjectDialogueButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false));
		
		addProjectDialogueButton.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = 5383804225331390829L;

			public void handleEvent(Event arg0) {
				NewProjectDialog newProjectDialog = new NewProjectDialog(Display.getCurrent().getActiveShell(), workspaceUI);
				newProjectDialog.open();
			}
		});
	}
	
	public ProjectsTableViewer getProjectsTableViewer() {
		return projectsTableViewer;
	}
	
	public WorkspaceUI getWorkspaceUI() {
		return workspaceUI;
	}
}
