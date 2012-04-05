package ivica.client.mypanel;

import com.google.gwt.user.client.DOM;
/*import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;*/
import com.google.gwt.user.client.ui.*;

import ivica.client.helper.Helper;
import ivica.client.viewport.*;

public class MyHorizontalSpliter extends MyHorizontalLayout
{
	public static final int SPLITER_SIZE=4;

	private MousePanel m_spliter=new MousePanel();
	private MousePanel m_dragSpliter=null;
	
	private MousePanel m_mousePanel=null;
	private boolean m_dragging = false;
	private int m_xOffset;
	
	private int m_splitPosition;
	private boolean m_loaded = false;
	private boolean m_dragEnabled = true;
	public MyHorizontalSpliter(boolean topLevel,Widget left, Widget right,int splitPosition, int minHeight, int minLeftWidth,int minRightWidth)
	{
		super(topLevel);
		
		m_splitPosition=splitPosition;
		addAbsoluteSizeSlot(left, splitPosition,minLeftWidth,minHeight);
		addAbsoluteSizeSlot(m_spliter,SPLITER_SIZE,0, minHeight);
		addRelativeSizeSlot(right, 1, minRightWidth,minHeight);

		bringToFrontSlot(1); 
		
		m_spliter.addStyleName("hSpliterBar");


		m_spliter.addMouseListener(new MouseListenerAdapter()
		{
			public void onMouseEnter(Widget widget)
			{
				//DOM.addEventPreview(Helper.preventDefaultMouseEvents);
				Helper.getPreventMouseEvents().Start();
				if(!m_dragEnabled)
					return;
				setStyleName("EastCursor");
			}
			public void onMouseLeave(Widget widget)
			{
				//DOM.removeEventPreview(Helper.preventDefaultMouseEvents);
				Helper.getPreventMouseEvents().Stop();
				if(!m_dragEnabled)
					return;
				removeStyleName("EastCursor");
			}
			public void onMouseDown(Widget widget, int x, int y)
			{
				if(!m_dragEnabled)
					return;
				setStyleName("EastCursor");
				//dodaj Mouse Panel preko celog canvasa
				m_dragging=true;
				m_mousePanel=new MousePanel();
				
				AbsolutePanel canvas=getCanvas();
				
				m_mousePanel.setPixelSize(canvas.getOffsetWidth(),canvas.getOffsetHeight());
				canvas.add(m_mousePanel, 0, 0);
				m_dragSpliter=new MousePanel();
				m_dragSpliter.addStyleName("hDragSpliterBar");
				int dragHeight=m_spliter.getOffsetHeight()-getWidgetHeightOffsetDiference(1);
				m_dragSpliter.setPixelSize(SPLITER_SIZE, dragHeight);
				int left=canvas.getWidgetLeft(m_spliter);
				int top=canvas.getWidgetTop(m_spliter);
				canvas.add(m_dragSpliter, left, top);
				
				//postavi capture za mouse panel
				DOM.setCapture(m_mousePanel.getElement());
				m_xOffset=canvas.getWidgetLeft(m_spliter)+x;
				
				m_mousePanel.addMouseListener(new MouseListenerAdapter()
				{
					public void onMouseMove(Widget sender, int x, int y)
					{
						if(m_dragging)
						{													
							int delta=m_xOffset-x;							
							AbsolutePanel parent=getCanvas();
							int left=parent.getWidgetLeft(m_dragSpliter)-delta;
							int top=parent.getWidgetTop(m_dragSpliter);
							

						//spreciti smanjivanje desnog dela prozora ispod minimalne dimenzije
							int rightMinSize=getSlotMinSize(2);
							int rightSize=getOffsetWidth()-(left+m_spliter.getOffsetWidth());
							if(rightSize<rightMinSize)
							{
//								left=getOffsetWidth()-m_spliter.getOffsetWidth()-rightMinSize;
								return;
							}
						//spreciti smanjivanje levog dela prozora ispod minimalne dimenzije
							int leftMinSize=getSlotMinSize(0);
							if(left<leftMinSize)
							{
								return;
							}							
							
							
							parent.setWidgetPosition(m_dragSpliter, left, top);
							m_xOffset=x;
						}
					}
					public void onMouseUp(Widget widget, int x, int y)
					{
						if(!m_dragEnabled)
							return;
						m_dragging=false;
						DOM.releaseCapture(m_mousePanel.getElement());
						getCanvas().remove(m_mousePanel);
						m_mousePanel=null;
						setSplitPosition(getCanvas().getWidgetLeft(m_dragSpliter));
						getCanvas().remove(m_dragSpliter);
						m_dragSpliter=null;
						removeStyleName("EastCursor");
					}
				});
			}
		});
	
	}
	

	
	
	public void setDragEnabled(boolean enabled)
	{
		m_dragEnabled = enabled;
	}
	
	public boolean isDragEnabled()
	{
		return m_dragEnabled;
	}
	
	public int getSplitPosition()
	{
		return getCanvas().getWidgetLeft(m_spliter);
	}
	
	public void onLoad()
	{
		super.onLoad();
		m_loaded = true;
		setSplitPosition(this.m_splitPosition);
	}
	
	public void setSplitPosition(int position)
	{
		if(! m_loaded)
			return;
		if(position<0)
			position=0;
		resizeSlot(0, position);	
	}
}
