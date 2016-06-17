package ee.ut.cs.rum.workspace.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ee.ut.cs.rum.controller.RumController;
import ee.ut.cs.rum.database.domain.Project;
import ee.ut.cs.rum.database.util.ProjectAccess;
import ee.ut.cs.rum.workspace.internal.Activator;
import ee.ut.cs.rum.workspace.internal.ui.project.ProjectTabFolder;
import ee.ut.cs.rum.workspace.ui.WorkspaceUI;

public class ProjectSelectorCombo extends Combo {
	private static final long serialVersionUID = -1671918025859199853L;
	
	private List<Project> projects;
	private List<ProjectTabFolder> projectsDetails;
	private WorkspaceUI workspaceUI;
	private WorkspaceHeader workspaceHeader;

	public ProjectSelectorCombo(WorkspaceHeader workspaceHeader, WorkspaceUI workspaceUI, RumController rumController) {
		super(workspaceHeader, SWT.READ_ONLY);
		this.workspaceUI=workspaceUI;
		this.workspaceHeader=workspaceHeader;

		this.add("Workspace overview");
		this.setVisibleItemCount(10);
		this.select(0);
		updateWorkspaceSelector();

		this.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = -1752969541573951231L;

			public void handleEvent(Event e) {
				updateSelectedProjectDetails();
			}
		});
	}
	
	private void updateWorkspaceSelector() {
		this.projects = new ArrayList<Project>();
		this.projects.add(null);
		this.projects.addAll(ProjectAccess.getProjectsDataFromDb());
		createProjectDetailsList(projects.size());

		for (Project project : projects) {
			if (project!=null) {
				this.add(project.getName());
			}
		}
	}

	public void updateSelectedProjectDetails() {
		int selectedIndex = ProjectSelectorCombo.this.getSelectionIndex();
		StackLayout workspaceContainerLayout = (StackLayout)workspaceUI.getWorkspaceContainer().getLayout();
		
		if (selectedIndex==0) {
			workspaceContainerLayout.topControl=workspaceUI.getProjectsOverview();
		} else {
			ProjectTabFolder selectedWorkspaceDetails = projectsDetails.get(selectedIndex);
			if (selectedWorkspaceDetails==null) {
				selectedWorkspaceDetails = new ProjectTabFolder(workspaceUI.getWorkspaceContainer(), projects.get(selectedIndex));
				projectsDetails.add(selectedIndex, selectedWorkspaceDetails);
			}
			workspaceContainerLayout.topControl=selectedWorkspaceDetails;
		}
		workspaceUI.getWorkspaceContainer().layout();

		
		if (projects.get(selectedIndex)!=null) {
			workspaceHeader.setHeaderTitle("Project: " + projects.get(selectedIndex).getName());
			Activator.getLogger().info("Opened project: " + projects.get(selectedIndex).toString());
		} else {
			workspaceHeader.setHeaderTitle("Workspace overview");
			Activator.getLogger().info("Opened projects overview");
		}
	}
	
	private void createProjectDetailsList(int size) {
		this.projectsDetails = new ArrayList<ProjectTabFolder>();
		for (int i = 0; i < size; i++) {
			projectsDetails.add(null);
		}
	}

	public void updateProjectSelector(List<Project> projects) {
		this.projects = new ArrayList<Project>();
		this.projects.add(null);
		this.projects.addAll(projects);
		//TODO: Update indexes instead of creating a new list
		createProjectDetailsList(this.projects.size());
		if (this.getItemCount()>1) {
			this.remove(1, this.getItemCount()-1);
		}
		for (Project project : this.projects) {
			if (project!=null) {
				this.add(project.getName());
			}
		}
	}

}
