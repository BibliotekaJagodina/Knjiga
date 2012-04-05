package ivica.client.mypanel;

import com.google.gwt.user.client.ui.*;
import java.util.*;
import com.google.gwt.user.client.*; 
import ivica.client.*;
//import com.google.gwt.user.client.Timer;
import ivica.client.helper.*;
public abstract class MyPanelLayout extends Composite
{
//	public static final int CHECK_PERIOD=250;
	private int m_width=0;
	private int m_height=0;
	private int m_commonSize=0;
	
	
	private boolean m_loaded=false;
	
/*	private int m_minWidth=0;
	private int m_minHeight=0;*/
	
	private int m_sizeCumul=0;
	private int m_absoluteWidth=0;
	private int m_absoluteHeight=0;
	
	
	private boolean m_topLevel=false;

	private ArrayList m_slots=new ArrayList();
	private ArrayList m_widgetSizeSlots=null;
	
	private Slot m_frontSlot=null;
	private AbsolutePanel m_canvas=new AbsolutePanel();

	private ArrayList m_listeners=null;
	
	private boolean m_horizontal=true;
	
//	private MyTimer m_timer=null;
	
/*	protected class MyTimer extends Timer
	{
		public void run()
		{
			if(isWidgetSizeSlotChanged())
				doVisit(new ResizeVisitor());
		}
	}*/
	
	protected abstract class Slot
	{
		
		protected Widget m_widget=null;	

		private int m_size;
		private int m_minWidth;
		private int m_minHeight;
				
		protected int m_heightOffsetDiference=0;
		protected int m_widthOffsetDiference=0;
		
		protected int getHeightOffsetDiference()
		{
			//Assertion.asert(m_loaded, "razlika u offsetu nije poznata dok se ne ucita widget");
			return m_heightOffsetDiference;
		}
		
		protected int getWidthOffsetDiference()
		{
			return m_widthOffsetDiference;
		}
		
		protected void bringToFront()
		{
			m_canvas.remove(m_widget);
			attachWidget();
		}
		
		protected Slot getPrevious()
		{
			int index=m_slots.indexOf(this);
			if(index<=0)
				return null;
			index--;
			return (Slot)m_slots.get(index);
		}
		
		protected int getOffsetHeight()
		{
			return m_widget.getOffsetHeight();
		}
		
		protected int getOffsetWidth()
		{
			return m_widget.getOffsetWidth();
		}
		
		protected int getEndPosition()
		{
			if(!m_horizontal)
				return m_canvas.getWidgetTop(m_widget)+m_widget.getOffsetHeight();
			else
				return m_canvas.getWidgetLeft(m_widget)+m_widget.getOffsetWidth();
		}

		
		protected void setWidgetSize(int initialSize)
		{
			if(m_horizontal)
				m_widget.setPixelSize(initialSize, m_height);
			else
				m_widget.setPixelSize(m_width, initialSize);
		}
		
		protected int getWidgetOffsetSize()
		{
			if(m_horizontal)
				return m_widget.getOffsetWidth();
			else
				return m_widget.getOffsetHeight();
				
		}
		
		protected void positionNextToPrevious()
		{
			Slot previous=getPrevious();
			int left=0;
			int top=0;
			// |horizontal| |horizontal|...|horizontal| 
			if(m_horizontal)
			{
				if(previous!=null)
					left=previous.getEndPosition();
			}
			else//vertical
			{
				if(previous!=null)
					top=previous.getEndPosition();
			}
			m_canvas.setWidgetPosition(m_widget, left, top);
		}
		
		/**
		 * dodaje Widget na panel iza poslednje dodatog
		 */
		protected  void attachWidget()
		{	
			m_canvas.add(m_widget);
			positionNextToPrevious();
		}
		
		
		protected void updateLeft()
		{
			Slot previous=getPrevious();
			int left=0;
			int top=0;
			// |horizontal| |horizontal|...|horizontal| 
			if(m_horizontal)
			{
				if(previous!=null)
					left=previous.getEndPosition();
			}
			else//vertical
			{
				if(previous!=null)
					top=previous.getEndPosition();
			}
			m_canvas.setWidgetPosition(m_widget, left, top);
			
		}
		
