package validation.view;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import validation.Messages;
import validation.QuickFix;
import validation.ValidationError;
import validation.utils.ImageLoader;
import validation.utils.ModelValidationUtil;
import validation.utils.TypeOf;

import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.change_vision.jude.api.inf.model.ICompositeStructureDiagram;
import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IMindMapDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;
import com.change_vision.jude.api.inf.model.IStateMachineDiagram;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.view.IconDescription;

public class PopupMenuBuilder {
    private ValidationError error;
    private INamedElement target;
    private List<QuickFix> quickFixes;
    private ModelValidationTable modelValidationTable;

    public PopupMenuBuilder() {
    }

    public JPopupMenu build(ModelValidationTable modelValidationTable) {
        this.modelValidationTable = modelValidationTable;
        this.error = (ValidationError) modelValidationTable.getSelectedModel();
        this.target = this.error.getTarget();
        this.quickFixes = this.error.getQuickFixes();

        JPopupMenu popupMenu = createPopupMenu();
        return popupMenu;
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        createShowInDiagramMenu(popupMenu);
        createShowInStructureTree(popupMenu);
        createQuickFixMenu(popupMenu);
        return popupMenu;
    }

    private JPopupMenu createShowInDiagramMenu(JPopupMenu popupMenu) {
        JMenu showInDiagramMenu = new JMenu(
                Messages.getMessage("model_validation_popup.show_in_diagram"));
        setShowInDiagramMenu(showInDiagramMenu);
        popupMenu.add(showInDiagramMenu);

        return popupMenu;
    }

    private void createShowInStructureTree(JPopupMenu popupMenu) {
        Action showInStructureTreeAction = new ShowInStructureTreeAction(error.getTarget());
        JMenuItem showInStructureTree = new JMenuItem(showInStructureTreeAction);
        if (target instanceof IConnector
                || target instanceof IDependency) {
            showInStructureTree.setEnabled(false);
        }

        popupMenu.add(showInStructureTree);
    }

    class ShowInStructureTreeAction extends AbstractAction {
        private static final long serialVersionUID = 8030649457669406597L;
        INamedElement target;

        public ShowInStructureTreeAction(INamedElement target) {
            super(Messages.getMessage("model_validation_popup.show_in_structure_tree"));
            this.target = target;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            ModelValidationUtil.showInStructureTree(target);
        }
    }

    private void createQuickFixMenu(JPopupMenu popupMenu) {
        JMenu quickFixMenu = new JMenu(
                Messages.getMessage("model_validation_popup.quick_fix"));
        if (quickFixes == null) {
            quickFixMenu.setEnabled(false);
        } else {
            setQuickFixSubMenu(quickFixMenu);
        }

        popupMenu.add(quickFixMenu);
    }

    private void setShowInDiagramMenu(JMenu showInDiagramMenu) {
        IPresentation[] presentations;
        List<INamedElement> relatedDiagramTargetList = this.error.getRelatedDiagramTargetList();
        if (relatedDiagramTargetList != null) {
            List<IPresentation> presentationList = new ArrayList<IPresentation>();
            for (INamedElement relatedDiagramTarget : relatedDiagramTargetList) {
                IPresentation[] pres = ModelValidationUtil.getPresentations(relatedDiagramTarget);
                if (pres.length > 0) {
                    presentationList.addAll(Arrays.asList(pres));
                }
            }
            presentations = (IPresentation[])presentationList.toArray(new IPresentation[0]);
        } else {
            presentations = ModelValidationUtil.getPresentations(target);
        }

        if (presentations.length == 0) {
            showInDiagramMenu.setEnabled(false);
            return;
        }

        for (IPresentation presentation : presentations) {
            Action showInDiagramAction = new ShowInDiagramAction(presentation);
            JMenuItem showInDiagramSubMenu = new JMenuItem(showInDiagramAction);
            showInDiagramSubMenu.setIcon(getIcon(presentation));
            showInDiagramMenu.add(showInDiagramSubMenu);
        }
    }

    private Icon getIcon(IPresentation presentation) {
        Icon icon = null;
        IDiagram diagram = presentation.getDiagram();
        if (diagram instanceof IClassDiagram) {
            icon = ModelValidationUtil.getIcon(IconDescription.UML_DGM_CLASS);
        } else if (diagram instanceof IStateMachineDiagram) {
            icon = ModelValidationUtil.getIcon(IconDescription.UML_DGM_STATECHART);
        } else if (diagram instanceof IMindMapDiagram) {
            icon = ModelValidationUtil.getIcon(IconDescription.UML_MINDMAP_DGM);
        } else if (diagram instanceof ICompositeStructureDiagram) {
            icon = new ImageIcon(ImageLoader.getImage(this.getClass(), "SystemStructureDiagram.gif"));
        }
        return icon;
    }

    static class ShowInDiagramAction extends AbstractAction {
        private static final long serialVersionUID = -5212909741239007688L;
        private IPresentation presentation;

        public ShowInDiagramAction(IPresentation presentation) {
            super(getDiagramName(presentation));
            this.presentation = presentation;
        }

        private static String getDiagramName(IPresentation presentation) {
            IElement model = presentation.getModel();
            if (TypeOf.isPart((INamedElement) model)
                    || (model instanceof IPort)) {
                String name = ModelValidationUtil.getName((INamedElement) model)
                        + " - " + presentation.getDiagram().getName();
                return name;
            } else {
                return presentation.getDiagram().getName();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ModelValidationUtil.showInDiagramEditor(presentation);
        }
    }

    private void setQuickFixSubMenu(JMenu quickFixMenu) {
        for (QuickFix quickFix : quickFixes) {
            Action quickFixAction =  new QuickFixAction(quickFix);
            JMenuItem quickFixSubMenu = new JMenuItem(quickFixAction);
            if (quickFix.isDisable()) {
                quickFixSubMenu.setEnabled(false);
            }
            quickFixMenu.add(quickFixSubMenu);
        }
    }

    class QuickFixAction extends AbstractAction {
        private static final long serialVersionUID = -4558576093894034157L;
        private QuickFix quickFix;

        public QuickFixAction(QuickFix quickFix) {
            super(quickFix.getLabel());
            this.quickFix = quickFix;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            modelValidationTable.setIsQuickFixed(true);
            quickFix.fix();
            modelValidationTable.setIsQuickFixed(false);
        }
    }

}
