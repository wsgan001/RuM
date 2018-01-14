package ee.ut.cs.rum.plugins.configuration.internal.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ee.ut.cs.rum.plugins.configuration.internal.ui.dialog.ObjectInputDialog;
import ee.ut.cs.rum.plugins.configuration.ui.PluginConfigurationUi;
import ee.ut.cs.rum.plugins.development.description.PluginInputObject;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterObjectList;

public class ConfigurationItemObjectList extends Composite implements ConfigurationItemInterface {
	private static final long serialVersionUID = -6538283686156068894L;
	
	private String internalName;
	private String displayName;
	private boolean required;
	private PluginInputObject pluginInputObject;
	private Map<Integer, String> inputObjectInstances;
	
	private Composite objectsComposite;
	
	public ConfigurationItemObjectList(PluginConfigurationUi pluginConfigurationUi, PluginParameterObjectList parameterObjectList, PluginInputObject pluginInputObject) {
		super(pluginConfigurationUi, SWT.NONE);
		
		this.internalName=parameterObjectList.getInternalName();
		this.displayName=parameterObjectList.getDisplayName();
		this.setToolTipText(parameterObjectList.getDescription());
		this.required=parameterObjectList.getRequired();
		this.pluginInputObject=pluginInputObject;
		
		inputObjectInstances = new HashMap<Integer, String>();
		
		this.setLayout(new GridLayout());
		
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		createContents();
	}
	

	private void createContents() {
		objectsComposite = new Composite(this, SWT.BORDER);
		objectsComposite.setLayout(new GridLayout());
		objectsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		ObjectInputDialog objectInputDialog = new ObjectInputDialog(Display.getCurrent().getActiveShell(), this, pluginInputObject);
		
		Button addObjectButton = new Button(this, SWT.PUSH);
		addObjectButton.setText("Add");
		addObjectButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		addObjectButton.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = 8589018096954211029L;

			@Override
			public void handleEvent(Event arg0) {
				objectInputDialog.open();
			}
		});
	}
	
	public void addInputObjectInstance (String instanceParameterValues) {
		if (inputObjectInstances.isEmpty()) {
			inputObjectInstances.put(0, instanceParameterValues);
		} else {
			inputObjectInstances.put(Collections.max(inputObjectInstances.keySet())+1, instanceParameterValues);			
		}
		Label label = new Label(objectsComposite, SWT.NONE);
		label.setText(instanceParameterValues);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		objectsComposite.layout();
	}

	@Override
	public void setValue(String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return "TODO";
	}

	@Override
	public String getInternalName() {
		return internalName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public boolean getRequired() {
		return required;
	}

}
