package com.github.astah.nanny;

import javax.swing.JFrame;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;
import com.change_vision.jude.api.inf.view.IViewManager;

public class AstahAPIHandler {
	public String getAstahEdition() {
		String edition = getProjectAccessor().getAstahEdition();
		if (edition.isEmpty()) {
			edition = "professional";
		}
		return edition;
	}
	
	public IDiagramViewManager getDiagramViewManager() {
		return getViewManager().getDiagramViewManager();
	}

	public ProjectAccessor getProjectAccessor() {
		ProjectAccessor projectAccessor = null;
		try {
			projectAccessor = ProjectAccessorFactory.getProjectAccessor();
		} catch (ClassNotFoundException e) {
	        throw new IllegalStateException(e);
		}
		if(projectAccessor == null) throw new IllegalStateException("ProjectAccessor must not be null.");
		return projectAccessor;
	}

	public JFrame getMainFrame() {
		return getViewManager().getMainFrame();
	}

	private IViewManager getViewManager() {
		try {
			return getProjectAccessor().getViewManager();
		} catch (InvalidUsingException e) {
			throw new IllegalStateException("viewManager must not be null.");
		}
	}
}
