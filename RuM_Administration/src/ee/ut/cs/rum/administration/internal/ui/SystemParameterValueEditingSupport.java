package ee.ut.cs.rum.administration.internal.ui;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import ee.ut.cs.rum.administration.internal.util.SystemParametersData;
import ee.ut.cs.rum.database.domain.SystemParameter;

public class SystemParameterValueEditingSupport extends EditingSupport {
	private static final long serialVersionUID = -5172543737241228835L;
	
	private final TableViewer viewer;
	private final CellEditor editor;

	public SystemParameterValueEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(viewer.getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		String value = ((SystemParameter) element).getValue();
		if (value != null) {
			return value;
		} else {
			return "";
		}
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		boolean setValueSuccess = SystemParametersData.updateParameterValue(((SystemParameter) element).getName(), String.valueOf(userInputValue));
		if (setValueSuccess) {
			((SystemParameter) element).setValue(String.valueOf(userInputValue));
			viewer.update(element, null);
		}
	}

}