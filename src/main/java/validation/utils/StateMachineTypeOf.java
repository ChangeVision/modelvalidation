package validation.utils;

import com.change_vision.jude.api.inf.model.IFinalState;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPseudostate;
import com.change_vision.jude.api.inf.model.IState;

public class StateMachineTypeOf {

    public static boolean isInitialPseudostate(INamedElement element) {
        if(element instanceof IPseudostate){
            return ((IPseudostate)element).isInitialPseudostate();
        }
        return false;
    }
    
    public static boolean isSubmachineState(INamedElement element) {
		if (!isState(element))
			return false;
		IState state = (IState) element;
		return state.isSubmachineState();
	}
    
    public static boolean isFinalState(INamedElement element) {
        return element instanceof IFinalState;
    }
    
	public static boolean isState(INamedElement element) {
		return element instanceof IState;
	}
}
