package validation.utils;

import com.change_vision.jude.api.inf.model.IAssociation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IPort;

public class TypeOf {

	public static boolean isPart(INamedElement element) {
		if (element instanceof IAttribute) {
			if (((IAttribute) element).getType() == null)
				return false;

			IAssociation association = ((IAttribute) element).getAssociation();
			if (association != null) {
				IAttribute[] memberEnds = association.getMemberEnds();
				if (memberEnds[0].equals(element)) {
					return memberEnds[1].isComposite();
				} else {
					return memberEnds[0].isComposite();
				}
			}
		}
		return false;
	}

	public static boolean isBlock(INamedElement target) {
		return target instanceof IBlock;
	}

	public static boolean isPort(INamedElement target) {
		return target instanceof IPort;
	}

	public static boolean isOperation(INamedElement target) {
		return target instanceof IOperation;
	}

	public static boolean isConnector(INamedElement target) {
		return target instanceof IConnector;
	}

	public static boolean isPackage(INamedElement target) {
		return target instanceof IPackage;
	}

	public static boolean isClass(INamedElement target) {
		return target instanceof IClass;
	}

	public static boolean isInterface(INamedElement target) {
		return isClass(target) && target.hasStereotype("interface");
	}

	public static boolean isAttribute(INamedElement target) {
		return target instanceof IAttribute;
	}
}
