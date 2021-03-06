package ee.ut.cs.rum.plugins.configuration.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ee.ut.cs.rum.controller.RumController;
import ee.ut.cs.rum.database.domain.SubTaskDependency;
import ee.ut.cs.rum.database.domain.UserFile;
import ee.ut.cs.rum.database.domain.UserFileType;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemDouble;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemFile;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemInteger;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemInterface;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemLabel;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemObjectList;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemObjectSelection;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemSelection;
import ee.ut.cs.rum.plugins.configuration.internal.ui.ConfigurationItemString;
import ee.ut.cs.rum.plugins.development.description.PluginInfo;
import ee.ut.cs.rum.plugins.development.description.PluginInputObject;
import ee.ut.cs.rum.plugins.development.description.PluginOutput;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameter;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterDouble;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterFile;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterInteger;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterLabel;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterObjectList;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterObjectSelection;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterSelection;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterString;
import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterType;

public class PluginConfigurationUi extends Composite {
	private static final long serialVersionUID = -5475837154117723386L;

	private RumController rumController;
	private PluginConfigurationContainer pluginConfigurationContainer;

	private Map<String, PluginInputObject> pluginInputObjectsMap;
	private List<ConfigurationItemInterface> configurationItems;
	private List<ConfigurationItemFile> configurationItemFiles;
	private List<UserFile> userFiles;
	private List<UserFile> taskUserFiles;
	private List<UserFile> tmpUserFiles;
	private List<UserFile> outputUserFiles;
	private Map<String, List<ConfigurationItemObjectSelection>> configurationItemObjectSelections;
	private Map<String, Map<Integer, JsonObject>> inputObjectsMap;

	public PluginConfigurationUi(PluginConfigurationContainer pluginConfigurationContainer, PluginInfo pluginInfo) {
		super(pluginConfigurationContainer, SWT.NONE);

		this.setEnabled(false);
		this.setLayout();
		this.configurationItemObjectSelections = new HashMap<String, List<ConfigurationItemObjectSelection>>(); 
		initPluginInputObjectsMap(pluginInfo);
		createContents(pluginInfo);
	}

	public PluginConfigurationUi(PluginConfigurationContainer pluginConfigurationContainer, PluginInfo pluginInfo, RumController rumController, List<UserFile> userFiles, List<UserFile> taskUserFiles, List<UserFile> tmpUserFiles) {
		super(pluginConfigurationContainer, SWT.NONE);

		this.pluginConfigurationContainer=pluginConfigurationContainer;

		this.rumController=rumController;
		this.userFiles=userFiles;
		this.taskUserFiles=taskUserFiles;
		this.tmpUserFiles=tmpUserFiles;

		this.configurationItemObjectSelections = new HashMap<String, List<ConfigurationItemObjectSelection>>(); 
		this.inputObjectsMap  = new HashMap<String, Map<Integer, JsonObject>>();
		initPluginInputObjectsMap(pluginInfo);

		this.outputUserFiles = new ArrayList<UserFile>();
		for (PluginOutput pluginOutput : pluginInfo.getOutputs()) {
			UserFile userFile = new UserFile();
			userFile.setOriginalFilename(pluginOutput.getFileName());
			List<UserFileType> userFileTypes = new ArrayList<UserFileType>();
			for (String fileType : pluginOutput.getFileTypes()) {
				UserFileType userFileType = new UserFileType();
				userFileType.setTypeName(fileType);
				userFileTypes.add(userFileType);
			}
			userFile.setUserFileTypes(userFileTypes);
			this.outputUserFiles.add(userFile);
		}

		this.setLayout();
		this.createContents(pluginInfo);
	}

	private void initPluginInputObjectsMap(PluginInfo pluginInfo) {
		this.pluginInputObjectsMap = new HashMap<String, PluginInputObject>();
		for (PluginInputObject pluginInputObject : pluginInfo.getInputObjects()) {
			pluginInputObjectsMap.put(pluginInputObject.getName(), pluginInputObject);
		}
	}

	private void setLayout() {
		GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
		this.setLayout(layout);
	}

