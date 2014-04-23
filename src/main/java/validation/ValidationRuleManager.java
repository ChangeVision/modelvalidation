package validation;

import java.util.List;

import com.change_vision.jude.api.inf.model.INamedElement;

public interface ValidationRuleManager {

    List<ValidationRule> getValidationRule();
    
    List<INamedElement> getTargetModels();

}
