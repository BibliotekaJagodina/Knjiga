package ivica.client.mypanel;

import com.google.gwt.user.client.ui.*;
import java.util.ArrayList;
import ivica.client.helper.*;

public  class MyToolbar extends Composite implements ClickListener
{
	private  String m_toolbarHoverStyle="MyToolbarHover";
	private String m_toolbarButtonStyle="MyToolbarButton";
	private  String m_toolbarDown="MyToolbarDown";
	private AbsolutePanel m_canvas=new AbsolutePanel();
	private boolean m_horizontal;
	private int m_size;
	private int m_toolDistance;
	private ArrayList m_widgets=new ArrayList();
	private boolean m_loaded=false;
	private int m_maxToolSize=0;
	private int m_toolSize;
	private boolean m_pushToolbar=false;
	private boolean m_optionToolbar = false;

	
	private MyButton m_selected = null;
	private PushButtonChangeListener m_changeListener=null;		
	
	public static interface PushButtonChangeListener
	{
		public void onSelectedButtonChanged(MyButton btn);
	}
	
	public void setOptionToolbar()
	{
		ivica.client.Assertion.asert(m_pushToolbar && m_widgets.size() == 0, "samo push toolbar moze biti option");
		m_optionToolbar = true;
	}
	
	public MyToolbar(boolean horizontal, boolean pushToolbar, int size, int toolDistance, int buttonSize)
	{
		m_pushToolbar=pushToolbar;
		m_toolSize = buttonSize;
		m_maxToolSize = buttonSize;
		m_horizontal = horizontal;
		m_size = size;
		m_toolDistance = toolDistance;
		if(horizontal)
			m_canvas.setHeight(size + "px");
		else
			m_canvas.setWidth(size + "px");
	
		initWidget(m_canvas);
	}
		
	public void setStyles(String buttonStyleName, String buttonHoverStyleName, String buttonDownStyleName)
	{
		m_toolbarDown = buttonDownStyleName;
		m_toolbarButtonStyle = buttonStyleName;
		m_toolbarHoverStyle = buttonHoverStyleName;
	}
	public void setChangeListener(PushButtonChangeListener listener)
	{
		m_changeListener = listener;
	}
	
	public void onClick(Widget sender)
	{
		if(sender instanceof MyButton)
		{	
			MyButton btn=(MyButton)sender;
			if(m_pushToolbar)
			{				
				if(btn != m_selected)
				{
					if(m_selected != null)
						m_selected.deselect();
				}
				if(btn.isSelected())
					m_selected = btn;
				else
					m_selected = null;
				if(m_changeListener != null)
					m_changeListener.onSelectedButtonChanged(m_selected);				
			}
			btn.doCommand();
		}
	}
	
	public void deselectAllButtons()
	{
		if(m_selected != null)
		{
			m_selected.deselect();
			m_selected = null;
			m_changeListener.onSelectedButtonChanged(null);			
		}
		
	}
	
	public void selectButton(MyButton btn, boolean doCommand)
	{
		ivica.client.Assertion.asert(m_widgets.contains(btn), "Mora sadrzati dugme");
		ivica.client.Assertion.asert(m_pushToolbar, "Samo push toolbar moze da ima selektovano dugme");
		
		if(m_selected != null)
			m_selected.deselect();
		m_selected = btn;
		btn.select();
		if(m_changeListener != null)
			m_changeListener.onSelectedButtonChanged(m_selected);
		if(doCommand)
			btn.doCommand();
	}
	
	public void addWidget(Widget w)
	{
		m_widgets.add(w);
		m_canvas.add(w);
		if(m_loaded)
			positionWidget(w, m_widgets.size()-1);
	}	
	
	private int getBorderDistance()
	{
		return (m_size - m_toolSize) /2;
	}
	
	public MyButton newButton(MyButton.Command cmd, String imageUrl, String disabledImageUrl, int imageHeight, int imageWidth, int otherSize, boolean growOnSelect)
	{
		MyButton btn = new MyButton(m_pushToolbar, m_toolbarButtonStyle, m_toolbarHoverStyle, m_toolbarDown, imageWidth, imageHeight);
		btn.setOptionButton(m_optionToolbar);
		btn.setImageUrl(imageUrl);
		btn.setDisabledImageUrl(disabledImageUrl);
		if(m_horizontal)
			Helper.setWidgetOffsetSize(btn, otherSize, m_toolSize);
		else
			Helper.setWidgetOffsetSize(btn,m_toolSize,otherSize);
		btn.setClickListener(this);
		btn.setCommand(cmd);
		if(growOnSelect)
			btn.setGrowOnSelect(!m_horizontal, getBorderDistance()+1);
		addWidget(btn);			
		return btn;
	}
	
	private int getPreviousWidgetEnd(int index)
	{
		if(index <= 0)
			return 0;
		if(m_widgets.size() == 0)
			return 0;
		Widget w=(Widget)m_widgets.get(index-1);
		if(m_horizontal)
			return m_canvas.getWidgetLeft(w)+w.getOffsetWidth();
		else
			return m_canvas.getWidgetTop(w)+w.getOffsetHeight();
	}
	
	private void positionWidget(Widget w, int index)
	{
		int left;
		int top;
		if(m_horizontal)
		{
			left = getPreviousWidgetEnd(index)+m_toolDistance;
			top = Helper.getCentredTop(w.getOffsetHeight(),m_size);	
			if(!(w instanceof MyButton ))
				m_maxToolSize=Math.max(m_maxToolSize,w.getOffsetHeight());
		}
		else
		{
			top = getPreviousWidgetEnd(index)+m_toolDistance;
			left =  Helper.getCentredLeft(w.getOffsetWidth(), m_size);
			if(!(w instanceof MyButton ))
				m_maxToolSize=Math.max(m_maxToolSize,w.getOffsetWidth());
 		}	
		m_canvas.setWidgetPosition(w, left, top);
	}
	
	/**
	 * 
	 * @return zajednicku velicinu, ako je toobar horizontalan onda je to visina
	 */
	public int getClientSize()
	{
		return m_size;
	}
	
	public int getButtonTop(MyButton b)
	{
		return m_canvas.getWidgetTop(b);
	}
	
	public void enableButtons(boolean enabled)
	{
		for(int i=0; i<m_widgets.size();i++)
		{
			Widget w = (Widget)m_widgets.get(i);
			if (w instanceof MyButton)
			{
				MyButton btn = (MyButton) w;
				btn.setEnabled(enabled);
			}
		}
	}	
	
	private void adjustToMaxSize()
	{
		if(m_size < m_maxToolSize)
			setCommonSize(m_maxToolSize);
	}
	
	private void setCommonSize(int size)
	{
		m_size=size;
		if(m_horizontal)
			setHeight(size + "px");
		else
			setWidth(size + "px");
	}

	public void clear()
	{
		for(int i = 0; i< m_widgets.size(); i++)
		{
			Widget w = (Widget)m_widgets.get(i);
			w.removeFromParent();			
		}
		m_widgets.clear();
	}
	
	public void refresh()
	{	
		adjustToMaxSize();		
		for(int i=0;i < m_widgets.size();i++)
		{
			Widget w=(Widget)m_widgets.get(i);
			positionWidget(w,i);
		}
		if(m_size < m_maxToolSize)
			refresh();
	}	
	
	public Widget getWidget(int index)
	{
		return (Widget)m_widgets.get(index);
	}
	
	public void onLoad()
	{
		m_loaded=true;
		refresh();
	}	
}