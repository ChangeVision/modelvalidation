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

enum ModelType {
    REQUIREMENT(Messages.getMessage("model_validation_type.requirement"), IconDescription.REQUIREMENT_REQUIREMENT),
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
    INITIALSTATE(Messages.getMessage("model_validation_type.initialstate"), IconDescription.UML_STATECHART_INITIALSTATE),
    TRANSITION(Messages.getMessage("model_validation_type.transition"), IconDescription.UML_STATECHART_TRANSITION),
    SHALLOWHISTORY(Messages.getMessage("model_validation_type.shallow_history"), IconDescription.UML_STATECHART_SHALLOWHISTORY),
    DEEPHISTORY(Messages.getMessage("model_validation_type.deep_history"), IconDescription.UML_STATECHART_DEEPHISTORY),
    CHOICE(Messages.getMessage("model_validation_type.choice"), IconDescription.UML_STATECHART_CHOICE),
    FORK(Messages.getMessage("model_validation_type.fork"), IconDescription.UML_STATECHART_FORK),
    JOIN(Messages.getMessage("model_validation_type.join"), IconDescription.UML_STATECHART_JOIN),
    JUNCTION(Messages.getMessage("model_validation_type.junction"), IconDescription.UML_STATECHART_JUNCTIONPOINT),
    DEPENDENCY(Messages.getMessage("model_validation_type.dependency"), IconDescription.UML_CLASS_DEPENDENCY),
    ASSOCIATIONEND(Messages.getMessage("model_validation_type.association_end"), IconDescription.UML_CLASS_ASSOCATION);

    String label = "";
    IconDescription iconDescription;

    ModelType(String label, IconDescription iconDescription) {
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
    
    public static ModelType getType(INamedElement target) {
        if (TypeOf.isPort(target)) {
            return ModelType.PORT;
        } else if (TypeOf.isOperation(target)) {
            return ModelType.OPERATION;
        } else if (TypeOf.isConnector(target)) {
            return ModelType.CONNECTOR;
        } else if (TypeOf.isPackage(target)) {
            if (target.getOwner() == null) {
                return ModelType.PROJECT;
            }
            return ModelType.PACKAGE;
        } else if (TypeOf.isAttribute(target)) {
            return ((IAttribute)target).getAssociation() == null? ModelType.ATTRIBUTE: ModelType.ASSOCIATIONEND;
        } else if (target instanceof IParameter) {
            return ModelType.PARAMETERS;
        } else if (target instanceof IStateMachineDiagram) {
            return ModelType.STATECHARTDGM;
        } else if (target instanceof IState) {
            if (StateMachineTypeOf.isSubmachineState(target)) {
                return ModelType.SUBMACHINESTATE;
            } else {
                return ModelType.STATE;
            }
        } else if (StateMachineTypeOf.isInitialPseudostate(target)) {
            return ModelType.INITIALSTATE;
        } else if (target instanceof ITransition) {
            return ModelType.TRANSITION;
        } else if (target instanceof IPseudostate) {
            IPseudostate pseudostate = (IPseudostate) target;
            if (pseudostate.isShallowHistoryPseudostate()) {
                return ModelType.SHALLOWHISTORY;
            } else if (pseudostate.isDeepHistoryPseudostate()) {
                return ModelType.DEEPHISTORY;
            } else if (pseudostate.isForkPseudostate()) {
                return ModelType.FORK;
            } else if (pseudostate.isChoicePseudostate()) {
                return ModelType.CHOICE;
            } else if (pseudostate.isJoinPseudostate()) {
                return ModelType.JOIN;
            } else if (pseudostate.isJunctionPseudostate()) {
                return ModelType.JUNCTION;
            }
        } else if (target instanceof IDependency) {
            return ModelType.DEPENDENCY;
        } else if (TypeOf.isClass(target)) {
            if (TypeOf.isInterface(target)) {
                return ModelType.INTERFACE;
            } else {
                return ModelType.CLASS;
            }
        }
        return null;
    }
}