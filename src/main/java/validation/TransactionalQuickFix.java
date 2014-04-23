package validation;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import validation.exceptions.ApplicationException;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.BadTransactionException;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandler;

public abstract class TransactionalQuickFix implements QuickFix {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TransactionalQuickFix.class);
	private IMessageDialogHandler util = Activator.getMessageHandler();
	
	public abstract void fixModel() throws InvalidEditingException;
	
	@Override
	public void fix() {
	    try {
	        AstahAPI.getAstahAPI().getProjectAccessor().getTransactionManager().beginTransaction();
	        fixModel();
	        AstahAPI.getAstahAPI().getProjectAccessor().getTransactionManager().endTransaction();
	    } catch (InvalidEditingException e) {
	        showErrorMessage(e);
	    } catch (Exception e) {
	        Throwable cause = e.getCause();
	        if (cause != null && cause instanceof InvalidEditingException) {
	            showErrorMessage((InvalidEditingException)cause);
	        } else {
	            logger.error(Messages.getMessage("error.during_quickfix_model_edit",e));
	            abortTransaction();
	            throw new ApplicationException(e);
	        }
	    }
	}
	
	private void showErrorMessage(InvalidEditingException e) {
        if (e.getKey().equals(InvalidEditingException.READ_ONLY_KEY)) {
        	util.showErrorMessage(getMainFrame(), 
                    Messages.getMessage("validation.error.read_only"));
        	abortTransaction();
        } else if (e.getKey().equals(InvalidEditingException.HAS_INVALID_MODEL_KEY)) {
            util.showErrorMessage(getMainFrame(), 
                    Messages.getMessage("validation.error.no_presentation"));
            abortTransaction();
        } else {
            logger.error(Messages.getMessage("error.during_quickfix_model_edit",e));
            abortTransaction();
            throw new ApplicationException(e);
        }
	}

	private void abortTransaction()  {
		try {
			AstahAPI.getAstahAPI().getProjectAccessor().getTransactionManager().abortTransaction();
		} catch (BadTransactionException e) {
			throw new ApplicationException(e);
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(e);
		}
	}
	
	private JFrame getMainFrame() {
		try {
			return AstahAPI.getAstahAPI().getViewManager().getMainFrame();
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(e);
		} catch (InvalidUsingException e) {
			throw new ApplicationException(e);
		}
	}
	
}
