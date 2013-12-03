package ac.soton.eventb.diagrameditor.features;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;

import ac.soton.eventb.diagrameditor.EventBDiagramFeatureProvider;
import ac.soton.eventb.diagrameditor.IEventBFeature;
import ac.soton.eventb.diagrameditor.Matcher;
import ac.soton.eventb.diagrameditor.features.add.EventBRelationshipAddFeature;
import ac.soton.eventb.diagrameditor.relations.EventBRelation;

public class EventBRelationFeature implements IEventBFeature {
	@Override
	public Matcher<IAddContext, IAddFeature> getAddMatcher() {
		return new Matcher<IAddContext, IAddFeature>() {

			@Override
			public boolean match(IAddContext o, EventBDiagramFeatureProvider e) {
				return o.getNewObject() instanceof EventBRelation;
			}

			@Override
			public IAddFeature getFeature(IAddContext o, EventBDiagramFeatureProvider e) {
				if(this.match(o, e)) {
					return new EventBRelationshipAddFeature(e);
				}
				return null;
			}
		};
	}

	@Override
	public boolean canAdd() {
		return true;
	}

	@Override
	public boolean canDelete() {
		return false;
	}

	@Override
	public Matcher<IDeleteContext, IDeleteFeature> getDeleteMatcher() {
		return new Matcher<IDeleteContext, IDeleteFeature>() {
			@Override
			public boolean match(IDeleteContext o, EventBDiagramFeatureProvider e) {
				return e.getBusinessObjectForPictogramElement(o.getPictogramElement()) instanceof EventBRelation;
			}
			
			@Override
			public IDeleteFeature getFeature(IDeleteContext o,
					EventBDiagramFeatureProvider e) {
				if(this.match(o, e)) {
					return new EventBRelationshipDeleteFeature(e);
				}
				return null;
			}
		};
	}
}