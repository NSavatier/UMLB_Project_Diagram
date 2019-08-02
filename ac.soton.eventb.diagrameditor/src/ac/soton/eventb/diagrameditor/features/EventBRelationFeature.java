package ac.soton.eventb.diagrameditor.features;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;
import org.eventb.emf.core.context.Context;
import org.eventb.emf.core.machine.Machine;

import ac.soton.eventb.diagrameditor.EventBDiagramFeatureProvider;
import ac.soton.eventb.diagrameditor.ImageProvider;
import ac.soton.eventb.diagrameditor.relations.ContextExtendsRelation;
import ac.soton.eventb.diagrameditor.relations.EventBRelation;
import ac.soton.eventb.diagrameditor.relations.MachineRefinesRelation;
import ac.soton.eventb.diagrameditor.relations.MachineSeesRelation;

/**
 * Defines the Extends RelationShip feature.
 * This describes the connection that can be drawn between two Contexts.
 */
class CreateExtendsRelationshipFeature extends AbstractCreateConnectionFeature {

	public CreateExtendsRelationshipFeature(IFeatureProvider fp) {
		super(fp, "Extends", "Create an Event-B Extends Relationship"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public String getCreateImageId(){
		return ImageProvider.IMG_EXTENTS;
	}

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		final Object source = this
				.getBusinessObjectForPictogramElement(context
						.getSourcePictogramElement());
		final Object target = this
				.getBusinessObjectForPictogramElement(context
						.getTargetPictogramElement());

		return source instanceof Context && target instanceof Context;
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		final Object source = this
				.getBusinessObjectForPictogramElement(context
						.getSourcePictogramElement());

		return source instanceof Context;
	}
	
	//Describes what happens when an "Extends" link is created using the dedicated Diagram tool
	@Override
	public Connection create(ICreateConnectionContext context) {
		final Object source = this
				.getBusinessObjectForPictogramElement(context
						.getSourcePictogramElement());
		final Object target = this
				.getBusinessObjectForPictogramElement(context
						.getTargetPictogramElement());

		if (source instanceof Context && target instanceof Context) {
			final ContextExtendsRelation msr = new ContextExtendsRelation(
					(Context) source, (Context) target);
			final AddConnectionContext acc = new AddConnectionContext(
					context.getSourceAnchor(), context.getTargetAnchor());
			acc.setNewObject(msr);
			return (Connection) this.getFeatureProvider().addIfPossible(acc);
		}
		return null;
	}

}

/**
 * Defines the Refines RelationShip feature.
 * This describes the connection that can be drawn between two Machines.
 */
class CreateRefinesRelationshipFeature extends AbstractCreateConnectionFeature {

	public CreateRefinesRelationshipFeature(IFeatureProvider fp) {
		super(fp, "Refines", "Create an Event-B Refines Relationship"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public String getCreateImageId(){
		return ImageProvider.IMG_REFINES;
	}
	
	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		final Object source = this
				.getBusinessObjectForPictogramElement(context
						.getSourcePictogramElement());
		final Object target = this
				.getBusinessObjectForPictogramElement(context
						.getTargetPictogramElement());

		return source instanceof Machine && target instanceof Machine;
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		final Object source = this
				.getBusinessObjectForPictogramElement(context
						.getSourcePictogramElement());

		return source instanceof Machine;
	}

	@Override
	public Connection create(ICreateConnectionContext context) {
		final Object source = this
				.getBusinessObjectForPictogramElement(context
						.getSourcePictogramElement());
		final Object target = this
				.getBusinessObjectForPictogramElement(context
						.getTargetPictogramElement());

		if (source instanceof Machine && target instanceof Machine) {
			final MachineRefinesRelation msr = new MachineRefinesRelation(
					(Machine) source, (Machine) target);
			final AddConnectionContext acc = new AddConnectionContext(
					context.getSourceAnchor(), context.getTargetAnchor());
			acc.setNewObject(msr);
			return (Connection) this.getFeatureProvider().addIfPossible(acc);
		}
		return null;
	}

}


/**
 * Defines the Sees RelationShip feature.
 * This describes the connection that can be drawn between a Machine and a Context.
 */
class CreateSeesRelationshipFeature extends AbstractCreateConnectionFeature {

