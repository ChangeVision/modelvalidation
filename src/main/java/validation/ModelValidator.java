package validation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import validation.exceptions.ApplicationException;
import validation.view.ModelValidationViewLocator;

import com.change_vision.jude.api.inf.model.INamedElement;

public class ModelValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(ModelValidator.class);

	public List<ValidationError> validate() {

		List<ValidationError> errors = new ArrayList<ValidationError>();
		List<ValidationRuleManager> ruleManagers = ModelValidationViewLocator.getInstance()
				.getValidationRuleManagers();

		for (ValidationRuleManager ruleManager : ruleManagers) {
			List<INamedElement> targetModels = ruleManager.getTargetModels();
			List<ValidationRule> rules = ruleManager.getValidationRule();
			
			for (INamedElement namedElement : targetModels) {
				for (ValidationRule rule : rules) {
					try{
						if (rule.isTargetModel(namedElement)) {
							if (!rule.validate(namedElement)) {
								errors.addAll(rule.getResults());
							}
						}
					}catch(Exception e){
						logger.error("error occured in " + rule.getClass().getName(),e);
						throw new ApplicationException(e);
					}
				}
			}
		}

		return errors;
	}

}
