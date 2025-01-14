package ac.soton.eventb.diagrameditor.features;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eventb.emf.core.EventBNamedCommentedElement;
import org.eventb.emf.core.Project;
import org.eventb.emf.core.context.Context;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.persistence.ProjectResource;

import ac.soton.eventb.diagrameditor.EventBDiagramFeatureProvider;
import ac.soton.eventb.diagrameditor.relations.ContextExtendsRelation;
import ac.soton.eventb.diagrameditor.relations.MachineRefinesRelation;
import ac.soton.eventb.diagrameditor.relations.MachineSeesRelation;

public class EventBProjectFeature implements IEventBFeature {

	@Override
	public boolean canAdd() {
		return false;
	}

	@Override
	public boolean canCreate() {
		return false;
	}

	@Override
	public boolean canCreateRelationship() {
		return false;
	}

	@Override
	public boolean canDelete() {
		return false;
	}

	@Override
	public boolean canDirectEdit() {
		return false;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public Matcher<IAddContext, IAddFeature> getAddMatcher() {
		return null;
	}

	@Override
	public Collection<ICreateFeature> getCreateFeatures(
			EventBDiagramFeatureProvider e) {
		return null;
	}

	@Override
	public Collection<? extends ICreateConnectionFeature> getCreateRelationshipFeatures(
			EventBDiagramFeatureProvider eventBDiagramFeatureProvider) {
		return null;
	}

	@Override
	public Matcher<IDeleteContext, IDeleteFeature> getDeleteMatcher() {
		return null;
	}

	@Override
	public Matcher<IDirectEditingContext, IDirectEditingFeature> getDirectEditingMatcher() {
		return null;
	}

	@Override
	public Matcher<IUpdateContext, IUpdateFeature> getUpdateMatcher() {
		return new Matcher<IUpdateContext, IUpdateFeature>() {

			@Override
			public IUpdateFeature getFeature(IUpdateContext o,
					EventBDiagramFeatureProvider e) {
				if (this.match(o, e)) {
					return new EventBProjectUpdateFeature(e);
				}
				return null;
			}

			@Override
			public boolean match(IUpdateContext o,
					EventBDiagramFeatureProvider e) {
				return o.getPictogramElement() instanceof Diagram;
			}
		};
	}

}

class EventBProjectUpdateFeature extends AbstractUpdateFeature {

	public EventBProjectUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		return context.getPictogramElement() instanceof Diagram;
	}

