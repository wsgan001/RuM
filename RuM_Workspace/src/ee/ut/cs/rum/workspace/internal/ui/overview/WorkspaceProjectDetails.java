package ee.ut.cs.rum.workspace.internal.ui.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ee.ut.cs.rum.database.domain.Project;

public class WorkspaceProjectDetails extends Composite {
	private static final long serialVersionUID = -5990558506997308715L;

	WorkspaceProjectDetails(Composite parent, Project project) {
		super(parent, SWT.NONE);
		
		this.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true));
		this.setLayout(new GridLayout());
		
		Label l = new Label(this, SWT.BORDER);
		l.setText(project.toString());
	}

}
