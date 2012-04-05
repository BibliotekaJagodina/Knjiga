package ivica.client.viewport;



import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import ivica.client.helper.*;
/*
 * Klasa Viewport se koristi za skrolovanje elemenata koji ne mogu celi da 
 * stanu u prozor. Standardni ScrollPanel se prosiruje tako da je moguće
 * i pomeranje prikaza misem.
 * */

public class Viewport extends  ScrollPanel 
{
	
	private static final String DEFAULT_MOUSE_DOWN_CURSOR = "moveCursor";
	private static final String DEFAULT_MOUSE_DRAG_CURSOR = "pointerCursor";
	
	private String mouseDownCursor = DEFAULT_MOUSE_DOWN_CURSOR;
	private String mouseDragCursor = DEFAULT_MOUSE_DRAG_CURSOR;
	
	private final MousePanel mousePanel = new MousePanel();

	private Widget view = null;
	private boolean dragging = false;
	private int xoffset, yoffset;
	
	
	public Viewport() 
	{
		setStyleName("Viewport");
		setWidget(mousePanel);
		
		mousePanel.addMouseListener(new MouseListenerAdapter(){
				public void onMouseEnter(Widget sender) 
				{
					Helper.getPreventMouseEvents().Start();
				}
					
				public void onMouseLeave(Widget sender) 
				{
					Helper.getPreventMouseEvents().Stop();
				}
				
				public void onMouseDown(Widget widget, int x, int y) 
				{	
					dragging = true;
					xoffset = x;
					yoffset = y;
					RootPanel.get().getElement().focus();//hocu da inputi izgube fokus
					DOM.setCapture(mousePanel.getElement());
					
				}
				
				public void onMouseMove(Widget widget, int x, int y) 
				{
					if (dragging)
					{
						int newX = getHorizontalScrollPosition()+ (xoffset-x);
						int newY = getScrollPosition() + (yoffset-y);
												
						setHorizontalScrollPosition(newX);
						setScrollPosition(newY);
					}
				}
					
				public void onMouseUp(Widget widget, int x, int y) 
				{
					if (dragging) 
					{
						dragging = false;
					}
					DOM.releaseCapture(mousePanel.getElement());
					onClick(x,y);
				}
			});
	}
		
	
	public void onClick(int x, int y)
	{
	}
		
	public String getMouseDownCursor() 
	{
		return mouseDownCursor;
	}
		
	public void setMouseDownCursor(String mouseDownCursor) 
	{
		this.mouseDownCursor = mouseDownCursor;
	}
		
	public String getMouseDragCursor() 
	{
		return mouseDragCursor;
	}
		
	public void setMouseDragCursor(String mouseDragCursor) 
	{
		this.mouseDragCursor = mouseDragCursor;
	}
		
	public Widget getView() 
	{
		return view;
	}
	
	public void setView(Widget view) 
	{
			mousePanel.setWidget(view);
	}

}
