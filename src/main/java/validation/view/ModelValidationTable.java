package validation.view;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import validation.Messages;
import validation.ModelValidator;
import validation.ValidationError;
import validation.ValidationErrorLevel;
import validation.exceptions.ApplicationException;
import validation.utils.ModelValidationUtil;
import validation.utils.StateMachineTypeOf;
import validation.utils.TypeOf;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.model.IPseudostate;
import com.change_vision.jude.api.inf.model.IState;
import com.change_vision.jude.api.inf.model.IStateMachineDiagram;
import com.change_vision.jude.api.inf.model.ITransition;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.view.IProjectViewManager;
import com.change_vision.jude.api.inf.view.IconDescription;

public class ModelValidationTable extends JTable
implements MouseListener, KeyListener, ListSelectionListener {
    private static final long serialVersionUID = 1L;
    static final String NAME = "ModelValidationTable";
    
    protected ModelValidationTableModel tableModel;
    private PopupMenuBuilder popupMenuBuilder;
    private QuickFixPopupMenuBuilder quickFixPopupMenuBuilder;
    private boolean isDetected = false;
    private boolean isQuickFixed = false;
    private boolean hasFatalError = false;
    private int totalErrorLevel = 0;
    private RowSorter<ModelValidationTableModel> sorter;
    
    public ModelValidationTable() {
        initModelValidationTable();
    }

    private void initModelValidationTable() {
        tableModel = new ModelValidationTableModel();
        setModel(tableModel);

        ValidationErrorLevelCellRenderer render = new ValidationErrorLevelCellRenderer();
        setSorter();

        getColumn(TableHeader.DESCRIPTION.label).setPreferredWidth(300);
        getColumn(TableHeader.KIND.label).setCellRenderer(render);
        getColumn(TableHeader.ERRORLEVEL.label).setCellRenderer(render);
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getSelectionModel().addListSelectionListener(this);
        setHeader();
        addMouseListener(this);
        addKeyListener(this);

        this.setName(NAME);

        popupMenuBuilder = new PopupMenuBuilder();
        quickFixPopupMenuBuilder = new QuickFixPopupMenuBuilder();
    }

    @SuppressWarnings("serial")
    private void setHeader() {
        JTableHeader hdr = new JTableHeader(getColumnModel()) {
            @Override
            public String getToolTipText(MouseEvent e) {
                int c = columnAtPoint(e.getPoint());
                if (c == getColumnIndex(TableHeader.ERRORLEVEL.label)) {
                    String msg = Messages.getMessage("model_validation_tooltip.errorlevel",
                            (getClass().getResource(ValidationErrorLevel.CRITICAL.getIconName())).toString(),
                            (getClass().getResource(ValidationErrorLevel.ERROR.getIconName())).toString(),
                            (getClass().getResource(ValidationErrorLevel.WARNING.getIconName())).toString(),
                            (getClass().getResource(ValidationErrorLevel.NOTICE.getIconName())).toString());
                    return msg;
                }
                return null;
            }
        };
        setTableHeader(hdr);
        hdr.addMouseListener(this);
    }

    protected void updateTable() {
    	ModelValidator modelValidator = new ModelValidator();
        List<ValidationError> errors = modelValidator.validate();

        if (errors.size() == 0) {
            tableModel.setNumRows(0);
            isDetected = false;
            hasFatalError = false;
            totalErrorLevel = 0;
            return;
        }

        isDetected = true;
        hasFatalError = false;
        totalErrorLevel = 0;

        tableModel.setNumRows(errors.size());
        INamedElement target = null;
        for (int i = 0; errors.size() > i ; i++) {
            ValidationError error = (ValidationError) errors.get(i);
            target = error.getTarget();

            String category = error.getCategory();
            
            ModelType type = getType(target);
            String name = ModelValidationUtil.getName(target);
            String path = getPath(target);
            ValidationErrorLevel errorLevel = error.getErrorLevel();

            setValueAt(category,i,getColumnIndex(TableHeader.CATEGORY.label));
            setValueAt(error, i, getColumnIndex(TableHeader.DESCRIPTION.label));
            setValueAt(type, i, getColumnIndex(TableHeader.KIND.label));
            setValueAt(name, i, getColumnIndex(TableHeader.MODEL.label));
            setValueAt(path, i, getColumnIndex(TableHeader.PATH.label));
            setValueAt(errorLevel, i, getColumnIndex(TableHeader.ERRORLEVEL.label));

            totalErrorLevel += errorLevel.getLevel();
            if (errorLevel.equals(ValidationErrorLevel.CRITICAL))
                hasFatalError = true;
        }
        if (sorter != null) {
            List<? extends SortKey> sortKeys = sorter.getSortKeys();
            sorter.setSortKeys(null);
            if (sortKeys.size() > 0) {
                sorter.setSortKeys(sortKeys);
            }
        }
        clearSelection();
    }

    public void clearTable() {
        tableModel.setNumRows(0);
    }
    protected boolean getIsDetected() {
        return isDetected;
    }

    protected boolean getIsQuickFixed() {
        return isQuickFixed;
    }

    protected boolean getHasFatalError() {
        return hasFatalError;
    }

    protected int getTotalErrorLevel() {
        return totalErrorLevel;
    }

    public void setIsQuickFixed(boolean isQuickFixed) {
        this.isQuickFixed = isQuickFixed;
    }

    private ModelType getType(INamedElement target) {
        ModelType type = null;
        if (TypeOf.isPart(target)) {
            type = ModelType.PART;
        } else if (TypeOf.isBlock(target)) {
            type = ModelType.BLOCK;
        } else if (TypeOf.isPort(target)) {
            type = ModelType.PORT;
        } else if (TypeOf.isOperation(target)) {
            type = ModelType.OPERATION;
        } else if (TypeOf.isConnector(target)) {
            type = ModelType.CONNECTOR;
        } else if (TypeOf.isPackage(target)) {
            type = ModelType.PACKAGE;
            if (target.getOwner() == null) {
                type = ModelType.PROJECT;
            }
        } else if (TypeOf.isClass(target)) {
            if (TypeOf.isInterface(target)) {
                type = ModelType.INTERFACE;
            } else {
                type = ModelType.CLASS;
            }
        } else if (TypeOf.isAttribute(target)) {
            type = ((IAttribute)target).getAssociation() == null? ModelType.ATTRIBUTE: ModelType.ASSOCIATIONEND;
        } else if (target instanceof IParameter) {
            type = ModelType.PARAMETERS;
        } else if (target instanceof IStateMachineDiagram) {
            type = ModelType.STATECHARTDGM;
        } else if (target instanceof IState) {
            if (StateMachineTypeOf.isSubmachineState(target)) {
                type = ModelType.SUBMACHINESTATE;
            } else {
                type = ModelType.STATE;
            }
        } else if (StateMachineTypeOf.isInitialPseudostate(target)) {
            type = ModelType.INITIALSTATE;
        } else if (target instanceof ITransition) {
            type = ModelType.TRANSITION;
        } else if (target instanceof IPseudostate) {
            IPseudostate pseudostate = (IPseudostate) target;
            if (pseudostate.isShallowHistoryPseudostate()) {
                type = ModelType.SHALLOWHISTORY;
            } else if (pseudostate.isDeepHistoryPseudostate()) {
                type = ModelType.DEEPHISTORY;
            } else if (pseudostate.isForkPseudostate()) {
                type = ModelType.FORK;
            } else if (pseudostate.isChoicePseudostate()) {
                type = ModelType.CHOICE;
            } else if (pseudostate.isJoinPseudostate()) {
                type = ModelType.JOIN;
            } else if (pseudostate.isJunctionPseudostate()) {
                type = ModelType.JUNCTION;
            }
        } else if (target instanceof IDependency) {
            type = ModelType.DEPENDENCY;
        } else if (target instanceof IInternalBlockDiagram){
        	type = ModelType.INTERNAL_BLOCK_DIAGRAM;
        }
        return type;
    }

    private String getPath(INamedElement target) {
        String path = "";
        if (TypeOf.isPart(target)) {
                path = target.getFullNamespace("/");
        } else if (target instanceof IState ||
                target instanceof ITransition ||
                target instanceof IPseudostate) {
            try {
                IPresentation[] pss = target.getPresentations();
                if (pss != null && pss.length > 0) {
                    IDiagram dgm = pss[0].getDiagram();
                    if (dgm != null) {
                        path = dgm.getFullName("/");
                    }
                }
            } catch (InvalidUsingException e) {
                path = "";
            }
        } else if (target instanceof IDependency) {
            path = ((IDependency)target).getClient().getFullNamespace("/");
        } else {
            path = target.getFullNamespace("/");
        }
        if (!path.equals("")) {
            path = "/" + path;
        }
        return path;
    }

    private int getColumnIndex(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (getColumnName(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private void setSorter() {
        sorter = new TableRowSorter<ModelValidationTableModel>(tableModel);
        ((DefaultRowSorter<ModelValidationTableModel, Integer>) sorter).setComparator
        (getColumnIndex(TableHeader.CATEGORY.label), new Comparator<String>() {
            public int compare(String l1, String l2) {
                return l1.compareTo(l2);
            }
        });
        List<SortKey> s = new ArrayList<SortKey>();
        s.add(new RowSorter.SortKey(getColumnIndex(TableHeader.CATEGORY.label), SortOrder.ASCENDING));
        s.add(new RowSorter.SortKey(getColumnIndex(TableHeader.ERRORLEVEL.label), SortOrder.ASCENDING));
        s.add(new RowSorter.SortKey(getColumnIndex(TableHeader.DESCRIPTION.label), SortOrder.ASCENDING));
        s.add(new RowSorter.SortKey(getColumnIndex(TableHeader.KIND.label), SortOrder.ASCENDING));
        sorter.setSortKeys(s);
        setRowSorter(sorter);
    }

    class ModelValidationTableModel extends DefaultTableModel {
        private static final long serialVersionUID = -5577433572799978616L;

        ModelValidationTableModel() {
            super();
            setColumnIdentifiers(TableHeader.values());
        }

        public INamedElement getModel(int row) {
            Object model = getValueAt(row, getColumnIndex(TableHeader.MODEL.label));
            if (model instanceof INamedElement) {
                return (INamedElement) model;
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    enum TableHeader {
    	CATEGORY(Messages.getMessage("model_validation_table.category"),0),
        DESCRIPTION(Messages.getMessage("model_validation_table.description"), 1),
        KIND(Messages.getMessage("model_validation_table.kind"), 2),
        MODEL(Messages.getMessage("model_validation_table.model"), 3),
        PATH(Messages.getMessage("model_validation_table.path"), 4),
        ERRORLEVEL(Messages.getMessage("model_validation_table.errorlevel"), 5);

        String label = "";
        int index = 0;

        TableHeader(String label, int index) {
            this.label = label;
            this.index = index;
        }

        @Override
        public String toString() {
            return this.label;
        }
    }

    enum ModelType {
        PART(Messages.getMessage("model_validation_type.part"), IconDescription.UML_COMPOSITE_STRUCTURE_PART),
        BLOCK(Messages.getMessage("model_validation_type.block"), IconDescription.SYSML_BLOCK_BLOCK),
        PORT(Messages.getMessage("model_validation_type.port"), IconDescription.UML_COMPOSITE_STRUCTURE_PORT),
        CONNECTOR(Messages.getMessage("model_validation_type.connector"), IconDescription.UML_COMPOSITE_STRUCTURE_CONNECTOR),
        PACKAGE(Messages.getMessage("model_validation_type.package"), IconDescription.PACKAGE),
        CLASS(Messages.getMessage("model_validation_type.class"), IconDescription.UML_CLASS_CLASS),
        INTERFACE(Messages.getMessage("model_validation_type.interface"), IconDescription.UML_CLASS_INTERFACE),
        OPERATION(Messages.getMessage("model_validation_type.operation"), IconDescription.UML_CLASS_OPE),
        ATTRIBUTE(Messages.getMessage("model_validation_type.attribute"), IconDescription.UML_CLASS_ATTR),
        PARAMETERS(Messages.getMessage("model_validation_type.parameters"), IconDescription.UML_CLASS_OPE),
        PROJECT(Messages.getMessage("model_validation_type.project"), IconDescription.PROJECT),
        STATECHARTDGM(Messages.getMessage("model_validation_type.statechartdgm"), IconDescription.UML_DGM_STATECHART),
        STATE(Messages.getMessage("model_validation_type.state"), IconDescription.UML_STATECHART_STATE),
        SUBMACHINESTATE(Messages.getMessage("model_validation_type.submachinestate"),IconDescription.UML_STATECHART_SUBSTATE),
        INITIALSTATE(Messages.getMessage("model_validation_type.initialstate"), IconDescription.UML_STATECHART_INITIALSTATE),
        TRANSITION(Messages.getMessage("model_validation_type.transition"), IconDescription.UML_STATECHART_TRANSITION),
        SHALLOWHISTORY(Messages.getMessage("model_validation_type.shallow_history"), IconDescription.UML_STATECHART_SHALLOWHISTORY),
        DEEPHISTORY(Messages.getMessage("model_validation_type.deep_history"), IconDescription.UML_STATECHART_DEEPHISTORY),
        CHOICE(Messages.getMessage("model_validation_type.choice"), IconDescription.UML_STATECHART_CHOICE),
        FORK(Messages.getMessage("model_validation_type.fork"), IconDescription.UML_STATECHART_FORK),
        JOIN(Messages.getMessage("model_validation_type.join"), IconDescription.UML_STATECHART_JOIN),
        JUNCTION(Messages.getMessage("model_validation_type.junction"), IconDescription.UML_STATECHART_JUNCTIONPOINT),
        DEPENDENCY(Messages.getMessage("model_validation_type.dependency"), IconDescription.UML_CLASS_DEPENDENCY),
        ASSOCIATIONEND(Messages.getMessage("model_validation_type.association_end"), IconDescription.UML_CLASS_ASSOCATION), 
        INTERNAL_BLOCK_DIAGRAM(Messages.getMessage("model_validation_type.ibd"), IconDescription.SYSML_INTERNALBLOCK_DIAGRAM);

        String label = "";
        IconDescription iconDescription;

        ModelType(String label, IconDescription iconDescription) {
            this.label = label;
            this.iconDescription = iconDescription;
        }

        @Override
        public String toString() {
            return this.label;
        }

        public IconDescription getIconDescription() {
            return this.iconDescription;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);

        Object obj = getSelectedModel();
        if (obj == null) {
            return;
        }
        INamedElement target = ((ValidationError) obj).getTarget();
        if (target != null) {
            updatePropertyView((INamedElement) target);
        }
    }
    
    private void updatePropertyView(INamedElement target) {
        IProjectViewManager projectViewManager;
		try {
			projectViewManager = AstahAPI.getAstahAPI().getViewManager().getProjectViewManager();
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(e);
		} catch (InvalidUsingException e) {
			throw new ApplicationException(e);
		}
        projectViewManager.showInPropertyView(target);
    }

    public Object getSelectedModel() {
        Object obj = null;
        int selectedRow = getSelectedRow();
        if (selectedRow == -1) {
            return null;
        } else {
            obj = getValueAt(selectedRow, getColumnIndex(TableHeader.DESCRIPTION.label));
        }
        return obj;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            Object selectedModel = getSelectedModel();
            ValidationError error = (ValidationError) selectedModel;
            if (error == null) {
                return;
            }

            IPresentation[] presentations = ModelValidationUtil.getPresentations(error.getTarget());
            if (presentations.length > 0) {
                ModelValidationUtil.showInDiagramEditor(presentations[0]);
            }
            return;
        } else if (e.getClickCount() > 1) {
            return;
        }

        showPopupMenu(e);
    }

    private void showPopupMenu(MouseEvent e) {
        Object obj = e.getSource();
        if (obj instanceof JTableHeader) {
            return;
        }

        if (e.isPopupTrigger()) {
            int y = e.getPoint().y;
            int index = y/getRowHeight();
            setRowSelectionInterval(index, index);

            JPopupMenu popupMenu = popupMenuBuilder.build(this);
            if (popupMenu == null) {
                return;
            }
            popupMenu.show((JComponent) e.getSource(), e.getX(), e.getY());
        }
    }

    private void showQuickFixPopupMenu(KeyEvent e) {
        Object selectedModel = getSelectedModel();
        if (selectedModel == null) {
            return;
        }

        JPopupMenu popupMenu = quickFixPopupMenuBuilder.build(this);
        if (popupMenu == null) {
            return;
        }

        int height = getRowHeight();
        int selectedRow = getSelectedRow();
        popupMenu.show((JComponent) e.getSource(), 0, height * selectedRow);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        showPopupMenu(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopupMenu(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_1)) {
            return;
        }
        showQuickFixPopupMenu(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private class ValidationErrorLevelCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int typeColumn = table.getColumnModel().getColumnIndex(TableHeader.KIND.label);
            int levelColumn = table.getColumnModel().getColumnIndex(TableHeader.ERRORLEVEL.label);
            if (column == levelColumn || column == typeColumn) {
                this.setIcon(getIcon(value));
            } else {
                this.setIcon(null);
            }
            return this;
        }

        private Icon getIcon(Object value) {
            if (value instanceof ValidationErrorLevel) {
            	return ((ValidationErrorLevel)value).getIcon();
            } else if (value instanceof ModelType) {
            	IconDescription desc = ((ModelType)value).getIconDescription();
            	return ModelValidationUtil.getIcon(desc);
            }
            
            return null;
        }
    }

}