	public CreateSeesRelationshipFeature(IFeatureProvider fp) {
		super(fp, "Sees", "Create an Event-B Sees Relationship"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getCreateImageId(){
		return ImageProvider.IMG_SEES;
	}
	
	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		final Object source = this
				.getBusinessObjectForPictogramElement(context
						.getSourcePictogramElement());
		final Object target = this
				.getBusinessObjectForPictogramElement(context
						.getTargetPictogramElement());

		return source instanceof Machine && target instanceof Context;
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		final Object source = this
				.getBusinessObjectForPictogramElement(context
						.getSourcePictogramElement());

		return source instanceof Machine;
	}

	@Override
	public Connection create(ICreateConnectionContext context) {
		final Object source = this
				.getBusinessObjectForPictogramElement(context
						.getSourcePictogramElement());
		final Object target = this
				.getBusinessObjectForPictogramElement(context
						.getTargetPictogramElement());

		if (source instanceof Machine && target instanceof Context) {
			final MachineSeesRelation msr = new MachineSeesRelation(
					(Machine) source, (Context) target);
			final AddConnectionContext acc = new AddConnectionContext(
					context.getSourceAnchor(), context.getTargetAnchor());
			acc.setNewObject(msr);
			return (Connection) this.getFeatureProvider().addIfPossible(acc);
		}
		return null;
	}

}

/**
 * Defines how the EventBRelations are drawn on the diagram.
 * the getAddMatcher() method returns a Matcher whose role is to provide the right EventBRelationshipAddFeature.
 */
public class EventBRelationFeature implements IEventBFeature {

	@Override
	public boolean canAdd() {
		return true;
	}

	@Override
	public boolean canCreate() {
		return false;
	}

	@Override
	public boolean canCreateRelationship() {
		return true;
	}

	@Override
	public boolean canDelete() {
		return true;
	}

	@Override
	public boolean canDirectEdit() {
		return false;
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public Matcher<IAddContext, IAddFeature> getAddMatcher() {
		return new Matcher<IAddContext, IAddFeature>() {

			//This method is the important one, as it is the one that describes what add feature will be returned,
			//depending on the addContext that needs to be drawn
			@Override
			public IAddFeature getFeature(IAddContext addContext, EventBDiagramFeatureProvider featureProvider) {
				Object connectionToDraw = addContext.getNewObject();
				if (this.match(addContext, featureProvider)) {
					if(connectionToDraw instanceof EventBRelation) {
						//Add feature for EventBRelations
						return new EventBRelationshipAddFeature(featureProvider, (EventBRelation) connectionToDraw);
					} else if(connectionToDraw instanceof EReference) {
						//Add feature for other types of relations (EReferences)
						return new GenericRelationshipAddFeature(featureProvider, (EReference) connectionToDraw);
					}//else return null
				}
				return null;
			}

			@Override
			public boolean match(IAddContext addContext, EventBDiagramFeatureProvider featureProvider) {
				return addContext.getNewObject() instanceof EventBRelation
						|| addContext.getNewObject() instanceof EReference;
			}
		};
	}

	@Override
	public Collection<ICreateFeature> getCreateFeatures(
			EventBDiagramFeatureProvider e) {
		return null;
	}

	//defines the create relationships features provided
	@Override
	public Collection<? extends ICreateConnectionFeature> getCreateRelationshipFeatures(
			EventBDiagramFeatureProvider fp) {
		final ArrayList<ICreateConnectionFeature> relationshipList = new ArrayList<>();
		relationshipList.add(new CreateExtendsRelationshipFeature(fp));
		relationshipList.add(new CreateRefinesRelationshipFeature(fp));
		relationshipList.add(new CreateSeesRelationshipFeature(fp));
		return relationshipList;
	}

	//defines the delete features provided
	@Override
	public Matcher<IDeleteContext, IDeleteFeature> getDeleteMatcher() {
		return new Matcher<IDeleteContext, IDeleteFeature>() {
			
			//the delete features are registered here
			@Override
			public IDeleteFeature getFeature(IDeleteContext deleteContext, EventBDiagramFeatureProvider featureProvider) {
				if (this.match(deleteContext, featureProvider)) {
					return new EventBRelationshipDeleteFeature(featureProvider);
				}
				return null;
			}

			@Override
			public boolean match(IDeleteContext deleteContext, EventBDiagramFeatureProvider featureProvider) {
				return featureProvider.getBusinessObjectForPictogramElement(deleteContext.getPictogramElement())
					   instanceof EventBRelation;
			}
		};
	}

	@Override
	public Matcher<IDirectEditingContext, IDirectEditingFeature> getDirectEditingMatcher() {
		return null;
	}

	@Override
	public Matcher<IUpdateContext, IUpdateFeature> getUpdateMatcher() {
		return null;
	}

}

/**
 * Defines how the links (relations) are drawn
 */
class EventBRelationshipAddFeature extends AbstractAddFeature {
	
	/**
	 * Label of the connection displayed by this addFeature
	 */
	private String label;
	
