package validation.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import validation.Activator;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandler;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.change_vision.jude.api.inf.view.IExtraViewManager;
import com.change_vision.jude.api.inf.view.IViewManager;

public class ShowHideModelValidationViewAction implements IPluginActionDelegate {
	private static final Logger logger = LoggerFactory.getLogger(ModelValidationAction.class);
	private IMessageDialogHandler util = Activator.getMessageHandler();

	@Override
	public Object run(IWindow window) throws UnExpectedException {
		try {
			ProjectAccessor prjAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			IViewManager viewManager = prjAccessor.getViewManager();
			IExtraViewManager extraViewManager = viewManager.getExtraViewManager();
			extraViewManager.showHideExtraView();
		} catch (InvalidUsingException e) {
			logger.error(e.getMessage(), e);
			util.showUnexpecedErrorMessage(window.getParent());
			throw new UnExpectedException();
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			util.showUnexpecedErrorMessage(window.getParent());
			throw new UnExpectedException();
		}
		return null;
	}

}
