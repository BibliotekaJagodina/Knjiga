package ivica.client.mypanel;

import com.google.gwt.user.client.ui.*;
import java.util.*;
import ivica.client.viewport.*;

import ivica.client.helper.*;

public class MyStackPanel extends MyVerticalLayout
{
	public static final int SPACEING = 5;
	private int m_headerHeight;
	private int m_headerImageHeight = 0;
	private int m_headerImageWidth = 0;
	
	private int m_currentStackIndex = -1;
	private int m_stackCount = 0;
	private ArrayList m_stacks = new ArrayList();
	
	protected class Stack
	{
		public Stack(Header h, Widget w)
		{
			header = h;
			content = w;
		}
		public Header header;
		public Widget content;
	}
	
	protected class Header extends Composite
	{
		private Label m_label = null;
		private MousePanel m_mousePanel = new MousePanel();
		private AbsolutePanel m_horizontal = new AbsolutePanel();
		private int index;		
		private boolean m_enabled = true;
		private Image m_img = null;
		private Image m_disabledImg = null;
		
		public Header(String text, int index, Image img, Image disabledImg)
		{
			m_img = img;
			m_disabledImg = disabledImg;
			this.index = index;
			m_label = new Label(text);
			int top = SPACEING;
			Image imgToShow = img;
			if(img != null)
				img.setPixelSize(m_headerImageWidth, m_headerImageHeight);
			if(disabledImg != null)
				disabledImg.setPixelSize(m_headerImageWidth, m_headerImageHeight);
			if(!m_enabled)
				imgToShow = disabledImg;
			if(imgToShow != null)
			{
				top = Helper.getCentredTop(m_headerImageHeight, m_headerHeight);
				m_horizontal.add(imgToShow, SPACEING, top);
			}

//			else
//				m_horizontal.add(m_label, SPACEING, 0);
			
			m_horizontal.add(m_label);
			m_horizontal.setWidgetPosition(m_label, m_headerImageWidth + SPACEING * 2, top);


			m_label.setStyleName("StackLabel");
			m_label.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender)
				{
					onHeaderClick();
				}
			});
			
			m_horizontal.add(m_mousePanel,0,0);
			//m_mousePanel.setWidget(m_horizontal);
			m_mousePanel.addMouseListener(new MouseListenerAdapter()
			{
				public void onMouseUp(Widget sender, int x, int y)
				{
					if(! m_enabled)
						return;
					onHeaderClick();
				}
				public void onMouseEnter(Widget sender)
				{
					if(! m_enabled)
						return;
					m_horizontal.setStyleName("StackHeaderOver");
				}
				
				public void onMouseLeave(Widget sender)
				{
					m_horizontal.setStyleName("StackHeader");					
				}				
			});
			initWidget(m_horizontal);
			m_horizontal.setStyleName("StackHeader");
		}
				
		protected void onHeaderClick()
		{
			if(! m_enabled)
				return;
			m_horizontal.setStyleName("StackHeader");
			if(getObj().index == m_currentStackIndex)
				return;
			//sakrij predhodni ako postoji
			if(m_currentStackIndex != -1)
				removeSlot(m_currentStackIndex + 1);
			//prikazi novi
			Widget w = ((Stack) m_stacks.get(getObj().index)).content;
			insertRelativeSizeSlot(getObj().index + 1, w, 100, 10,10);
			m_currentStackIndex = getObj().index;
		}
		
		protected void enable(boolean b)
		{
			//ako nema promene izadji
			if(m_enabled == b)
				return;
			
			m_enabled = b;

			Image old = m_img;
			Image nw = m_disabledImg;
			//ako je promena sa disabled na enabled
			if(m_enabled)
			{
				old = m_disabledImg;
				nw = m_img;
			}			
			//ukloni disabled sliku ako postoji
			if(old != null)
				m_horizontal.remove(old);			
			//postavi enabled sliku ako postoji
			if(nw != null)
			{
				int top = Helper.getCentredTop(m_headerImageHeight, m_headerHeight);
				m_horizontal.add(nw, SPACEING, top);
			}			
		}
		
		public void setPixexSize(int width, int height)
		{
			m_mousePanel.setPixelSize(width, height);
			m_horizontal.setPixelSize(width, height);
		}
		
		public void setSize(String width, String height)
		{
			m_mousePanel.setSize(width,height);
			m_horizontal.setSize(width, height);
		}
		
		public void setWidth(String width)
		{
			m_mousePanel.setWidth(width);
			m_horizontal.setWidth(width);
		}
		
		public void setHeight(String height)
		{
			m_mousePanel.setHeight(height);
			m_horizontal.setHeight(height);
		}
		
		public Header getObj()
		{
			return this;
		}
	}
	
	public void openStack(int index)
	{
		Header h = ((Stack)m_stacks.get(index)).header;
		h.onHeaderClick();
	}
	protected MyStackPanel getThis()
	{
		return this;
	}
	
	public MyStackPanel()
	{
		super(false);	
	}
	
	public void add(Widget contents, String header)
	{		
		add(contents, header, null, null);
	}
	
	public void setHeaderHeight(int headerHeight)
	{
		this.m_headerHeight = headerHeight;
	}
	
	public void setHeaderImageDimensions(int width, int height)
	{
		m_headerImageWidth = width;
		m_headerImageHeight = height;
	}
	
	public void add(Widget contents, String header, Image img, Image disabled)
	{		
		Header h = new Header(header, m_stackCount, img, disabled);
		m_stackCount++;
		this.addAbsoluteSizeSlot(h, m_headerHeight, 1, m_headerHeight);
		this.m_stacks.add(new Stack(h,contents));
	}

}