		protected void calculateOffsetDiference(int desiredSize)
		{
			if(m_horizontal)
			{
				m_heightOffsetDiference=m_widget.getOffsetHeight()-m_height;
				m_widthOffsetDiference=m_widget.getOffsetWidth()-desiredSize;
			}
			else
			{
				m_heightOffsetDiference=m_widget.getOffsetHeight()-desiredSize;
				m_widthOffsetDiference=m_widget.getOffsetWidth()-m_width;
			}
		}
		
		protected void setWidgetOffsetSize(int pixelSize)
		{
			Assertion.asert(m_widget.getParent()==m_canvas, "Widget mora biti postavljen na panel da bi mogao da mu se podesi offset");
			int width,height;
			if(m_horizontal)
			{
				width=pixelSize-m_widthOffsetDiference;
				height=m_height-m_heightOffsetDiference;
			}
			else
			{
				width=m_width-m_widthOffsetDiference;
				height=pixelSize-m_heightOffsetDiference;				
			}

			m_widget.setPixelSize(width, height);				
		}
			
		/**
		 * podešava dimenziju koja je zajednička za sve komponente
		 * (visinu ili širinu u zavisnosti da li je horizontalna ili vertikalna komponenta)
		 * tako da sve komponente budu poravnate 
		 */
		protected void adjustCommonOffsetSize()
		{			
			if(m_horizontal)
			{
				int height=m_commonSize-m_heightOffsetDiference;
				//m_widget.setHeight((height-m_heightOffsetDiference)+"px");
				int width=m_widget.getOffsetWidth()-m_widthOffsetDiference;
				m_widget.setPixelSize(width, height);
			}
			else
			{	
				int width=m_commonSize-m_widthOffsetDiference;
				int height=m_widget.getOffsetHeight()-m_heightOffsetDiference;
				m_widget.setPixelSize(width, height);				
			}
		}
		
		protected void setWidget(Widget w)
		{
			m_widget=w;
		}
				
		protected void setSize(int size)
		{
			m_size=size;
		}
		
		protected int getSize()
		{
			return m_size;
		}
		protected int getMinWidth()
		{
			return m_minWidth;
		}
		protected int getMinHeight()
		{
			return m_minHeight;
		}
		
		protected void setMinWidth(int minWidth)
		{
			m_minWidth=minWidth;
		}
		
		protected void setMinHeight(int minHeight)
		{
			m_minHeight=minHeight;
		}
		
		protected int getMinSize()
		{
			if(m_horizontal)
				return m_minWidth;
			else
				return m_minHeight;
		}
		
	}
	

	protected class AbsoluteSizeSlot extends Slot
	{
		public void attachWidget()
		{
			Assertion.asert(getSize()>=getMinSize(), "Velicina ne sme biti manja od minimalne velicine");
			setWidgetSize(this.getSize());
			super.attachWidget();
			calculateOffsetDiference(getSize());
			setWidgetOffsetSize(getSize());
		}
	}
	
	protected class RelativeSizeSlot extends Slot
	{
		private int m_pixelSize;
		
		public void setWidgetOffsetSize(int pixelSize)
		{
			m_pixelSize=pixelSize;
			if(m_pixelSize<getMinSize())
				m_pixelSize=getMinSize();
			super.setWidgetOffsetSize(m_pixelSize);
		}
		
		public void attachWidget()
		{
			setWidgetSize(this.getMinSize());
			super.attachWidget();
			calculateOffsetDiference(getMinSize());
		}
	}
	
	protected class WidgetSizeSlot extends Slot
	{
		public void attachWidget()
		{
			super.attachWidget();
			setMinHeight(m_widget.getOffsetHeight());
			setMinWidth(m_widget.getOffsetWidth());
		}
		
		public void setOffsetSize(int size)
		{
			Assertion.asert(false, "velicina WidgetSizeSlotu se ne sme menjati!");
		}
	}
	
