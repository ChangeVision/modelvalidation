package validation.view;

import validation.Messages;
import validation.utils.StateMachineTypeOf;
import validation.utils.TypeOf;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.model.IPseudostate;
import com.change_vision.jude.api.inf.model.IState;
import com.change_vision.jude.api.inf.model.IStateMachineDiagram;
import com.change_vision.jude.api.inf.model.ITransition;
import com.change_vision.jude.api.inf.view.IconDescription;

public class UmlModelType implements ModelType {
	private UmlModelTypes type;
	
	public UmlModelType(UmlModelTypes type){
		this.type = type;
	}
	
    public ModelType getType(INamedElement target) {
        if (TypeOf.isPart(target)) {
            type = UmlModelTypes.PART;
        } else if (TypeOf.isPort(target)) {
            type = UmlModelTypes.PORT;
        } else if (TypeOf.isOperation(target)) {
            type = UmlModelTypes.OPERATION;
        } else if (TypeOf.isConnector(target)) {
            type = UmlModelTypes.CONNECTOR;
        } else if (TypeOf.isPackage(target)) {
            type = UmlModelTypes.PACKAGE;
            if (target.getOwner() == null) {
                type = UmlModelTypes.PROJECT;
            }
        } else if (TypeOf.isClass(target)) {
            if (TypeOf.isInterface(target)) {
                type = UmlModelTypes.INTERFACE;
            } else {
                type = UmlModelTypes.CLASS;
            }
        } else if (TypeOf.isAssociation(target)) {
        	type = UmlModelTypes.ASSOCIATION;
        } else if (TypeOf.isAttribute(target)) {
            type = ((IAttribute)target).getAssociation() == null? UmlModelTypes.ATTRIBUTE: UmlModelTypes.ASSOCIATIONEND;
        } else if (target instanceof IParameter) {
            type = UmlModelTypes.PARAMETERS;
        } else if (target instanceof IStateMachineDiagram) {
            type = UmlModelTypes.STATECHARTDGM;
        } else if (target instanceof IState) {
            if (StateMachineTypeOf.isSubmachineState(target)) {
                type = UmlModelTypes.SUBMACHINESTATE;
            } else if (StateMachineTypeOf.isFinalState(target)) {
                type = UmlModelTypes.FINALSTATE;
            } else {
                type = UmlModelTypes.STATE;
            }
        } else if (StateMachineTypeOf.isInitialPseudostate(target)) {
            type = UmlModelTypes.INITIALSTATE;
        } else if (target instanceof ITransition) {
            type = UmlModelTypes.TRANSITION;
        } else if (target instanceof IPseudostate) {
            IPseudostate pseudostate = (IPseudostate) target;
            if (pseudostate.isShallowHistoryPseudostate()) {
                type = UmlModelTypes.SHALLOWHISTORY;
            } else if (pseudostate.isDeepHistoryPseudostate()) {
                type = UmlModelTypes.DEEPHISTORY;
            } else if (pseudostate.isForkPseudostate()) {
                type = UmlModelTypes.FORK;
            } else if (pseudostate.isChoicePseudostate()) {
                type = UmlModelTypes.CHOICE;
            } else if (pseudostate.isJoinPseudostate()) {
                type = UmlModelTypes.JOIN;
            } else if (pseudostate.isJunctionPseudostate()) {
                type = UmlModelTypes.JUNCTION;
            } else if (pseudostate.isEntryPointPseudostate()) {
                type = UmlModelTypes.ENTRYPOINT;
            } else if (pseudostate.isExitPointPseudostate()) {
                type = UmlModelTypes.EXITPOINT;
            } else if (pseudostate.isStubState()) {
                type = UmlModelTypes.STUBSTATE;
            }
        } else if (target instanceof IDependency) {
            type = UmlModelTypes.DEPENDENCY;
        }
        return new UmlModelType(type);
    }

