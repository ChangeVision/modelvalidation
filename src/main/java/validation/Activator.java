package validation;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import validation.view.ModelValidationViewLocator;

import com.change_vision.jude.api.inf.ui.IMessageDialogHandler;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandlerFactory;

public class Activator implements BundleActivator {
	private static IMessageDialogHandler messageHandler;

	public void start(BundleContext context) {
		ServiceReference reference = context.getServiceReference(IMessageDialogHandlerFactory.class
				.getName());
		IMessageDialogHandlerFactory factory = (IMessageDialogHandlerFactory) context
				.getService(reference);
		if (factory != null) {
			messageHandler = factory.createMessageDialogHandler(new Messages(),
					"\\.astah\\sysml\\modelvalidation.log");
		}

		context.registerService(ModelValidationViewLocator.class.getName(), ModelValidationViewLocator.getInstance(), null);

		context.ungetService(reference);
	}

	public void stop(BundleContext context) {
	}

	public static IMessageDialogHandler getMessageHandler() {
		return messageHandler;
	}
}
