package ivica.client.mypanel;

import com.google.gwt.user.client.ui.*;
import java.util.*;
//import ivica.client.Assertion;
import ivica.client.helper.Helper;
/**
 * 
 * @author ivica.lazarevic
 *<b>Ne sme da ima stil sa postavljenim okvirom (border), stil treba da ima komponenta koju sadrzi</b>
 */
public abstract class MultyView extends Composite 
{
	private HashMap<Object,Object> m_widgets=null;
	protected Object m_currentViewKey=null;
	private boolean m_loaded=false;
	
	
	protected abstract void attachWidget(Widget w);
	protected Widget getWidget(Object key)
	{
		if(m_widgets == null)
			return null;
		View v = (View)m_widgets.get(key);
		if(v == null)
			return null;
		return v.view;
	}
	
	protected void onViewShowed(Widget w)
	{
		
	}
	
	public String getLabel(Object key)
	{
		if(m_widgets == null)
			return null;
		View v = (View)m_widgets.get(key);
		if(v == null)
			return null;
		return v.label;
	}
	
	public void showView(Object key)
	{		
		if(!m_loaded)
		{
			m_currentViewKey = key;
			return;
		}
		//sakrij trenutno prikazani
		if(m_currentViewKey != null)
		{
			Widget toHide=getWidget(m_currentViewKey);
			if(toHide != null)
				if(toHide.getParent() != null)
				{
					toHide.removeFromParent();
					if(toHide instanceof MultyShowListener)
					{
						MultyShowListener sl = (MultyShowListener)toHide;
						sl.onHide();
					}
				}
		}
		
		// ako je kljuc null onda se ne prikazuje novi pogled
		// a stari je uklonjen ukoliko je postojao
		if(key == null)
			return;
		//prikazi novi
		Widget toShow=getWidget(key);
		if(toShow !=null)
		{
			m_currentViewKey=key;
			attachWidget(toShow);	
			Helper.setWidgetOffsetSize(toShow, this.getOffsetWidth(), this.getOffsetHeight());
			onViewShowed(toShow);
			if(toShow instanceof MultyShowListener)
			{
				MultyShowListener sl = (MultyShowListener)toShow;
				sl.onShowView();
			}
		}
	}
	
	public void onLoad()
	{
		m_loaded=true;
		if(this.m_currentViewKey != null)
			this.showView(m_currentViewKey);
	}
	
	private class View
	{
		public View(Widget view, String label)
		{
			this.label = label;
			this.view = view;
		}
		public String label;
		public Widget view;
	}
	
	public void addView(Object key, Widget w, String label)
	{
		if(m_widgets == null)
			m_widgets = new HashMap<Object,Object>();
		
		m_widgets.put(key, new View(w,label));		
	}	

	private void adjustCurrentWidgetSize()
	{
		Widget current=getWidget(this.m_currentViewKey);
		if(current != null)
			Helper.setWidgetOffsetSize(current, this.getOffsetWidth(), this.getOffsetHeight());		
	}
	
	public void setPixelSize(int width, int height)
	{
		super.setPixelSize(width, height);
		adjustCurrentWidgetSize();
	}
	public void setHeight(String height)
	{
		super.setHeight(height);
		adjustCurrentWidgetSize();		
	}
	public void setWidth(String width)
	{
		super.setWidth(width);
		adjustCurrentWidgetSize();
	}	
}
