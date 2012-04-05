package ivica.client.mypanel;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import ivica.client.viewport.MousePanel;
import ivica.client.helper.*;
import com.google.gwt.user.client.DOM;
public class MyButton extends Composite
{
	private final AbsolutePanel m_canvas = new AbsolutePanel();
	private final Image m_img  = new Image();
	private final MousePanel m_mousePanel = new MousePanel();
	private String m_downStyle=null;
	private String m_hoverStyle=null;
	private String m_buttonStyle=null;
	private boolean m_selected=false;
	private boolean m_pushButton = false;
	private boolean m_optionButton = false;
	
	private ClickListener m_listener;
//	private ClickHandler m_clickHandler;
	
	private boolean m_growOnSelect=false;
	private boolean m_growWidth=true;
	private int m_growSize=0;
	
	private int m_width;
	private int m_height;
	
	private int m_imgWidth;
	private int m_imgHeight;
	private String m_imageUrl;
	private String m_disabledImageUrl=null;
	
	private boolean m_enabled = true;
	
	private Command m_command = null;
	
	
	public static interface Command
	{
		public void doCommand(MyButton btn);
	}
	
	/**
	 * Funkcija se mora pozvati pre nego sto je dugme prikaceno na DOM da bi radila ispravno!
	 * @param downStyle
	 * @param hoverStyle
	 * @param buttonStyle
	 */
	protected void setStyles(String buttonStyle, String hoverStyle, String downStyle)
	{
		m_buttonStyle = buttonStyle;
		m_downStyle = downStyle;
		m_hoverStyle = hoverStyle;
		setStyleName(m_buttonStyle);
	}

	public void addMouseListener(MouseListener m)
	{
		m_mousePanel.addMouseListener(m);
	}
	
	public void removeMouseListener(MouseListener m)
	{
		m_mousePanel.removeMouseListener(m);
	}
	
	public void setOptionButton(boolean option)
	{
		m_optionButton = option;
	}
	
	protected MyButton(boolean pushButton, String buttonStyle, String hoverStyle, String downStyle, int imageWidth, int imageHeight)
	{
		m_pushButton = pushButton;
		m_canvas.add(m_img);
		m_imgWidth=imageWidth;
		m_imgHeight=imageHeight;
		m_img.setWidth(imageWidth+"px");
		m_img.setHeight(imageHeight+"px");

		m_mousePanel.setWidget(m_canvas);
		initWidget(m_mousePanel);
		m_mousePanel.addMouseListener(new MouseListener()
		{
			private boolean mouseDown = false;
			private boolean exit = false;
			public void onMouseEnter(Widget widget)
			{
				if(!m_enabled)
					return;
				if(!mouseDown)
				{
					//DOM.addEventPreview(Helper.preventDefaultMouseEvents);
					Helper.getPreventMouseEvents().Start();
					exit = false;
				}
				if(!m_selected && !mouseDown)
					setStyleName(m_hoverStyle);
			}
			public void onMouseLeave(Widget widget)
			{	
				Helper.getPreventMouseEvents().Stop();
				if(!m_enabled)
					return;
				exit = true;
				if(!m_selected)
					setStyleName(m_buttonStyle);
			}
			
			public void onMouseMove(Widget sender, int x, int y)
			{
				
			}
			
			public void onMouseDown(Widget sender, int x, int y)
			{
				if(!m_enabled)
					return;
				mouseDown=true;
				DOM.setCapture(getThis().getElement());
				setStyleName(m_downStyle);
			}
			
			public void onMouseUp(Widget sender, int x, int y)
			{
				if(!m_enabled)
					return;
				
				mouseDown=false;
				DOM.releaseCapture(getThis().getElement());
							
				if(!m_pushButton)
					setStyleName(m_hoverStyle);
				if(x>=0 && x<=getOffsetWidth() && y>=0 && y<=getOffsetHeight() && !exit)
				{
					if(m_pushButton)
					{
						if(m_selected && !m_optionButton)
						{
							deselect();
							setStyleName(m_hoverStyle);
						}
						else
							select();
					}
					if(m_listener != null)
						m_listener.onClick(getThis());
				}
				else
				{
					if(!m_selected)
						setStyleName(m_buttonStyle);	
				}
				exit = false;
			}			
		});
		if(buttonStyle != null)
			setStyleName(buttonStyle);
		m_hoverStyle=hoverStyle;
		m_buttonStyle=buttonStyle;
		m_downStyle=downStyle;
	}
	

	public void setCommand(Command cmd)
	{
		m_command = cmd;
	}
	
	public void doCommand()
	{
		if(m_command != null)
			m_command.doCommand(this);
	}
	
	public void setGrowOnSelect(boolean growWidth, int growSize)
	{
		this.m_growOnSelect=true;
		this.m_growWidth=growWidth;
		this.m_growSize=growSize;
		
	}
	
	private MyButton getThis()
	{
		return this;
	}
	
	public void setClickListener(ClickListener click)
	{
		m_listener=click;
	}
	
	protected boolean select()
	{
		if(!m_enabled)
			return false;
		if(isSelected())
			return true;
		m_selected=true;
		setStyleName(m_downStyle);
		if(m_growOnSelect)
		{
			if(m_growWidth)
				setWidth(m_width+m_growSize +"px"); 
			else
				setHeight(m_height+m_growSize+"px");				
		}
		return true;
	}
	
	protected void deselect()
	{
		if(!isSelected())
			return;	
		m_selected=false;
		setStyleName(m_buttonStyle);
		if(m_growOnSelect)
		{
			if(m_growWidth)
				setWidth(m_width-m_growSize+"px");
			else
				setHeight(m_height-m_growSize+"px");				
		}		
	}
	
	public boolean isSelected()
	{
		return m_selected;
	}
	
	public void setEnabled(boolean enabled)
	{
		m_enabled=enabled;
		setStyleName(m_buttonStyle);
		if(enabled)
			m_img.setUrl(m_imageUrl);
		else
			m_img.setUrl(m_disabledImageUrl);
	}
	
	public void setImageUrl(String url)
	{
		m_imageUrl=url;
		m_img.setUrl(url);
	}
	
	public void setDisabledImageUrl(String url)
	{
		m_disabledImageUrl=url;
	}
	
	public void setImagePosition()
	{
		if(m_img == null)
			return;
		int canvasWidth = m_canvas.getOffsetWidth();
		int canvasHeight = m_canvas.getOffsetHeight();
		if(m_growOnSelect)
		{
			if(isSelected())
			{
				if(m_growWidth)
					canvasWidth-=m_growSize;
				else
					canvasHeight-=m_growSize;
			}
		}				
		int left=Helper.getCentredLeft(m_imgWidth, canvasWidth);
		int top=Helper.getCentredTop(m_imgHeight, canvasHeight);	
		m_canvas.setWidgetPosition(m_img, left, top);
	}
	
	public void setPixelSize(int width, int height)
	{
		m_width=width;
		m_height=height;
		super.setPixelSize(width, height);
		m_canvas.setPixelSize(width, height);
		setImagePosition();
	}
	public void setWidth(String width)
	{
		m_width=Integer.parseInt(width.substring(0,width.length()-2));
		super.setWidth(width);
		m_canvas.setWidth(width);
		setImagePosition();
	}
	public void setHeight(String height)
	{
		m_height=Integer.parseInt(height.substring(0,height.length()-2));
		super.setHeight(height);
		m_canvas.setHeight(height);
		setImagePosition();
	}
	public void onLoad()
	{
		setImagePosition();
	}
}
