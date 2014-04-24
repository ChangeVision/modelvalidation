package validation.view;

import java.util.ArrayList;
import java.util.List;

import validation.ValidationRuleManager;


public class ModelValidationViewLocator {

	private static ModelValidationViewLocator instance = new ModelValidationViewLocator();
	private ModelValidationView view;
	private List<ValidationRuleManager> ruleManagers;
	
	private ModelValidationViewLocator(){
		ruleManagers = new ArrayList<ValidationRuleManager>();
	}
	
	public static ModelValidationViewLocator getInstance(){
		return instance;
	}
	
	void setModelValidationView(ModelValidationView view){
		this.view = view;
	}
	
	public void addValidationRuleManager(ValidationRuleManager ruleManager){
		ruleManagers.add(ruleManager);
	}
	
	public List<ValidationRuleManager> getValidationRuleManagers(){
		return this.ruleManagers;
	}
	
	public void showErrorOnModelValidationView(){
		this.view.showError();
	}

    public boolean isDetected() {
        return view.isDetected();
    }
    
    public boolean hasFatalError() {
        return view.hasFatalError();
    }
    
    public boolean isNoticeOnly() {
        return view.isNoticeOnly();
    }

    void clearRuleManager(){
    	ruleManagers.clear();
    }
}
