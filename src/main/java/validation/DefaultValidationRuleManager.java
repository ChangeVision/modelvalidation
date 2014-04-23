package validation;

import java.util.List;

public abstract class DefaultValidationRuleManager implements ValidationRuleManager {

    public boolean hasRule(Class<? extends ValidationRule> rule) {
    	List<ValidationRule> rules = getValidationRule();
        for (ValidationRule r: rules) {
            if (r.getClass() == rule) {
                return true;
            }
        }
        return false;
    }
}
