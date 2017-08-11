package ee.ut.cs.rum.plugins.development.description.parameter;

import com.google.gson.JsonParseException;

public class PluginParameterDouble extends PluginParameter {
	private Double defaultValue;
	private Double minValue;
	private Double maxValue;
	private Integer decimalPlaces;
	
	public PluginParameterDouble() {
		super();
		super.setParameterType(PluginParameterType.DOUBLE);
	}

	public Double getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Double defaultValue) {
		if (defaultValue==null) {
			throw new JsonParseException(this.getClass().getSimpleName() + " - defaultValue can not be empty");
		}
		this.defaultValue = defaultValue;
	}
	
	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Integer getDecimalPlaces() {
		return decimalPlaces;
	}

	public void setDecimalPlaces(Integer decimalPlaces) {
		if (decimalPlaces==null || !(decimalPlaces>0)) {
			throw new JsonParseException(this.getClass().getSimpleName() + " - decimalPlaces must be > 0");
		}
		this.decimalPlaces = decimalPlaces;
	}

	@Override
	public String toString() {
		return "PluginParameterDouble [minValue=" + minValue + ", maxValue=" + maxValue + ", defaultValue="
				+ defaultValue + ", decimalPlaces=" + decimalPlaces + "]";
	}
}
