package ee.ut.cs.rum.workspace.internal.ui.task.newtask;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ee.ut.cs.rum.database.domain.SubTask;
import ee.ut.cs.rum.workspace.internal.Activator;

public class NewTaskDependenciesScrolledComposite extends ScrolledComposite {
	private static final long serialVersionUID = -7929702586611542936L;
	
	private NewTaskGeneralInfo newTaskGeneralInfo;
	private Composite taskDependenciesContents;
	private ScrolledComposite dependsOnScrolledComposite;
	private ScrolledComposite requiredForScrolledComposite;
	
	private Composite dependsOnContents;
	private Composite subTaskNamesComposite;
	private Composite requiredForContents;
	
	private ArrayList<SubTask> subTasks;
	private ArrayList<ArrayList<Label>> subTaskLabels;
	private ArrayList<Composite> subTaskDependsOnComposites;
	private ArrayList<Composite> subTaskRequiredForComposites;

	public NewTaskDependenciesScrolledComposite(NewTaskGeneralInfo newTaskGeneralInfo) {
		super(newTaskGeneralInfo, SWT.V_SCROLL);
		
		this.newTaskGeneralInfo=newTaskGeneralInfo;
		
		subTasks = new ArrayList<SubTask>();
		subTaskLabels = new ArrayList<ArrayList<Label>>();
		subTaskDependsOnComposites = new ArrayList<Composite>();
		subTaskRequiredForComposites = new ArrayList<Composite>();
		
		createContents();
	}

	private void createContents() {
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		taskDependenciesContents = new Composite(this, SWT.NONE);
		taskDependenciesContents.setLayout(new GridLayout(3, false));
		taskDependenciesContents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		dependsOnScrolledComposite = new ScrolledComposite(taskDependenciesContents, SWT.H_SCROLL);
		dependsOnScrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dependsOnContents = new Composite(dependsOnScrolledComposite, SWT.NONE);
		dependsOnContents.setLayout(new GridLayout());
		dependsOnContents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dependsOnScrolledComposite.setContent(dependsOnContents);


		subTaskNamesComposite = new Composite(taskDependenciesContents, SWT.NONE);
		subTaskNamesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		subTaskNamesComposite.setLayout(new GridLayout());


		requiredForScrolledComposite = new ScrolledComposite(taskDependenciesContents, SWT.H_SCROLL);
		requiredForScrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		requiredForContents = new Composite(requiredForScrolledComposite, SWT.NONE);
		requiredForContents.setLayout(new GridLayout());
		requiredForContents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		requiredForScrolledComposite.setContent(requiredForContents);

		this.setContent(taskDependenciesContents);
		this.addListener(SWT.Resize, new Listener() {
			private static final long serialVersionUID = -8072559449087915608L;

			@Override
			public void handleEvent(Event event) {
				NewTaskDependenciesScrolledComposite.this.recalculateSizes();
			}
		});
	}
	
