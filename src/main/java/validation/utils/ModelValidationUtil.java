package validation.utils;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import validation.exceptions.ApplicationException;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;
import com.change_vision.jude.api.inf.view.IIconManager;
import com.change_vision.jude.api.inf.view.IProjectViewManager;
import com.change_vision.jude.api.inf.view.IconDescription;

public class ModelValidationUtil {
	private static final Logger logger = LoggerFactory.getLogger(ModelValidationUtil.class);

	public static void showInDiagramEditor(IPresentation presentation) {
		try {
			IDiagramViewManager diagramViewManager = AstahAPI.getAstahAPI().getViewManager().getDiagramViewManager();
			diagramViewManager.showInDiagramEditor(presentation);
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(e);
		} catch (InvalidUsingException e) {
			throw new ApplicationException(e);
		}
	}

	public static void showInStructureTree(INamedElement target) {
		IProjectViewManager projectViewManager;
		try {
			projectViewManager = AstahAPI.getAstahAPI().getViewManager().getProjectViewManager();
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(e);
		} catch (InvalidUsingException e) {
			throw new ApplicationException(e);
		}

		if (target instanceof IParameter) {
			target = (IOperation) target.getOwner();
		}
		projectViewManager.showInStructureTree((IElement) target);
	}


	public static IPresentation[] getPresentations(INamedElement target) {
		IPresentation[] presentations = null;
		try {
			if (target instanceof IOperation) {
				IElement owner = target.getOwner();
				presentations = owner.getPresentations();
			} else {
				presentations = ((IElement) target).getPresentations();
			}
		} catch (InvalidUsingException e) {
			logger.error(e.getMessage(), e);
		}

		return presentations;
	}

	public static Icon getIcon(IconDescription desc) {
		try {
			IIconManager iconManager = AstahAPI.getAstahAPI().getProjectAccessor().getViewManager()
					.getIconManager();
			return iconManager.getIcon(desc);
		} catch (InvalidUsingException ue) {
			return new ImageIcon();
		} catch (Exception e) {
			if (e.toString().equals("java.lang.reflect.UndeclaredThrowableException")) {
				return new ImageIcon();
			} else {
				throw new ApplicationException(e);
			}
		} catch (Throwable e) {
			throw new ApplicationException(e);
		}
	}

	public static String getName(INamedElement target) {
		String name = null;
		if (TypeOf.isPart(target)) {
			IAttribute iAttribute = (IAttribute) target;
			name = iAttribute.getName() + ":" + iAttribute.getType().getName();
		} else if (target instanceof IPackage && (target.getOwner() == null)) {
			name = getProjectName();
		} else {
			name = target.getName();
		}
		return name;
	}

	public static String getProjectName() {
		try {
			String prjPath = AstahAPI.getAstahAPI().getProjectAccessor().getProjectPath();
			File prjFile = new File(prjPath);
			String fileName = prjFile.getName();
			int point = fileName.lastIndexOf(".");
			if (point != -1) {
				return fileName.substring(0, point);
			}
			return "no-title";
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}
}
