package validation;

import java.util.List;

import com.change_vision.jude.api.inf.model.INamedElement;

public interface ValidationRule {

    boolean validate(INamedElement target);

    boolean isTargetModel(INamedElement target);

    List<ValidationError> getResults();

    List<QuickFix> getQuickFixes(INamedElement target);

    ValidationErrorLevel getErrorLevel();

    String getHelpPath();

}
