package validation.view;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import validation.QuickFix;
import validation.ValidationError;

public class QuickFixPopupMenuBuilder {
    private List<QuickFix> quickFixes;
    private ModelValidationTable modelValidationTable;

    public QuickFixPopupMenuBuilder() {
    }

    public JPopupMenu build(ModelValidationTable modelValidationTable) {
        this.modelValidationTable = modelValidationTable;
        ValidationError validationError = (ValidationError) modelValidationTable.getSelectedModel();
        this.quickFixes = validationError.getQuickFixes();
        if (quickFixes != null) {
            return createPopupMenu();
        }
        return null;
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        for (QuickFix quickFix : quickFixes) {
            Action quickFixAction =  new QuickFixAction(quickFix);
            JMenuItem quickFixMenu = new JMenuItem(quickFixAction);
            if (quickFix.isDisable()) {
                quickFixMenu.setEnabled(false);
            }
            popupMenu.add(quickFixMenu);
        }
        return popupMenu;
    }

    class QuickFixAction extends AbstractAction {
        private static final long serialVersionUID = 8410952775980289745L;
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