	private void recalculateSizes() {
		int x1 = NewTaskDependenciesScrolledComposite.this.getSize().x-5;
		taskDependenciesContents.setSize(taskDependenciesContents.computeSize(x1, SWT.DEFAULT));

		int x2 = ((NewTaskDependenciesScrolledComposite.this.getSize().x-5)-subTaskNamesComposite.getSize().x)/2;
		if (dependsOnContents.computeSize(SWT.DEFAULT, SWT.DEFAULT).x > x2) {
			dependsOnContents.setSize(dependsOnContents.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		} else {
			dependsOnContents.setSize(dependsOnContents.computeSize(dependsOnScrolledComposite.getBounds().width-5, SWT.DEFAULT));
		}

		if (requiredForContents.computeSize(SWT.DEFAULT, SWT.DEFAULT).x > x2) {
			requiredForContents.setSize(requiredForContents.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		} else {
			requiredForContents.setSize(requiredForContents.computeSize(requiredForScrolledComposite.getBounds().width-5, SWT.DEFAULT));
		}
		newTaskGeneralInfo.layout(true, true);
	}

	public void clearContents() {
		Activator.getLogger().info("clearing NewTaskDependenciesScrolledComposite");
		
		for (ArrayList<Label> subTaskLabelsSubList : subTaskLabels) {
			for (Label label : subTaskLabelsSubList) {
				if (!label.isDisposed()) {
					label.dispose();
				}
			}
		}
		subTasks.clear();
		subTaskLabels.clear();
		subTaskDependsOnComposites.forEach(c -> c.dispose());
		subTaskRequiredForComposites.forEach(c -> c.dispose());
		subTaskDependsOnComposites.clear();
		subTaskRequiredForComposites.clear();
		this.recalculateSizes();
	}

	public void addSubTask(SubTask subTask) {
		Label l = new Label(subTaskNamesComposite, SWT.NONE);
		l.setText(subTask.getName());
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gd.heightHint=22;
		l.setLayoutData(gd);
		subTaskLabels.add(new ArrayList<Label>());
		subTaskLabels.get(subTaskLabels.size()-1).add(l);
		
		Composite c = new Composite(dependsOnContents, SWT.RIGHT_TO_LEFT);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.heightHint=22;
		c.setLayoutData(gd);
		RowLayout rl = new RowLayout();
		rl.wrap=false;
		rl.marginTop=rl.marginBottom=0;
		c.setLayout(rl);
		subTaskDependsOnComposites.add(c);
		
		c = new Composite(requiredForContents, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.heightHint=22;
		c.setLayoutData(gd);
		rl = new RowLayout();
		rl.wrap=false;
		rl.marginTop=rl.marginBottom=0;
		c.setLayout(rl);
		subTaskRequiredForComposites.add(c);
		subTasks.add(subTask);
		
		this.recalculateSizes();
	}

	public void removeSubtask(SubTask subTask) {
		int subTaskIndex = subTasks.indexOf(subTask);
		subTasks.remove(subTaskIndex);
		for (Label label : subTaskLabels.get(subTaskIndex)) {
			label.dispose();
		}
		subTaskLabels.remove(subTaskIndex);
		subTaskDependsOnComposites.remove(subTaskIndex).dispose();
		subTaskRequiredForComposites.remove(subTaskIndex).dispose();
		
		this.recalculateSizes();
	}

	public void changeSubTaskName(SubTask subTask) {
		int subTaskIndex = subTasks.indexOf(subTask);
		for (Label label : subTaskLabels.get(subTaskIndex)) {
			label.setText(subTask.getName());
		}
		this.recalculateSizes();
	}

	public void addDependency(SubTask dependsOnSubTask, SubTask requiredForSubTask) {
		int dependsOnSubTaskIndex = subTasks.indexOf(dependsOnSubTask);
		int requiredForSubTaskIndex = subTasks.indexOf(requiredForSubTask);
		Label l;
		
		if (subTaskRequiredForComposites.get(dependsOnSubTaskIndex).getChildren().length==0) {
			l = new Label(subTaskRequiredForComposites.get(dependsOnSubTaskIndex), SWT.NONE);
			l.setText(">>  ");
			l.setLayoutData(new RowData());
		}
		l = new Label(subTaskRequiredForComposites.get(dependsOnSubTaskIndex), SWT.BORDER);
		l.setText(requiredForSubTask.getName());
		l.setLayoutData(new RowData());
		subTaskLabels.get(requiredForSubTaskIndex).add(l);				
		
		if (subTaskDependsOnComposites.get(requiredForSubTaskIndex).getChildren().length==0) {
			l = new Label(subTaskDependsOnComposites.get(requiredForSubTaskIndex), SWT.NONE);
			l.setText("<<  ");
			l.setLayoutData(new RowData());
		}
		l = new Label(subTaskDependsOnComposites.get(requiredForSubTaskIndex), SWT.BORDER);
		l.setText(dependsOnSubTask.getName());
		l.setLayoutData(new RowData());
		subTaskLabels.get(dependsOnSubTaskIndex).add(l);			
		
		this.recalculateSizes();
	}
	
	public void removeDependency(SubTask dependsOnSubTask, SubTask requiredForSubTask) {
		int dependsOnSubTaskIndex = subTasks.indexOf(dependsOnSubTask);
		int requiredForSubTaskIndex = subTasks.indexOf(requiredForSubTask);
		
		ArrayList<Label> subTaskLabelsSubList = subTaskLabels.get(requiredForSubTaskIndex);
		for (Control control : subTaskRequiredForComposites.get(dependsOnSubTaskIndex).getChildren()) {
			int subTaskLabelsSubListIndex = subTaskLabelsSubList.indexOf(control);
			if (subTaskLabelsSubListIndex != -1) {
				subTaskLabelsSubList.remove(subTaskLabelsSubListIndex).dispose();
			}
		}
		
		subTaskLabelsSubList = subTaskLabels.get(dependsOnSubTaskIndex);
		for (Control control : subTaskDependsOnComposites.get(requiredForSubTaskIndex).getChildren()) {
			int subTaskLabelsSubListIndex = subTaskLabelsSubList.indexOf(control);
			if (subTaskLabelsSubListIndex != -1) {
				subTaskLabelsSubList.remove(subTaskLabelsSubListIndex).dispose();
			}
		}
		
		if (subTaskRequiredForComposites.get(dependsOnSubTaskIndex).getChildren().length==1) {
			subTaskRequiredForComposites.get(dependsOnSubTaskIndex).getChildren()[0].dispose();
		}
		if (subTaskDependsOnComposites.get(requiredForSubTaskIndex).getChildren().length==1) {
			subTaskDependsOnComposites.get(requiredForSubTaskIndex).getChildren()[0].dispose();
		}
		
		this.recalculateSizes();
	}

}
