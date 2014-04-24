package validation;

import java.util.List;

import com.change_vision.jude.api.inf.model.INamedElement;

public class ValidationError {

    private String message;
    private INamedElement target;
    private ValidationRule rule;
    private List<INamedElement> relatedDiagramTargetList;
	private String category;

    public ValidationError(String category,String message, INamedElement target, ValidationRule rule) {
    	this.category = category;
        this.message = message;
        this.target = target;
        this.rule = rule;
    }

    public ValidationError(String category,String message, INamedElement target, ValidationRule rule, List<INamedElement> relatedDiagramTargetList) {
    	this(category,message,target,rule);
        this.relatedDiagramTargetList = relatedDiagramTargetList;
    }

    public String getMessage() {
        return this.message;
    }

    public INamedElement getTarget() {
        return this.target;
    }

    public List<QuickFix> getQuickFixes() {
        return rule.getQuickFixes(target);
    }

    public String toString() {
        return this.message;
    }

    public ValidationErrorLevel getErrorLevel() {
        return this.rule.getErrorLevel();
    }

    public String getHelpPath() {
        return rule.getHelpPath();
    }

    public List<INamedElement> getRelatedDiagramTargetList() {
        return this.relatedDiagramTargetList;
    }

	public String getCategory() {
		return category;
	}

}
