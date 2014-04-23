package validation.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import validation.Activator;
import validation.Messages;
import validation.view.ModelValidationViewLocator;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandler;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.change_vision.jude.api.inf.view.IExtraViewManager;
import com.change_vision.jude.api.inf.view.IViewManager;

public class ModelValidationAction implements IPluginActionDelegate {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ModelValidationAction.class);
	private IMessageDialogHandler util = Activator.getMessageHandler();

	public Object run(IWindow window) throws UnExpectedException {
		try {
			AstahAPI.getAstahAPI().getProjectAccessor().getProject();
			ModelValidationViewLocator.getInstance().showErrorOnModelValidationView();
			showDialog(window);
		} catch (ProjectNotFoundException e) {
			String message = Messages.getMessage("error.project.not.found");
			util.showWarningMessage(window.getParent(), message);
		} catch (ClassNotFoundException e) {
			util.showUnexpecedErrorMessage(window.getParent());
			throw new UnExpectedException();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			util.showUnexpecedErrorMessage(window.getParent());
			throw new UnExpectedException();
		}

		return null;
	}

	private void showDialog(IWindow window) throws UnExpectedException {
		ModelValidationViewLocator instance = ModelValidationViewLocator.getInstance();
		Boolean isDetected = instance.isDetected();
		boolean isNoticeOnly = instance.isNoticeOnly();
		if (isDetected) {
			showModelValidationView(window);
			String message = Messages.getMessage("validation.result.detected");
			if (isNoticeOnly) {
				message = Messages.getMessage("validation.result.noticeonly");
			}
			util.showWarningMessage(window.getParent(), message);
		} else {
			String message = Messages.getMessage("validation.result.ok");
			util.showInformationMessage(window.getParent(), message);
		}
	}

	private void showModelValidationView(IWindow window) throws UnExpectedException {
		try {
			IViewManager viewManager = AstahAPI.getAstahAPI().getProjectAccessor().getViewManager();
			IExtraViewManager extraViewManager = viewManager.getExtraViewManager();
			extraViewManager.showExtraView();
		} catch (InvalidUsingException e) {
			throw new UnExpectedException();
		} catch (ClassNotFoundException e) {
			throw new UnExpectedException();
		}
	}
	
}
