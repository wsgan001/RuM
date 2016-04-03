package ee.ut.cs.rum.plugins.development.ui.input;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ee.ut.cs.rum.plugins.development.description.parameter.PluginParameterDouble;

public class ConfigurationItemDouble extends Text implements ConfigurationItemInterface {
	private static final long serialVersionUID = -6806863065839357763L;

	public ConfigurationItemDouble(Composite parent, PluginParameterDouble parameterDouble) {
		super(parent, SWT.BORDER);
		
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.setText(Double.toString(parameterDouble.getDefaultValue()));
		this.setToolTipText(parameterDouble.getDescription());
	}

	@Override
	public void setValue(String value) {
		this.setText(value);
	}

	@Override
	public String getValue() {
		return this.getText();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

}