	@Override
	public boolean update(IUpdateContext context) {
		boolean updated = false;
		Project project = (Project) this
				.getBusinessObjectForPictogramElement(context
						.getPictogramElement());

		if (project == null) {
			final ProjectResource pr = ((EventBDiagramFeatureProvider) this
					.getFeatureProvider()).getProjectResource();
			project = (Project) pr.getContents().get(0);
			this.link(context.getPictogramElement(), project);
			updated = true;
		}
		
		//Remove the links graphical components (to redraw them properly)
		//ie : unregister the connections from the elements to which they are connected, then remove them
		EList<Connection> connections = this.getDiagram().getConnections();
		while(! connections.isEmpty()) {
			Connection connection = connections.get(0);
			//Then we remove the pictogram of this connection from the diagram, while unregistering cross references to this pictogram
			//to ensure that it is deallocated properly (ie : no more handles on this object, so that it can be garbage-collected)
			Graphiti.getPeService().deletePictogramElement(connection);
		}

		//Add a shape for each EventBNamedCommentedElement that exist in the project
		for (final EventBNamedCommentedElement e : project.getComponents()) {
			final AddContext ac = new AddContext();
			ac.setTargetContainer(this.getDiagram());
			ac.setNewObject(e);
			this.getFeatureProvider().addIfPossible(ac);
			
			//also, for each EventBNamedCommentedElement, we look for any relation that points to an EventBNamedCommentedElement
			//And we create a Shape for any that we find.
			TreeIterator<EObject> contents = e.eAllContents();
			while(contents.hasNext()) {
				EObject content = contents.next();
				//if the element is an EventBNamedCommentedElement, we try to display it
				if(content instanceof EventBNamedCommentedElement) {
					final AddContext addContext = new AddContext();
					addContext.setTargetContainer(this.getDiagram());
					addContext.setNewObject(content);
					this.getFeatureProvider().addIfPossible(addContext);
				}
			}
		}

		//Create the various connections
		for (final Shape s : this.getDiagram().getChildren()) {
			if (s instanceof ContainerShape) {
				final ContainerShape c = (ContainerShape) s;
				final EventBNamedCommentedElement element = (EventBNamedCommentedElement) this
						.getBusinessObjectForPictogramElement(c);
				if (element instanceof Machine) {
					final Machine m = (Machine) element;
					//For all contexts that this machine sees
					for (final Context ctx : m.getSees()) {
						//look for Shapes who represent the context that the machine m sees
						//and create the connection between the machine and its context
						for (final Shape innerShape : this.getDiagram().getChildren()) {
							if (this.getBusinessObjectForPictogramElement(innerShape) == ctx) {
								//Add anchors to the Shape that represents the Context
								final AnchorContainer a1 = s;//anchor on the machine side (s is the machine's shape)
								final AnchorContainer a2 = innerShape;//anchor on the context side
								Graphiti.getPeService().createChopboxAnchor(a1);
								Graphiti.getPeService().createChopboxAnchor(a2);
								
								//create the connection between the two anchors
								final AddConnectionContext acc = new AddConnectionContext(
										a1.getAnchors().get(0), a2.getAnchors().get(0));
								acc.setNewObject(new MachineSeesRelation(m, ctx));
								this.getFeatureProvider().addIfPossible(acc);
							}
						}
					}
					//For all machines that refine m
					for (final Machine mac : m.getRefines()) {
						//look for Shapes who represent the machine that refines m
						//and create the connection between the two machines
						for (final Shape innerShape : this.getDiagram().getChildren()) {
							if (this.getBusinessObjectForPictogramElement(innerShape) == mac) {
								final AnchorContainer a1 = s;
								final AnchorContainer a2 = innerShape;
								Graphiti.getPeService().createChopboxAnchor(a1);
								Graphiti.getPeService().createChopboxAnchor(a2);

								final AddConnectionContext acc = new AddConnectionContext(
										a1.getAnchors().get(0), a2.getAnchors().get(0));
								acc.setNewObject(new MachineRefinesRelation(m,mac));
								this.getFeatureProvider().addIfPossible(acc);
							}
						}
					}
				} else if (element instanceof Context) {
					final Context ctx1 = (Context) element;
					//For all Contexts that extend ctx1
					for (final Context ctx2 : ctx1.getExtends()) {
						//look for shapes that represent this context
						//and create the connection between the two
						for (final Shape innerShape : this.getDiagram().getChildren()) {
							if (this.getBusinessObjectForPictogramElement(innerShape) == ctx2) {

								final AnchorContainer a1 = s;
								final AnchorContainer a2 = innerShape;
								Graphiti.getPeService().createChopboxAnchor(a1);
								Graphiti.getPeService().createChopboxAnchor(a2);

								final AddConnectionContext acc = new AddConnectionContext(
										a1.getAnchors().get(0), a2.getAnchors().get(0));
								acc.setNewObject(new ContextExtendsRelation(ctx1, ctx2));
								this.getFeatureProvider().addIfPossible(acc);
							}
						}
					}
				} 
				//NOTE : this portion of code allows to draw any kind of EReference between EventBNamedCommentedElements
				if(element instanceof EventBNamedCommentedElement) {
					TreeIterator<EObject> contents = element.eAllContents();
					while(contents.hasNext()) {
						EObject content = contents.next();
						
						if(content instanceof EventBNamedCommentedElement) {
							
							//We draw links between the EventBNamedCommentedElement and its container
							EObject container = content.eContainer();
							//get the Pictograms that represent the container and the content
							PictogramElement[] containerPictograms = this.getFeatureProvider().getAllPictogramElementsForBusinessObject(container);
							PictogramElement[] contentPictograms = this.getFeatureProvider().getAllPictogramElementsForBusinessObject(content);
							
							if(containerPictograms.length >= 1 && contentPictograms.length >= 1) {
								//We get the shapes for the container and the content
								PictogramElement containerShape = containerPictograms[0];
								PictogramElement contentShape = contentPictograms[0];
								//Add anchors to the Shapes that represents the container and the content
								///XXX : potentially unsafe cast made here !
								final AnchorContainer a1 = (AnchorContainer) containerShape;//anchor on the container side
								final AnchorContainer a2 = (AnchorContainer) contentShape;//anchor on the content side
								Graphiti.getPeService().createChopboxAnchor(a1);
								Graphiti.getPeService().createChopboxAnchor(a2);
								
								//create the connection between the two anchors
								final AddConnectionContext acc = new AddConnectionContext(
										a1.getAnchors().get(0), a2.getAnchors().get(0));
								acc.setNewObject(content.eContainingFeature());
								//System.out.println("Trying to add a connection between "+((EventBNamedCommentedElement) container).getName()+" and "+((EventBNamedCommentedElement) content).getName());
								//System.out.println("Relation type : "+content.eContainingFeature().eClass()+" relation is an EventBRelation : "+(content.eContainingFeature() instanceof EventBRelation));
								this.getFeatureProvider().addIfPossible(acc);
							} else {
								//if one of the elements that we tried to link had no pictogram, we signal it via a error message
								Logger.getLogger("diagram-editor").log(Level.SEVERE, "WARNING : could not create link between "
										+ "two EventBNamedCommentedElement : " +((EventBNamedCommentedElement) container).getName()
										+" and "+((EventBNamedCommentedElement) content).getName()
										+ "\n"+"Reason : containerPictograms.length = "+containerPictograms.length
										+" contentPictograms.length = "+contentPictograms.length);
							}
						}
					}
				}
			}
		}

		return updated;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		return Reason.createTrueReason();
	}

}