	@Override
	public IconDescription getIconDescription() {
		return type.getIconDescription();
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	

	public enum UmlModelTypes {
		PART(Messages.getMessage("model_validation_type.part"), IconDescription.UML_COMPOSITE_STRUCTURE_PART),
	    PORT(Messages.getMessage("model_validation_type.port"), IconDescription.UML_COMPOSITE_STRUCTURE_PORT),
	    CONNECTOR(Messages.getMessage("model_validation_type.connector"), IconDescription.UML_COMPOSITE_STRUCTURE_CONNECTOR),
	    PACKAGE(Messages.getMessage("model_validation_type.package"), IconDescription.PACKAGE),
	    CLASS(Messages.getMessage("model_validation_type.class"), IconDescription.UML_CLASS_CLASS),
	    INTERFACE(Messages.getMessage("model_validation_type.interface"), IconDescription.UML_CLASS_INTERFACE),
	    OPERATION(Messages.getMessage("model_validation_type.operation"), IconDescription.UML_CLASS_OPE),
	    ATTRIBUTE(Messages.getMessage("model_validation_type.attribute"), IconDescription.UML_CLASS_ATTR),
	    PARAMETERS(Messages.getMessage("model_validation_type.parameters"), IconDescription.UML_CLASS_OPE),
	    PROJECT(Messages.getMessage("model_validation_type.project"), IconDescription.PROJECT),
	    STATECHARTDGM(Messages.getMessage("model_validation_type.statechartdgm"), IconDescription.UML_DGM_STATECHART),
	    STATE(Messages.getMessage("model_validation_type.state"), IconDescription.UML_STATECHART_STATE),
	    SUBMACHINESTATE(Messages.getMessage("model_validation_type.submachinestate"),IconDescription.UML_STATECHART_SUBSTATE),
        FINALSTATE(Messages.getMessage("model_validation_type.finalstate"), IconDescription.UML_STATECHART_FINALSTATE),
	    INITIALSTATE(Messages.getMessage("model_validation_type.initialstate"), IconDescription.UML_STATECHART_INITIALSTATE),
	    TRANSITION(Messages.getMessage("model_validation_type.transition"), IconDescription.UML_STATECHART_TRANSITION),
	    SHALLOWHISTORY(Messages.getMessage("model_validation_type.shallow_history"), IconDescription.UML_STATECHART_SHALLOWHISTORY),
	    DEEPHISTORY(Messages.getMessage("model_validation_type.deep_history"), IconDescription.UML_STATECHART_DEEPHISTORY),
	    CHOICE(Messages.getMessage("model_validation_type.choice"), IconDescription.UML_STATECHART_CHOICE),
	    FORK(Messages.getMessage("model_validation_type.fork"), IconDescription.UML_STATECHART_FORK),
	    JOIN(Messages.getMessage("model_validation_type.join"), IconDescription.UML_STATECHART_JOIN),
	    JUNCTION(Messages.getMessage("model_validation_type.junction"), IconDescription.UML_STATECHART_JUNCTIONPOINT),
        ENTRYPOINT(Messages.getMessage("model_validation_type.entry_point"), IconDescription.UML_STATECHART_ENTRY_POINT),
        EXITPOINT(Messages.getMessage("model_validation_type.exit_point"), IconDescription.UML_STATECHART_EXIT_POINT),
        STUBSTATE(Messages.getMessage("model_validation_type.stubstate"), IconDescription.UML_STATECHART_SUBSTATE),
	    DEPENDENCY(Messages.getMessage("model_validation_type.dependency"), IconDescription.UML_CLASS_DEPENDENCY),
	    ASSOCIATION(Messages.getMessage("model_validation_type.association"), IconDescription.UML_CLASS_ASSOCATION),
	    ASSOCIATIONEND(Messages.getMessage("model_validation_type.association_end"), IconDescription.UML_CLASS_ASSOCATION);

	    String label = "";
	    IconDescription iconDescription;
	    
	    UmlModelTypes(String label, IconDescription iconDescription) {
	        this.label = label;
	        this.iconDescription = iconDescription;
	    }
	    
	    @Override
	    public String toString() {
	        return this.label;
	    }
	    
	    public IconDescription getIconDescription() {
	        return this.iconDescription;
	    }
	}
    
}