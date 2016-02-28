package ee.ut.cs.rum.plugins.internal.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ee.ut.cs.rum.database.domain.Plugin;
import ee.ut.cs.rum.plugins.internal.util.PluginsData;
import ee.ut.cs.rum.plugins.ui.PluginsManagementUI;

public class PluginsTableViewer extends TableViewer {
	private static final long serialVersionUID = -2085870762932626509L;

	public PluginsTableViewer(OverviewTabContents overviewTabContents, PluginsManagementUI pluginsManagementUI) {
		super(overviewTabContents, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		
		createColumns(this, pluginsManagementUI);
		
		final Table table = this.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		this.setContentProvider(new ArrayContentProvider());
		this.setInput(PluginsData.getPluginsDataFromDb());
	}
	
	
	private static void createColumns(final TableViewer viewer, PluginsManagementUI pluginsManagementUI) {
		String[] titles = { "Name", "Description", "Details"};
		int[] bounds = { 200, 400, 100 };

		TableViewerColumn nameColumn = createTableViewerColumn(titles[0], bounds[0], viewer);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = 5872575516853111364L;

			@Override
			public String getText(Object element) {
				Plugin plugin = (Plugin) element;
				return plugin.getName();
			}
		});

		TableViewerColumn descriptionColumn = createTableViewerColumn(titles[1], bounds[1], viewer);
		descriptionColumn.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = 859768103676685673L;

			@Override
			public String getText(Object element) {
				Plugin plugin = (Plugin) element;
				return plugin.getDescription();
			}
		});

		TableViewerColumn detailsButtonColumn = createTableViewerColumn(titles[2], bounds[2], viewer);
		detailsButtonColumn.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = -8762829711174270692L;
			
			//TODO: The buttons are probably not disposed properly
			Map<Object, PluginDetailsButton> pluginDetailsButtons = new HashMap<Object, PluginDetailsButton>();

			@Override
			public void update(ViewerCell cell) {
				TableItem item = (TableItem) cell.getItem();
				PluginDetailsButton pluginDetailsButton;
				if(pluginDetailsButtons.containsKey(cell.getElement())) {
					pluginDetailsButton = pluginDetailsButtons.get(cell.getElement());
				}
				else {
					Plugin plugin = (Plugin) cell.getElement();
					pluginDetailsButton = new PluginDetailsButton((Composite) cell.getViewerRow().getControl(), plugin.getId(), pluginsManagementUI);
					pluginDetailsButtons.put(cell.getElement(), pluginDetailsButton);
				}
				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal  = true;
				editor.grabVertical = true;
				editor.setEditor(pluginDetailsButton , item, cell.getColumnIndex());
				editor.layout();
			}
		});

	}

	private static TableViewerColumn createTableViewerColumn(String title, int bound, final TableViewer viewer) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		return viewerColumn;
	}

}