	protected abstract class SlotVisitor
	{
		public abstract void visit(Slot s);
		public abstract void complete();
	}
	
	protected class LoadVisitor extends SlotVisitor
	{	
		private int m_realAbsoluteWidth=0;
		private int m_realAbsoluteHeight=0;
			
		//Load visitor visit
		public void visit(Slot s)
		{	
			s.attachWidget();
			if(s instanceof WidgetSizeSlot)
			{
				s.setSize(s.getWidgetOffsetSize());
			}
			if( ! (s instanceof RelativeSizeSlot))
			{
				if(m_horizontal)
				{
					m_realAbsoluteWidth+=s.getOffsetWidth();
					m_realAbsoluteHeight=Math.max(m_realAbsoluteHeight, s.getOffsetHeight());
				}
				else
				{
					m_realAbsoluteWidth=Math.max(m_realAbsoluteWidth,s.getOffsetWidth());
					m_realAbsoluteHeight+=s.getOffsetHeight();
				}
			}
		}
		
		public void complete()
		{
			if(m_frontSlot!=null)
				m_frontSlot.bringToFront();
			m_absoluteHeight=m_realAbsoluteHeight;
			m_absoluteWidth=m_realAbsoluteWidth;
			m_loaded=true;
		}
		
	}
	
	protected class ResizeVisitor extends SlotVisitor
	{
		private int m_realHeight=0;
		private int m_realWidth=0;
		private int m_realCommonSize=0;
		boolean m_newResize=false;
		
		public ResizeVisitor()
		{
			if(m_horizontal)
				m_realCommonSize=m_height;
			else
				m_realCommonSize=m_width;
		}
		
		public void visit(Slot s)
		{
			if(s instanceof RelativeSizeSlot)
			{
				int pixelSize=calculateRelativeSize(s);
				s.setWidgetOffsetSize(pixelSize);
				s.adjustCommonOffsetSize();
			}
			else if(s instanceof AbsoluteSizeSlot)
			{
				//promeniti velicinu koja je zajednicka za sve widgete
				s.adjustCommonOffsetSize();
			}
			else if(s instanceof WidgetSizeSlot)
			{
				//s.adjustCommonOffsetSize();
				if(s.getSize()!=s.getWidgetOffsetSize())
				{							
					int oldSize=s.getSize();
					
					if(m_horizontal)
						m_absoluteWidth += (s.getOffsetWidth()-oldSize);				
					else
						m_absoluteHeight += (s.getOffsetHeight()-oldSize);
					
					s.setSize(s.getWidgetOffsetSize());		
					s.setMinHeight(s.getOffsetHeight());
					s.setMinWidth(s.getOffsetWidth());
					m_newResize=true;
				}
			}
			if(m_horizontal)
			{
				m_realWidth+=s.getOffsetWidth();
				m_realHeight=Math.max(m_realHeight, s.getOffsetHeight());
				m_realCommonSize=Math.max(m_realCommonSize,s.getOffsetHeight());
			}
			else
			{
				m_realWidth=Math.max(m_realWidth,s.getOffsetWidth());
				m_realHeight+=s.getOffsetHeight();
				m_realCommonSize=Math.max(m_realCommonSize,s.getOffsetWidth());

			}
			s.positionNextToPrevious();
		}

		public void complete()
		{			
			if(m_realCommonSize > m_commonSize)
			{
				m_commonSize=m_realCommonSize;
				m_newResize=true;
			}
									
			m_canvas.setPixelSize(m_realWidth, m_realHeight);
			if(m_topLevel)
			{
				if(getOffsetHeight()>Window.getClientHeight() ||
						getOffsetWidth()>Window.getClientWidth())
					Window.enableScrolling(true);
				else
					Window.enableScrolling(false);				
			}
			notifyListeners();
			if(m_newResize)
				doVisit(new ResizeVisitor());
		}
	}
	
	
	public MyPanelLayout(boolean topLevel, boolean horizontal) 
	{
		m_topLevel=topLevel;
		m_horizontal=horizontal;
		
		if(m_topLevel)
		{
			Window.setMargin("0px");
			Window.enableScrolling(false);
			m_height=Window.getClientHeight();
			m_width=Window.getClientWidth();		
		}
		initWidget(m_canvas);
	}
	
