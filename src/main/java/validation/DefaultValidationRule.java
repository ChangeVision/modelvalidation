package validation;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultValidationRule implements ValidationRule {

    private List<ValidationError> results = new ArrayList<ValidationError>();

    public void setResult(ValidationError result) {
        results.clear();
        this.results.add(result);
    }

    public void setResult(ValidationError result, boolean needClear) {
        if (needClear) {
            results.clear();
        }
        this.results.add(result);
    }

    @Override
    public List<ValidationError> getResults() {
        return results;
    }

    @Override
    public ValidationErrorLevel getErrorLevel() {
        return ValidationErrorLevel.NOTICE;
    }

    @Override
    public String getHelpPath() {
        return null;
    }
}
