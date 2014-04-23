package validation;

import java.util.ArrayList;
import java.util.List;

import validation.view.ModelValidationViewLocator;

import com.change_vision.jude.api.inf.model.INamedElement;

public class ModelValidator {

	public List<ValidationError> validate() {

		List<ValidationError> errors = new ArrayList<ValidationError>();
		List<ValidationRuleManager> ruleManagers = ModelValidationViewLocator.getInstance()
				.getValidationRuleManagers();

		try{
		for (ValidationRuleManager ruleManager : ruleManagers) {
			List<INamedElement> targetModels = ruleManager.getTargetModels();
			List<ValidationRule> rules = ruleManager.getValidationRule();
			
			for (INamedElement namedElement : targetModels) {
				for (ValidationRule rule : rules) {
					if (rule.isTargetModel(namedElement)) {
						if (!rule.validate(namedElement)) {
							errors.addAll(rule.getResults());
						}
					}
				}
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}

		return errors;
	}

}
