package ee.ut.cs.rum.plugins.configuration.internal.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.google.gson.Gson;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemDouble;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemInteger;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemInterface;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemLabel;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemObjectList;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemObjectSelection;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemSelection;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemString;
import ee.ut.cs.rum.plugins.configuration.ui.PluginConfigurationUi;
import ee.ut.cs.rum.plugins.development.description.PluginInputObject;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameter;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterDouble;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterInteger;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterLabel;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterObjectSelection;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterSelection;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterString;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterType;

public class ObjectInputDialog extends Dialog {
	private static final long serialVersionUID = 6650172134399735836L;
	
	private Shell shell;
	private ConfigurationItemObjectList configurationItemObjectList;
	private PluginInputObject pluginInputObject;
	private String displayName;
	private PluginConfigurationUi pluginConfigurationUi;
	
	private Label feedbackTextLabel;
	
	private List<ConfigurationItemInterface> configurationItems;

	public ObjectInputDialog(Shell activeShell, ConfigurationItemObjectList configurationItemObjectList, PluginInputObject pluginInputObject, String displayName, PluginConfigurationUi pluginConfigurationUi) {
		super(activeShell, SWT.APPLICATION_MODAL | SWT.TITLE | SWT.BORDER | SWT.RESIZE);
		
		this.configurationItemObjectList = configurationItemObjectList;
		this.pluginInputObject = pluginInputObject;
		this.displayName=displayName;
		this.pluginConfigurationUi=pluginConfigurationUi;
	}
	
	public String open() {
		shell = new Shell(this.getParent(), getStyle());
		shell.setText("Add object to " + displayName);
		createContents();
		shell.pack();
		shell.setLocation (100, 100);
		shell.setMinimumSize(shell.computeSize(500, SWT.DEFAULT));
		shell.open();
		return null;
	}

	private void createContents() {
		shell.setLayout(new GridLayout());
		createObjectParametersComposite();
		feedbackTextLabel = new Label(shell, SWT.NONE);
		feedbackTextLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		createButtonsComposite();
	}
	
	private void createObjectParametersComposite() {
		Composite objectParametersComposite  = new Composite(shell, SWT.NONE);
		objectParametersComposite.setLayout(new GridLayout(2, false));
		objectParametersComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		configurationItems = new ArrayList<ConfigurationItemInterface>();
		
		Label label;
		for (PluginParameter pluginParameter : pluginInputObject.getParameters()) {
			if (!pluginParameter.getParameterType().equals(PluginParameterType.LABEL)) {
				label = new Label (objectParametersComposite, SWT.NONE);
				if (pluginParameter.getRequired()) {
					label.setText(pluginParameter.getDisplayName()+" *");				
				} else {
					label.setText(pluginParameter.getDisplayName()+"  ");
				}
				label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));				
			}
			
			switch (pluginParameter.getParameterType()) {
			case STRING:
				PluginParameterString parameterString = (PluginParameterString) pluginParameter;
				configurationItems.add(new ConfigurationItemString(objectParametersComposite, parameterString));
				break; 
			case INTEGER:
				PluginParameterInteger parameterInteger = (PluginParameterInteger) pluginParameter;
				configurationItems.add(new ConfigurationItemInteger(objectParametersComposite, parameterInteger));
				break; 
			case DOUBLE:
				PluginParameterDouble parameterDouble = (PluginParameterDouble) pluginParameter;
				configurationItems.add(new ConfigurationItemDouble(objectParametersComposite, parameterDouble));
				break; 
			case SELECTION:
				PluginParameterSelection parameterSelection = (PluginParameterSelection) pluginParameter;
				configurationItems.add(new ConfigurationItemSelection(objectParametersComposite, parameterSelection));
				break;
			case LABEL:
				PluginParameterLabel pluginParameterLabel = (PluginParameterLabel) pluginParameter;
				//This is not an actual configurationItem, therefore it should not be added to configurationItems list
				ConfigurationItemLabel configurationItemLabel = new ConfigurationItemLabel(objectParametersComposite, pluginParameterLabel);
				((GridData) configurationItemLabel.getLayoutData()).horizontalSpan=((GridLayout) objectParametersComposite.getLayout()).numColumns;
				break;
			case OBJECT_SELECTION:
				PluginParameterObjectSelection pluginParameterObjectSelection = (PluginParameterObjectSelection) pluginParameter;
				ConfigurationItemObjectSelection configurationItemObjectSelection = new ConfigurationItemObjectSelection(objectParametersComposite, pluginParameterObjectSelection);
				configurationItemObjectSelection.setObjectReferences(pluginConfigurationUi.getObjectsFromInputObjectsMap(pluginParameterObjectSelection.getInputObjectListName()));
				configurationItems.add(configurationItemObjectSelection);
				break;
			default:
				break;
			}
		}
	}
	
	private void createButtonsComposite() {
		Composite buttonsComposite  = new Composite(shell, SWT.NONE);
		buttonsComposite.setLayout(new GridLayout(2, false));
		buttonsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button okButton = new Button(buttonsComposite, SWT.PUSH);
		okButton.setText("OK");
		okButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true));
		okButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 8685716146386181669L;
			
			public void widgetSelected(SelectionEvent event) {
				Map<String, String> configurationValues = new HashMap<String, String>();
				
				if (!getDisplayNamesOfEmptyRequiredParameters().isEmpty()) {
					feedbackTextLabel.setText("Required parameters empty");
				} else {
					for (ConfigurationItemInterface configurationItem : configurationItems) {
						if (configurationItem.getValue()!=null) {
							configurationValues.put(configurationItem.getInternalName(), configurationItem.getValue());
						} else {
							configurationValues.put(configurationItem.getInternalName(), "");
						}
					}
					Gson gson = new Gson();
					configurationItemObjectList.addInputObjectInstance(gson.toJsonTree(configurationValues));
					shell.close();
				}
				
			}
		});
		
		Button cancel = new Button(buttonsComposite, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, true));
		cancel.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = -758572263013004367L;

			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
	}
	
	private List<String> getDisplayNamesOfEmptyRequiredParameters() {
		List<String> displayNames = new ArrayList<String>();

		for (ConfigurationItemInterface configurationItem : configurationItems) {
			if (configurationItem.getRequired() && (configurationItem.getValue()==null || configurationItem.getValue().equals(""))) {
				displayNames.add(configurationItem.getDisplayName());
			}
		}
		return displayNames;
	}
}
