package ivica.client.viewport;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesMouseEvents;


public class MousePanel extends SimplePanel	implements SourcesMouseEvents 
{
	private MouseListenerCollection mouseListeners = null;

	public MousePanel() 
	{
		super();
	// sinkEvents, znaci da ce GWT pozivati onBrowserEvent
	//svaki put kad se event odigra u okviru ovog panela
	sinkEvents(Event.MOUSEEVENTS);
	}

	public void onBrowserEvent(Event event) 
	{
	// Dogadjaj se prosledjuje pretplatnicima ako
	// trenutno postoje registrovani
		if (mouseListeners != null)
			mouseListeners.fireMouseEvent(this, event);
		super.onBrowserEvent(event);
	}
	
	public void addMouseListener(MouseListener listener)
	{
		// Odlozena inicijalizacija kolekcije pretplatnika(listeners).
		// Vrsi se pri prvom registrovanju listenera

		if (mouseListeners == null)
			mouseListeners = new MouseListenerCollection();

		mouseListeners.add(listener);
	}
 
	public void removeMouseListener(MouseListener listener) 
	{
		if (mouseListeners != null)
			mouseListeners.remove(listener);
 }
}