	private int calculateRelativeSize(Slot s)
	{
		int size;
		if(m_horizontal)
		{				
			//izracunati koliki deo ostaje za komponente sa
			//relativnim dimenzijama
			int relativeWidth = m_width-m_absoluteWidth;
			//izracunati koliko pripada proporcionalno ovoj komponenti
			size=(int)( ((double)relativeWidth/
						  (double)(m_sizeCumul)) * 							 
						  (double)(s.getSize()) );
		}
		else //vertikalni
		{
			int relativeHeight = m_height-m_absoluteHeight;
			size=(int)( ((double)relativeHeight/
					  (double)(m_sizeCumul)) * 							 
					  (double)(s.getSize()) );
		}
		return size;
	}
	
	
	private void addSlot(Slot s, Widget w, int size, int minWidth, int minHeight)
	{
		s.setWidget(w);
		s.setSize(size);
		s.setMinHeight(minHeight);
		s.setMinWidth(minWidth);
		m_slots.add(s);
	}
	
	public void addRelativeSizeSlot(Widget w, int size, int minWidth, int minHeight)
	{
		Slot s=new RelativeSizeSlot();
		addSlot(s,w,size,minWidth,minHeight);	
		m_sizeCumul += s.getSize();
	}
	
	public void addAbsoluteSizeSlot(Widget w, int size, int minWidth, int minHeight)
	{
		Slot s=new AbsoluteSizeSlot();
		addSlot(s,w,size,minWidth,minHeight);		
	}
	
	public void addWidgetSizeSlot(Widget w)
	{
		Slot s=new WidgetSizeSlot();
		addSlot(s,w,0,0,0);	
		if(m_widgetSizeSlots==null)
			m_widgetSizeSlots=new ArrayList();
		m_widgetSizeSlots.add(s);
	}
	
	public boolean isWidgetSizeSlotChanged()
	{
		if(m_widgetSizeSlots==null)
			return false;
		for(int i=0;i<m_widgetSizeSlots.size();i++)
		{
			Slot s=(Slot)m_widgetSizeSlots.get(i);
			if(s.getSize()!=s.getWidgetOffsetSize())
				return true;
		}
		return false;
	}
	
	public void onLoad()
	{
//		if(m_topLevel)
//		{
//			if(m_widgetSizeSlots!=null)
//			{
//				m_timer=new MyTimer();
//				m_timer.scheduleRepeating(CHECK_PERIOD);
//			}
//		}
		doVisit(new LoadVisitor());
		doVisit(new ResizeVisitor());
	}
	
	public void refresh()
	{
		doVisit(new ResizeVisitor());
	}
	
	protected void doVisit(SlotVisitor v)
	{
		for(int i=0;i<m_slots.size();i++)
			v.visit((Slot)m_slots.get(i));
		v.complete();
	}
	
	
	public void addChangeListener(LayoutChangeListener listener)
	{
		if(m_listeners==null)
			m_listeners=new ArrayList();
		//ako nije u listi onda se dodaje
		if(!(m_listeners.indexOf(listener)>=0))
			m_listeners.add(listener);	
	}
	
	public void removeChangeListener(LayoutChangeListener listener)
	{
		m_listeners.remove(listener);
	}
	
	private void notifyListeners()
	{
		if(m_listeners!=null)
		{
			for(int i=0;i<m_listeners.size();i++)
			{
				LayoutChangeListener l=(LayoutChangeListener)m_listeners.get(i);
				l.onLayoutChange();
			}
		}
	}
	
	//Uradi - overarjdovati nesto od ovoga ili sve?
	