	private void createContents(PluginInfo pluginInfo) {
		Label label;
		configurationItems = new ArrayList<ConfigurationItemInterface>();
		configurationItemFiles = new ArrayList<ConfigurationItemFile>();

		label = new Label (this, SWT.NONE);
		label.setText("Plugin description:");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

		label = new Label (this, SWT.NONE);
		label.setText(pluginInfo.getDescription());
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		label = new Label (this, SWT.NONE);
		label.setText("Plugin configuration:");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

		label = new Label (this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		for (PluginParameter pluginParameter : pluginInfo.getParameters()) {

			if (!pluginParameter.getParameterType().equals(PluginParameterType.LABEL)) {
				label = new Label (this, SWT.NONE);
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
				configurationItems.add(new ConfigurationItemString(this, parameterString));
				break; 
			case INTEGER:
				PluginParameterInteger parameterInteger = (PluginParameterInteger) pluginParameter;
				configurationItems.add(new ConfigurationItemInteger(this, parameterInteger));
				break; 
			case DOUBLE:
				PluginParameterDouble parameterDouble = (PluginParameterDouble) pluginParameter;
				configurationItems.add(new ConfigurationItemDouble(this, parameterDouble));
				break; 
			case SELECTION:
				PluginParameterSelection parameterSelection = (PluginParameterSelection) pluginParameter;
				configurationItems.add(new ConfigurationItemSelection(this, parameterSelection));
				break; 
			case FILE:
				PluginParameterFile parameterFile = (PluginParameterFile) pluginParameter;
				configurationItemFiles.add(new ConfigurationItemFile(this, parameterFile, rumController));
				break;
			case OBJECT_LIST:
				PluginParameterObjectList parameterObjectList = (PluginParameterObjectList) pluginParameter;
				configurationItems.add(new ConfigurationItemObjectList(this, parameterObjectList, pluginInputObjectsMap.get(parameterObjectList.getInputObjectName())));
				if (inputObjectsMap!=null) {
					if (inputObjectsMap.get(parameterObjectList.getInternalName())==null) {
						inputObjectsMap.put(parameterObjectList.getInternalName(), new HashMap<Integer, JsonObject>());
					}					
				}
				break;
			case OBJECT_SELECTION:
				PluginParameterObjectSelection pluginParameterObjectSelection = (PluginParameterObjectSelection) pluginParameter;
				ConfigurationItemObjectSelection configurationItemObjectSelection = new ConfigurationItemObjectSelection(this, pluginParameterObjectSelection);
				configurationItems.add(configurationItemObjectSelection);
				if (configurationItemObjectSelections.get(pluginParameterObjectSelection.getInputObjectListName())==null) {
					configurationItemObjectSelections.put(pluginParameterObjectSelection.getInputObjectListName(), new ArrayList<ConfigurationItemObjectSelection>());
				}
				configurationItemObjectSelections.get(pluginParameterObjectSelection.getInputObjectListName()).add(configurationItemObjectSelection);
				break;
			case LABEL:
				PluginParameterLabel pluginParameterLabel = (PluginParameterLabel) pluginParameter;
				//This is not an actual configurationItem, therefore it should not be added to configurationItems list
				ConfigurationItemLabel configurationItemLabel = new ConfigurationItemLabel(this, pluginParameterLabel);
				((GridData) configurationItemLabel.getLayoutData()).horizontalSpan=((GridLayout) this.getLayout()).numColumns;
				break;
			default:
				break;
			}
		}

		label = new Label (this, SWT.NONE);
		label.setText("Plugin outputs:");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

		label = new Label (this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		for (PluginOutput pluginOutput : pluginInfo.getOutputs()) {
			label = new Label (this, SWT.NONE);
			label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

			String fileTypeString = "";
			for (String fileType : pluginOutput.getFileTypes()) {
				fileTypeString+=fileType+", ";
			}
			if (!fileTypeString.isEmpty()) {
				fileTypeString=fileTypeString.substring(0, fileTypeString.length()-2);
			}

			label = new Label (this, SWT.NONE);
			label.setText(pluginOutput.getFileName() + " (" + fileTypeString + ")");
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
	}

	public List<String> getDisplayNamesOfEmptyRequiredParameters() {
		List<String> displayNames = new ArrayList<String>();

		for (ConfigurationItemInterface configurationItem : configurationItems) {
			if (configurationItem.getRequired() && (configurationItem.getValue()==null || configurationItem.getValue().equals(""))) {
				displayNames.add(configurationItem.getDisplayName());
			}
		}
		for (ConfigurationItemFile configurationItemFile : configurationItemFiles) {
			if (configurationItemFile.getRequired() && configurationItemFile.getValue()==null) {
				displayNames.add(configurationItemFile.getDisplayName());
			}
		}
		return displayNames;
	}

	public String getConfigurationValuesString() {
		Map<String, Object> configurationValues = new HashMap<String, Object>();
		for (ConfigurationItemInterface configurationItem : configurationItems) {
			if (configurationItem.getValue()!=null) {
				if (configurationItem.getClass()==ConfigurationItemObjectList.class) {
					JsonParser jsonParser = new JsonParser();
					configurationValues.put(configurationItem.getInternalName(), jsonParser.parse(configurationItem.getValue()));
				} else {
					configurationValues.put(configurationItem.getInternalName(), configurationItem.getValue());
				}
			} else {
				configurationValues.put(configurationItem.getInternalName(), "");
			}
		}

		for (ConfigurationItemFile configurationItemFile : configurationItemFiles) {
			if (configurationItemFile.getValue()!=null) {
				configurationValues.put(configurationItemFile.getInternalName(), configurationItemFile.getValue());
			} else {
				configurationValues.put(configurationItemFile.getInternalName(), "");
			}
		}

		Gson gson = new Gson();
		String configurationValuesString = gson.toJson(configurationValues);
		return configurationValuesString;
	}

	public List<SubTaskDependency> getDependsOn() {
		List<SubTaskDependency> dependsOn = new ArrayList<SubTaskDependency>();
		for (ConfigurationItemFile configurationItemFile : configurationItemFiles) {
			if (configurationItemFile.getValue().equals("") && configurationItemFile.getDependsOnFile()!=null) {
				SubTaskDependency subTaskDependency = new SubTaskDependency();
				subTaskDependency.setRequiredForParameter(configurationItemFile.getInternalName());
				subTaskDependency.setFileName(configurationItemFile.getDependsOnFile().getOriginalFilename());
				subTaskDependency.setFulfilledBySubTask(configurationItemFile.getDependsOnFile().getSubTask());
				dependsOn.add(subTaskDependency);
			}
		}
		return dependsOn;
	}

	public void setConfigurationValues(Map<String, String> configurationValues) {
		for (ConfigurationItemInterface configurationItem : configurationItems) {
			if (configurationValues.get(configurationItem.getInternalName())!=null) {
				if (configurationItem.getClass()==ConfigurationItemObjectList.class) {
					Gson gson = new Gson();
					String valueString = gson.toJson(configurationValues.get(configurationItem.getInternalName()));
					configurationItem.setValue(valueString);
				} else {					
					configurationItem.setValue(configurationValues.get(configurationItem.getInternalName()));				
				}
			}
		}
		for (ConfigurationItemFile configurationItemFile : configurationItemFiles) {
			if (configurationValues.get(configurationItemFile.getInternalName())!=null) {
				configurationItemFile.setValue(configurationValues.get(configurationItemFile.getInternalName()));				
			}
		}
	}

	public List<UserFile> getUserFiles() {
		return userFiles;
	}

	public List<UserFile> getTaskUserFiles() {
		return taskUserFiles;
	}

	public List<UserFile> getTmpUserFiles() {
		return tmpUserFiles;
	}

	public List<UserFile> getOutputUserFiles() {
		return outputUserFiles;
	}

	public List<ConfigurationItemFile> getConfigurationItemFiles() {
		return configurationItemFiles;
	}

	public PluginConfigurationContainer getPluginConfigurationContainer() {
		return pluginConfigurationContainer;
	}

	public void notifySubTaskOfPluginSelect(List<UserFile> outputFiles) {
		if (outputUserFiles!=outputFiles) {
			for (ConfigurationItemFile configurationItemFile : configurationItemFiles) {
				configurationItemFile.notifyFileParameterOfPluginSelect(outputFiles);
			}			
		}
	}

	public void notifySubTaskOfPluginDeselect(List<UserFile> outputFiles) {
		if (outputUserFiles!=outputFiles) {
			for (ConfigurationItemFile configurationItemFile : configurationItemFiles) {
				configurationItemFile.notifyFileParameterOfPluginDeselect(outputFiles);
			}			
		}
	}

	public void notifySubTaskOfSubTaskNameChange(List<UserFile> outputFiles) {
		if (outputUserFiles!=outputFiles) {
			for (ConfigurationItemFile configurationItemFile : configurationItemFiles) {
				configurationItemFile.notifyFileParameterOfSubTaskNameChange(outputFiles);
			}			
		}
	}

	public void notifySubTaskOfTmpFileUpload(UserFile tmpUserFile) {
		for (ConfigurationItemFile configurationItemFile : configurationItemFiles) {
			configurationItemFile.notifyFileParameterOfTmpFileUpload(tmpUserFile);
		}
	}

	public void inputObjectInstanceAddedNotify(String parameterInternalName, JsonObject inputObjectInstance) {
		inputObjectsMap.get(parameterInternalName).put(inputObjectInstance.get("id").getAsInt(), inputObjectInstance);
		if (configurationItemObjectSelections.get(parameterInternalName)!=null) {
			for (ConfigurationItemObjectSelection configurationItemObjectSelection : configurationItemObjectSelections.get(parameterInternalName)) {
				configurationItemObjectSelection.addObjectReference(inputObjectInstance);
			}
		}
	}

	public void inputObjectInstanceRemovedNotify(String parameterInternalName, int id) {
		inputObjectsMap.get(parameterInternalName).remove(id);
		if (configurationItemObjectSelections.get(parameterInternalName)!=null) {
			for (ConfigurationItemObjectSelection configurationItemObjectSelection : configurationItemObjectSelections.get(parameterInternalName)) {
				configurationItemObjectSelection.removeObjectReference(id);
			}
		}
	}
	
	public Map<Integer, JsonObject> getObjectsFromInputObjectsMap(String parameterInternalName) {
		return inputObjectsMap.get(parameterInternalName);
	}

	public void addUserFile(UserFile userFile) {
		for (ConfigurationItemFile configurationItemFile : configurationItemFiles) {
			configurationItemFile.addUserFile(userFile);
		}
	}

	public void modifyUserFile(UserFile userFile) {
		for (ConfigurationItemFile configurationItemFile : configurationItemFiles) {
			configurationItemFile.modifyUserFile(userFile);
		}
	}

	public void removeUserFile(UserFile userFile) {
		for (ConfigurationItemFile configurationItemFile : configurationItemFiles) {
			configurationItemFile.removeUserFile(userFile);
		}
	}

}
