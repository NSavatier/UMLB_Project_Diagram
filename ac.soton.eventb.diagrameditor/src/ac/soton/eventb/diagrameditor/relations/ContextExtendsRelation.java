package ac.soton.eventb.diagrameditor.relations;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eventb.emf.core.context.Context;
import org.eventb.emf.persistence.ProjectResource;

public class ContextExtendsRelation implements EventBRelation {
	Context source;
	Context target;

	// source extends target

	public ContextExtendsRelation(Context source, Context target) {
		this.source = source;
		this.target = target;
		if (!source.getExtends().contains(target)) {
			source.getExtends().add(target);
		}
	}

	public ContextExtendsRelation(String key, ProjectResource pr) {
		final String[] keys = key.substring("extends:".length()).split("<!extends!>"); //$NON-NLS-1$
		this.source = (Context) pr.getEObject(URI.createURI(keys[0], true)
				.fragment());
		this.target = (Context) pr.getEObject(URI.createURI(keys[1], true)
				.fragment());
		
		//source or target might be null, so I add this logging to notify the developer in that case (should help with debugging)
		if(this.source == null) {
			Logger.getLogger("diagram-editor").log(Level.SEVERE, "WARNING : source could not be resolved in ContextExtendsRelation");
		}
		if(this.target == null) {
			Logger.getLogger("diagram-editor").log(Level.SEVERE, "WARNING : target could not be resolved in ContextExtendsRelation");
		}
	}

	@Override
	public void delete() {
		this.getSource().getExtends().remove(this.getTarget());

	}

	@Override
	public String getKey() {
		return "extends:" + EcoreUtil.getURI(this.getSource()).toString() //$NON-NLS-1$
				+ "<!extends!>" + EcoreUtil.getURI(this.getTarget()).toString(); //$NON-NLS-1$
	}

	public Context getSource() {
		return this.source;
	}

	public Context getTarget() {
		return this.target;
	}
}
