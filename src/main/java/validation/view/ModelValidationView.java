package validation.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import validation.Messages;
import validation.ValidationErrorLevel;
import validation.utils.ImageLoader;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.IEntity;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEditUnit;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;

public class ModelValidationView extends JPanel implements IPluginExtraTabView, ProjectEventListener, PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(ModelValidationView.class);

    protected ModelValidationTable modelValidationTable;
    private boolean isDetected;
    private boolean hasFatalError;
    private int totalErrorLevel;
    private boolean isAutoValidation;
    private UpdateButton updateButton;
    private boolean isChangeTab = false;

    public ModelValidationView() {
        initComponents();
        ModelValidationViewLocator.getInstance().setModelValidationView(this);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(createToolBar(), BorderLayout.NORTH);
        add(createTablePane(), BorderLayout.CENTER);

        addProjectEventListener();

    }

    public void showError(){
        updateButton.setUpdatingIcon();

        modelValidationTable.updateTable();
        isDetected = modelValidationTable.getIsDetected();
        hasFatalError = modelValidationTable.getHasFatalError();
        totalErrorLevel = modelValidationTable.getTotalErrorLevel();

        updateButton.setBaseIcon();
    }

    public boolean isValidationDone(){
    	return updateButton.isDone();
    }
    
    public boolean isDetected() {
        return isDetected;
    }

    public boolean hasFatalError() {
        return hasFatalError;
    }

    public boolean isNoticeOnly() {
        return totalErrorLevel == ValidationErrorLevel.NOTICE.getLevel();
    }

    public void errorClear(){
    	modelValidationTable.clearTable();
    }
    
    private Container createToolBar() {
        final FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT, 0, 0);

        JToolBar toolbar = new JToolBar();
        toolbar.setLayout(flowLayout);
        toolbar.setFloatable(false);
        toolbar.setBorder(null);

        toolbar.add(new AutoUpdateButton());
        updateButton = new UpdateButton();
        toolbar.add(updateButton);
        return toolbar;
    }

    private Container createTablePane() {
        modelValidationTable = new ModelValidationTable();
        JScrollPane pane = new JScrollPane(modelValidationTable);
        return pane;
    }

    class AutoUpdateButton extends JToggleButton {
        private static final long serialVersionUID = -7154824125762961471L;
        static final String NAME = "autoUpdateButton";

        private AutoUpdateButton() {
            setName(NAME);
            setIcon(new ImageIcon(ImageLoader.getImage(this.getClass(), "autoUpdate.png")));
            setToolTipText(Messages.getMessage("model_validation_auto_update.tooltip"));
            setSelected(true);
            isAutoValidation = true;
            setFocusable(false);

            addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        isAutoValidation = true;
                        showError();
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        isAutoValidation = false;
                    }
                }
            });
        }
    }

    class UpdateButton extends JButton {
        private static final long serialVersionUID = -773401587104169310L;
        static final String NAME = "updateButton";

        private ModelValidationUpdater modelValidationUpdater;

        private ImageIcon baseIcon = new ImageIcon(
                ImageLoader.getImage(this.getClass(), "update.png"));
        private ImageIcon updatingIcon = new ImageIcon(
                ImageLoader.getImage(this.getClass(), "updating.gif"));

        private UpdateButton() {
            setName(NAME);
            setBaseIcon();
            setToolTipText(Messages.getMessage("model_validation_update.tooltip"));
            setFocusable(false);
            modelValidationUpdater = new ModelValidationUpdater();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    StateValue currentState = modelValidationUpdater.getState();
                    if (currentState.equals(StateValue.DONE)) {
                        modelValidationUpdater = new ModelValidationUpdater();
                    }

                    if (!currentState.equals(StateValue.STARTED)) {
                        modelValidationUpdater.execute();
                    }
                }
            });
        }

        public void setBaseIcon() {
            setIcon(baseIcon);
        }

        public void setUpdatingIcon() {
            setIcon(updatingIcon);
        }
        
        public boolean isDone(){
        	return modelValidationUpdater.getState().equals(StateValue.DONE);
        }
    }

    class ModelValidationUpdater extends SwingWorker<Object, Object> {
        @Override
        public Object doInBackground() {
            showError();
            return null;
        }
    }

    private void addProjectEventListener() {
        try {
            ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
            projectAccessor.addProjectEventListener(this);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void addSelectionListener(ISelectionListener arg0) {
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getDescription() {
        return Messages.getMessage("description");
    }

    @Override
    public String getTitle() {
        return Messages.getMessage("title");
    }

    @Override
    public void projectChanged(ProjectEvent e) {
        try {
            if ((isAutoValidation && isModelChanged(e)) 
                    || modelValidationTable.getIsQuickFixed()) {
                showError();
            }
        } catch (Exception ex) {
            // Systemがまだ作成されていない状態でprojectChangedがはしることがあるため、
            // Exceptionが発生してもスルーする
        }
    }
    
    public boolean isModelChanged(ProjectEvent e) {
        ProjectEditUnit[] projectEditUnits = e.getProjectEditUnit();
        if (projectEditUnits.length == 0) {
            return true;  //ex. merge
        }
        for (ProjectEditUnit projectEditUnit : projectEditUnits) {
            IEntity entity = projectEditUnit.getEntity();
            int operation = projectEditUnit.getOperation();
            if (operation != ProjectEditUnit.MODIFY ||
                    !(entity instanceof IPresentation)) {
                return true; // ADD/DELETEでは無条件に、MODIFYはプレゼンテーション以外でモデル検証をする
            }
        }
        return false;
    }

    @Override
    public void projectClosed(ProjectEvent e) {
        isChangeTab = false;
        modelValidationTable.clearTable();
    }

     @Override
    public void projectOpened(ProjectEvent e) {
    }

    @Override
    public void activated() {
        try {
            if (!isChangeTab && isAutoValidation) {
                showError();
            }
        } catch (Exception ex) {
            //新規作成の場合には、Systemがまだ作成されていないため、Exceptionが発生するが特に何もしない
        }
    }

    @Override
    public void deactivated() {
        isChangeTab = true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("documentName")) {
            if (!evt.getNewValue().equals("no_title")) {
                if (isAutoValidation) {
                    showError();
                }
            }
        }
    }
}