	//For creating the polyline arrow
	private Polyline createPolylineArrow(GraphicsAlgorithmContainer container) {
		IGaService gaService = Graphiti.getGaService();
		Polygon polygon =
				gaService.createPolygon(container, new int[] { -12, 10, 1, 0, -12, -10 });
		polygon.setForeground(manageColor(IColorConstant.BLACK));
		polygon.setBackground(manageColor(IColorConstant.BLACK));
		polygon.setLineWidth(1);
		polygon.setFilled(true);
		return polygon;
	}

	private Text createLabel(GraphicsAlgorithmContainer container) {
		IGaService gaService = Graphiti.getGaService();
		Text text = gaService.createText(container);
		text.setForeground(manageColor(IColorConstant.BLACK));
		gaService.setLocation(text, 10, 0);
		text.setValue(label);
		return text;
	}

	public EventBRelationshipAddFeature(IFeatureProvider fp, EventBRelation eventBRelation) {
		super(fp);
		//store the label text for the connection that will be drawn
		if(eventBRelation instanceof MachineRefinesRelation) {
			this.label = "refines";
		} else if(eventBRelation instanceof MachineSeesRelation) {
			this.label = "sees";
		} else if(eventBRelation instanceof ContextExtendsRelation) {
			this.label = "extends";
		}
	}

	@Override
	public PictogramElement add(IAddContext context) {
		final IAddConnectionContext addConContext = (IAddConnectionContext) context;
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();

		// CONNECTION WITH POLYLINE
		final Connection connection = peCreateService
				.createFreeFormConnection(this.getDiagram());
		connection.setStart(addConContext.getSourceAnchor());
		connection.setEnd(addConContext.getTargetAnchor());

		final IGaService gaService = Graphiti.getGaService();
		final Polyline polyline = gaService.createPolyline(connection);
		polyline.setLineWidth(new Integer(2));
		polyline.setForeground(this.manageColor(IColorConstant.RED));

		//label of the connection
		ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
		createLabel(textDecorator);

		//"triangle" at the end of the arrow
		ConnectionDecorator conDeco;
		conDeco = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
		createPolylineArrow(conDeco);

		// create link and wire it
		this.link(connection, context.getNewObject());
		
		return connection;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context instanceof IAddConnectionContext && context.getNewObject() instanceof EventBRelation) {
			return true;
		}
		return false;
	}

}

/**
 * Defines how other types of links are drawn
 */
class GenericRelationshipAddFeature extends AbstractAddFeature {

	/**
	 * Name of the eReference drawn by this GenericRelationshipAddFeature.
	 * Displayed as the connection's label.
	 */
	private String objectShownName;

	public GenericRelationshipAddFeature(IFeatureProvider fp, EReference eReferenceToDraw) {
		super(fp);
		this.objectShownName = eReferenceToDraw.getName();
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context instanceof IAddConnectionContext) {
			return true;
		}
		return false;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		final IAddConnectionContext addConContext = (IAddConnectionContext) context;
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();

		// CONNECTION WITH POLYLINE
		final Connection connection = peCreateService
				.createFreeFormConnection(this.getDiagram());
		connection.setStart(addConContext.getSourceAnchor());
		connection.setEnd(addConContext.getTargetAnchor());

		final IGaService gaService = Graphiti.getGaService();
		final Polyline polyline = gaService.createPolyline(connection);
		polyline.setLineWidth(new Integer(2));
		polyline.setForeground(this.manageColor(IColorConstant.GRAY));

		//label of the connection
		ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
		createLabel(textDecorator);

		//"triangle" at the end of the arrow
		ConnectionDecorator conDeco;
		conDeco = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
		createPolylineArrow(conDeco);

		// create link and wire it
		this.link(connection, context.getNewObject());
		
		return connection;
	}

	private Polyline createPolylineArrow(GraphicsAlgorithmContainer container) {
		IGaService gaService = Graphiti.getGaService();
		Polygon polygon =
				gaService.createPolygon(container, new int[] { -12, 10, 1, 0, -12, -10 });
		polygon.setForeground(manageColor(IColorConstant.BLACK));
		polygon.setBackground(manageColor(IColorConstant.BLACK));
		polygon.setLineWidth(1);
		polygon.setFilled(true);
		return polygon;
	}

	private Text createLabel(GraphicsAlgorithmContainer container) {
		IGaService gaService = Graphiti.getGaService();
		Text text = gaService.createText(container);
		text.setForeground(manageColor(IColorConstant.BLACK));
		gaService.setLocation(text, 10, 0);
		text.setValue(objectShownName);
		return text;
	}

}


/**
 * Describes how the deletion of a relation must be handled
 */
class EventBRelationshipDeleteFeature extends org.eclipse.graphiti.ui.features.DefaultDeleteFeature {

	public EventBRelationshipDeleteFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected void deleteBusinessObject(Object bo) {
		final EventBRelation r = (EventBRelation) bo;
		r.delete();
	}

}
