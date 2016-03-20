package ee.ut.cs.rum.workspaces.internal.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ee.ut.cs.rum.database.domain.Workspace;
import ee.ut.cs.rum.workspaces.internal.ui.workspace.WorkspacesOverview;
import ee.ut.cs.rum.workspaces.internal.util.WorkspacesData;

public class NewWorkspaceDialog extends Dialog {
	private static final long serialVersionUID = -9152678513520036179L;
	
	private WorkspacesOverview workspacesOverview;
	
	private Text nameValue;
	private Text descriptionValue;
	private Label feedbackTextValue;
	
	private Button okButton;
	
	public NewWorkspaceDialog(Shell activeShell, WorkspacesOverview workspacesOverview) {
		super(activeShell, SWT.APPLICATION_MODAL | SWT.TITLE | SWT.BORDER);
		this.workspacesOverview=workspacesOverview;
	}
	
	public String open() {
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText("Add workspace");
		createContents(shell);
		shell.pack();
		shell.setLocation (100, 100);
		shell.open();
		return null;
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(2, true));

		Label workspaceNameLabel = new Label(shell, SWT.NONE);
		workspaceNameLabel.setText("Workspace name:");
		nameValue = new Text(shell, SWT.BORDER);
		nameValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label descriptionNameLabel = new Label(shell, SWT.NONE);
		descriptionNameLabel.setText("Workspace description:");
		descriptionValue = new Text(shell, SWT.BORDER);
		descriptionValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		feedbackTextValue = new Label(shell, SWT.NONE);
		feedbackTextValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		((GridData) feedbackTextValue.getLayoutData()).horizontalSpan = ((GridLayout) shell.getLayout()).numColumns;
		
		okButton = new Button(shell, SWT.PUSH);
		okButton.setText("OK");
		okButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		okButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = -4306176546808600070L;
			
			public void widgetSelected(SelectionEvent event) {
				if (nameValue.getText().isEmpty() || descriptionValue.getText().isEmpty()) {
					feedbackTextValue.setText("Name and description must be filled");
				} else {
					Workspace workspace = new Workspace();
					workspace.setName(nameValue.getText());
					workspace.setDescription(descriptionValue.getText());
					WorkspacesData.addWorkspaceDataToDb(workspace, workspacesOverview);
					shell.close();
				}
			}
		});
		
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = -4620143960354064523L;

			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
	}
}
