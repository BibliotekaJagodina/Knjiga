package ivica.client.helper;

import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.*;
public class Helper 
{
	public static class MySimplePanel extends SimplePanel
	{
		
	}
	
	public static boolean isPointInRect(int rectLeft,int rectTop, 
										int rectWidth,int rectHeight, 
										int x, int y)
	
	{
		if(x>rectLeft && x<rectLeft+rectWidth && y>rectTop && y<rectTop+rectHeight)
			return true;
		return false;
	}
	
	public static int getCentredLeft(int pageWidth, Widget canvas)
	{
		int panelWidth=canvas.getOffsetWidth();
		return Helper.getCentredLeft(pageWidth, panelWidth);
	}
	
	public static int getCentredLeft(int pageWidth, int panelWidth)
	{
		if(pageWidth < panelWidth)
		{
			int left=(panelWidth-pageWidth)/2;
			return left;
		}
		else
			return 0;
	}
	
	public static int getCentredTop(int pageHeight, Widget canvas)
	{
		int panelHeight=canvas.getOffsetHeight();
		return Helper.getCentredTop(pageHeight, panelHeight);
	}
	public static int getCentredTop(int pageHeight, int panelHeight)
	{
		if(pageHeight < panelHeight)
		{
			int top=(panelHeight-pageHeight)/2;
			return top;
		}
		else
			return 0;
	}
	
	
	public static void setWidgetOffsetHeight(Widget w, int height)
	{
		setWidgetOffsetSize(w,w.getOffsetWidth(),height);
	}
	
	public static void setWidgetOffsetWidth(Widget w, int width)
	{
		setWidgetOffsetSize(w, width, w.getOffsetHeight());
	}
	

	public static class returnInteger
	{
		public int intVal;
	}
	
	public static void setWidgetOffsetSize(Widget w, int width, int height, returnInteger clientWidth, returnInteger clientHeight)
	{
		w.setPixelSize(width, height);
		int difWidth=w.getOffsetWidth()-width;
		int difHeight=w.getOffsetHeight()-height;
		if(difWidth > 0 || difHeight > 0)
		{
			height-=difHeight;
			width-=difWidth;
			w.setPixelSize(width, height);
		}
		if(clientWidth != null)
			clientWidth.intVal = width;
		if(clientHeight != null)
			clientHeight.intVal = height;
	}
	
	public static void setWidgetOffsetSize(Widget w, int width, int height)
	{
		setWidgetOffsetSize(w,width,height,null,null);
	}
	
	public static int getPixelSize(String size)
	{
		return Integer.parseInt(size.substring(0,size.length()-2));
	}
	
	public static void setWidgetOffsetPosition(AbsolutePanel canvas, Widget w, int left, int top)
	{
		canvas.setWidgetPosition(w, left, top);
		int difLeft = canvas.getWidgetLeft(w) - left;
		int difTop = canvas.getWidgetTop(w) - top;
		if(difLeft != 0 || difTop  != 0)
		{
			left-=difLeft;
			top-=difTop;
			canvas.setWidgetPosition(w, left, top);
		}
	}
	
	public static class PreventMouseEvents
	{
		private boolean m_paused = false;
		private boolean m_started = false;
		public void Start()
		{			
			if(m_started)
				return;
			DOM.addEventPreview(preventDefaultMouseEvents);
/*			
 * ne znam kako radi
 * 	        Event.addNativePreviewHandler(new NativePreviewHandler(){
				@Override
				public void onPreviewNativeEvent(NativePreviewEvent event) {
					Event.
					if(event.getNativeEvent() &)
						event.cancel();
				}
			}
			);*/
			m_paused = false;
			m_started = true;
		}
		
		public void Stop()
		{
			if(!m_started)
				return;
			DOM.removeEventPreview(preventDefaultMouseEvents);
			m_started = false;
			m_paused = false;
		}
		
		public void Pause()
		{
			//ako nije startovan nema sta da pauzira
			if(!m_started )
				return;
			//ako je vec pauziran ne sme ponovo
			if(m_paused)
				return;
			DOM.removeEventPreview(preventDefaultMouseEvents);
			m_paused = true;
		}
		
		public void Resume()
		{
			//ako nije startovan nema sta da pokrene ponovo
			if(!m_started)
				return;
			//ako nije pauziran ne sme da pokrene ponovo
			if(!m_paused)
				return;
			DOM.addEventPreview(preventDefaultMouseEvents);
			m_paused = false;
		}
	}
	
	private static PreventMouseEvents m_preventMouseEvents = null;
	public static PreventMouseEvents getPreventMouseEvents()
	{
		if(m_preventMouseEvents == null)
			m_preventMouseEvents = new PreventMouseEvents();
		return m_preventMouseEvents;
	}
	
	private static EventPreview preventDefaultMouseEvents =
		new EventPreview()
		{ 
			public boolean onEventPreview(Event event)
			{
				switch (DOM.eventGetType(event)) 
				{
					case Event.ONMOUSEDOWN:
					case Event.ONMOUSEMOVE:
					case Event.BUTTON_LEFT:
					case Event.BUTTON_RIGHT:
					case Event.MOUSEEVENTS:
						
						DOM.eventPreventDefault(event);
				}
				return true;
			}
		};
}