	public void setWidth(String width)
	{
		//Assertion.asert(false, "funkcija nije implementirana!");
		m_width=Helper.getPixelSize(width);
		if(!m_horizontal)
			m_commonSize=m_width;
		if(m_loaded)
			doVisit(new ResizeVisitor());
	}
	public void setHeight(String height)
	{
		//Assertion.asert(false, "funkcija nije implementirana!");
		m_height=Helper.getPixelSize(height);
		if(m_horizontal)
			m_commonSize=m_height;
		if(m_loaded)
			doVisit(new ResizeVisitor());
	}
	
	//obavezno overrajdovati!
	public void setPixelSize(int width, int height)
	{
		m_width=width;
		m_height=height;
		
		if(m_horizontal)
			m_commonSize=m_height;
		else
			m_commonSize=m_width;
//		sizeCorrection();		
		if(m_loaded)
			doVisit(new ResizeVisitor());
	}
	
	public void setSize(String width, String height)
	{
		Assertion.asert(false, "funkcija nije implementirana!");
	}		
	
	public void bringToFrontSlot(int index)
	{
		if(index<0 || index>=m_slots.size())
			return;
		Slot s=(Slot)m_slots.get(index);
		m_frontSlot=s;		
	}
	
	private void resizeSlot(Slot s,int size)
	{
		int oldOffsetHeight=s.getOffsetHeight();
		int oldOffsetWidth=s.getOffsetWidth();
		int oldSize=s.getSize();
		
		s.setSize(size);
		if(s instanceof AbsoluteSizeSlot)
		{			
			s.setWidgetOffsetSize(size);
			
			m_absoluteHeight+= (s.getOffsetHeight()-oldOffsetHeight);
			m_absoluteWidth+= (s.getOffsetWidth()-oldOffsetWidth);				
		}
		else if(s instanceof RelativeSizeSlot)
		{
			m_sizeCumul+=(size-oldSize);
		}
		else //if(s instanceof WidgetSizeSlot)
		{
			Assertion.asert(false, "Promena velicine WidgetSizeSlot-u nije implementirana");
		}
		doVisit(new ResizeVisitor());	
	}
	
	public void resizeSlot(int index, int size)
	{
		if(index<0 || index>=m_slots.size())
			return;
		
		Slot s=(Slot)m_slots.get(index);		
		resizeSlot(s, size);
	
	}
	
	protected AbsolutePanel getCanvas()
	{
		return m_canvas;
	}
	
	public int getSlotMinSize(int index)
	{
		Slot s=(Slot)m_slots.get(index);
		return s.getMinSize();
	}
	
	protected int getWidgetHeightOffsetDiference(int slotIndex)
	{
		Slot s=(Slot)m_slots.get(slotIndex);
		return s.getHeightOffsetDiference();
	}
	
	protected int getWidgetWidthOffsetDiference(int slotIndex)
	{
		Slot s=(Slot)m_slots.get(slotIndex);
		return s.getWidthOffsetDiference();
	}
	//TODO proveri 
	//{
	
/*	private void initVars()
	{
		m_width=0;
		m_height=0;
		m_commonSize=0;
		m_loaded=false;		
		m_sizeCumul=0;
		m_absoluteWidth=0;
		m_absoluteHeight=0;
	}*/
	
	public void removeSlot(int index)
	{
		Slot s = (Slot)m_slots.get(index);
		m_sizeCumul -= s.getSize();
		s.m_widget.removeFromParent();
		m_slots.remove(index);
//		initVars();
//		m_canvas.clear();
		onLoad();
	}
	
	private void insertSlot(int index, Slot s, Widget w, int size, int minWidth, int minHeight)
	{
		s.setWidget(w);
		s.setSize(size);
		s.setMinHeight(minHeight);
		s.setMinWidth(minWidth);
		m_slots.add(index, s);
	}
	
	public void insertRelativeSizeSlot(int index, Widget w, int size, int minWidth, int minHeight)
	{
		Slot s = new RelativeSizeSlot();
		insertSlot(index, s,w,size,minWidth,minHeight);	
		m_sizeCumul += s.getSize();
//		initVars();
//		m_canvas.clear();
		onLoad();
	}
	//}
}
