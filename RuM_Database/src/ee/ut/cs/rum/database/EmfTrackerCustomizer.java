package ee.ut.cs.rum.database;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import ee.ut.cs.rum.database.RumEmfService;
import ee.ut.cs.rum.database.internal.Activator;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EmfTrackerCustomizer implements ServiceTrackerCustomizer {
	private final BundleContext context;

	public EmfTrackerCustomizer(BundleContext context) {
		this.context = context;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		RumEmfService service = (RumEmfService) context.getService(reference);
		Activator.getLogger().info("Emf registered in bundle: " + context.getBundle().getSymbolicName());
		
		return service;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		removedService(reference, service);
		addingService(reference);
		Activator.getLogger().info("Emf modified in bundle: " + context.getBundle().getSymbolicName());
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		context.ungetService(reference);
		Activator.getLogger().info("Emf in removed bundle: " + context.getBundle().getSymbolicName());
		//TODO: Handle removal of EntityManager
	}
}
