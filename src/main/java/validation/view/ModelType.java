package validation.view;

import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.view.IconDescription;

public interface ModelType {

	 public IconDescription getIconDescription();
	 
	 public ModelType getType(INamedElement target);
}
