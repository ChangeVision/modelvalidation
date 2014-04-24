package validation;

import java.util.ArrayList;
import java.util.List;

import validation.exceptions.ApplicationException;

import com.change_vision.jude.api.inf.model.INamedElement;

public abstract class DefaultValidationRule implements ValidationRule {

    private List<ValidationError> results = new ArrayList<ValidationError>();

    public void setResult(ValidationError result) {
        results.clear();
        results.add(result);
    }

    @Override
    public List<ValidationError> getResults() {
        return results;
    }

    @Override
    public boolean validate(INamedElement target) {
    	try{
    		return validateRule(target);
    	}catch(Exception e){
    		throw new ApplicationException(e);
    	}
    }
    
    @Override
    public ValidationErrorLevel getErrorLevel() {
        return ValidationErrorLevel.ERROR;
    }

    @Override
    public String getHelpPath() {
        return null;
    }
    
    @Override
    public List<validation.QuickFix> getQuickFixes(INamedElement target) {
    	return null;
    }
    
    abstract public boolean validateRule(INamedElement target) throws Exception;
    	
}
