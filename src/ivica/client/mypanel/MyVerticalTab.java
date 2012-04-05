package ivica.client.mypanel;

import com.google.gwt.user.client.ui.*;

import ivica.client.helper.Helper;

public class MyVerticalTab  extends Composite implements MyToolbar.PushButtonChangeListener, ClickListener
{


	public static final int VSPACE = 3;
//	public static final int BORDER_DISTANCE = 6;
	public static final int CONTAINER_FRAME_SIZE = 4;
	
	public static final int MIN_WIDTH=0;
	public static final int MIN_HEIGHT=0;
	
	private AbsolutePanel m_canvas;
	private AbsolutePanel m_viewContainer;
	private AbsolutePanel m_hider = new AbsolutePanel();
	private MyToolbar m_tabBar;

	private MultyView m_view=null;
	private int m_buttonWidth=1;
	private int m_buttonHeight=1;
	
	private int m_splitPosition = 400;
	private Label m_label;
	private MyButton m_closeButton;
	
	private int m_height;
	private int m_width;
//	private int m_currentTab=-1;
	private boolean m_loaded=false;
	
	private MyHorizontalSpliter m_spliter;
	
	public MyVerticalTab(int barSize, int buttonWidth, int buttonHeight, boolean scrollView)
	{
		m_buttonWidth = buttonWidth;
		m_buttonHeight = buttonHeight;
		m_canvas = new AbsolutePanel();	
		m_viewContainer = new AbsolutePanel(); 
		m_tabBar = new MyToolbar(/*horizontal=*/ false,
								/*pushToolbar=*/true,
								/*size=*/barSize,
								/*toolDistance=*/VSPACE,
								/*toolSize=*/m_buttonWidth);
		m_tabBar.setChangeListener(this);
		if(scrollView)
			m_view=new ScrollMultyView();
		else
			m_view=new AbsoluteMultyView();
		m_canvas.add(m_tabBar, 0, 0);
		
		m_canvas.add(m_viewContainer);
		m_label = new Label();
		m_label.setStyleName("MyVerticalTabLabel");
		m_viewContainer.add(m_label);
		
		m_closeButton = new MyButton(false, "MyTabCloseButton", "MyTabCloseButtonHover", "MyTabCloseButtonDown", 7, 7);
		m_closeButton.setCommand(new MyButton.Command()
		{
			public void doCommand(MyButton btn)
			{
				m_tabBar.deselectAllButtons();
			}
		});
		m_closeButton.setClickListener(this);
	//TODO stilovi za pushbuton, slike
		m_closeButton.setImageUrl("close.gif");
		m_closeButton.setDisabledImageUrl("close.gif");
		
		m_closeButton.setPixelSize(15, 15);
		m_viewContainer.add(m_closeButton);
		
		m_viewContainer.add(m_view);

		m_canvas.add(m_hider);
//		m_hider.setPixelSize(m_tabBar.get, buttonHeight);
		m_hider.setVisible(false);
		initWidget(m_canvas);		
	}
	
	public void onClick(Widget sender) 
	{
		if(sender == m_closeButton)
			m_closeButton.doCommand();
		
	}
	
	public void setPixelSize(int width, int height)
	{
		//super.setPixelSize(width, height);
		//m_canvas.setPixelSize(width, height);
		super.setPixelSize(width, height);
		m_width=width;
		m_height=height;
		if(m_loaded)
			positionWidgets();
	}
	
	public void setWidth(String width)
	{
		super.setWidth(width);
		m_width = Helper.getPixelSize(width);
		if(m_loaded)
			positionWidgets();
//		m_canvas.setWidth(width);
	}
	public void setHeight(String height)
	{
		super.setHeight(height);
		//m_canvas.setHeight(height);
		m_height = Helper.getPixelSize(height);
		if(m_loaded)
			positionWidgets();
	}
	
	public void setSpliter(MyHorizontalSpliter spliter)
	{
		m_spliter=spliter;
	}
	
	public void onSelectedButtonChanged(MyButton btn) 
	{
		if(m_spliter.isDragEnabled())
			m_splitPosition = m_spliter.getSplitPosition();
		if(btn == null)
		{
			//pomeri spliter skroz u levo da se vidi samo dugmad
			m_spliter.setSplitPosition(this.m_tabBar.getOffsetWidth());
			//sakrij spliter bar i onemoguci promenu velicine prozora
			m_spliter.setDragEnabled(false);
			
			//uklanja pogled iz multi pogleda
			m_view.showView(btn);
			
			//sakrij hider koji skriva border 
			m_hider.setVisible(false);
		}
		else
		{
			//prikazi pogled
			m_view.showView(btn);
			m_label.setText(m_view.getLabel(btn));
			//prikazi spliter bar
			m_spliter.setDragEnabled(true);
			//rasiri spliter na predhodnu poziciju 
			m_spliter.setSplitPosition(m_splitPosition);
			
			m_hider.setPixelSize(3, this.m_buttonHeight);
			m_canvas.setWidgetPosition(m_hider, m_tabBar.getOffsetWidth()-1, m_tabBar.getButtonTop(btn)+1);
			m_hider.setVisible(true);
			//TODO ? refreshuj spliter da bi postavio pogledu odgovarajucu velicinu
		}		
	}
	
	public void setStyles(String barStyle, String buttonStyleName, String buttonHoverStyleName, String buttonDownStyleName, String viewContainerStyleName)
	{
		m_tabBar.setStyleName(barStyle);
		m_hider.setStyleName(buttonHoverStyleName);
		m_canvas.setStyleName(barStyle);
		m_viewContainer.setStyleName(viewContainerStyleName);
		m_tabBar.setStyles(buttonStyleName, buttonHoverStyleName, buttonDownStyleName);
		m_closeButton.setStyles(buttonHoverStyleName, buttonStyleName, buttonDownStyleName);
	}
	
	public void addTab(Widget view,String label, String imageUrl, String disabledImageUrl,int  imageHeight, int imageWidth)
	{
		MyButton btn = m_tabBar.newButton(null, imageUrl, disabledImageUrl, imageHeight, imageWidth, m_buttonHeight, true);
		m_view.addView(btn, view, label);
	}
	
	public void positionWidgets()
	{
		Helper.setWidgetOffsetHeight(m_tabBar, m_height);
		//podesavam visinu viewContaineru tako da za rastojanje dugmeta manja od canvasa
		int containerHeight = m_height - 2 * VSPACE; 		
		int containerWidth = m_width - m_tabBar.getOffsetWidth() - CONTAINER_FRAME_SIZE;
		Helper.returnInteger containerClientHeight = new Helper.returnInteger();
		Helper.returnInteger containerClientWidth = new Helper.returnInteger();
		Helper.setWidgetOffsetSize(m_viewContainer, containerWidth, containerHeight, containerClientWidth, containerClientHeight);
		Helper.setWidgetOffsetPosition(m_canvas, m_viewContainer, m_tabBar.getOffsetWidth(), VSPACE);
		Helper.setWidgetOffsetPosition(m_viewContainer, m_label,CONTAINER_FRAME_SIZE,0);
		Helper.setWidgetOffsetPosition(m_viewContainer, m_closeButton,containerClientWidth.intVal - m_closeButton.getOffsetWidth()- CONTAINER_FRAME_SIZE, 0);
		int viewTop = Math.max(m_label.getOffsetHeight(), m_closeButton.getOffsetHeight());
		int viewWidth = containerClientWidth.intVal -  2 * CONTAINER_FRAME_SIZE;
		int viewHeight = containerClientHeight.intVal - viewTop - CONTAINER_FRAME_SIZE ;
		Helper.setWidgetOffsetSize(m_view, viewWidth, viewHeight);
		if(m_viewContainer.getOffsetWidth() == containerClientWidth.intVal)
			Helper.setWidgetOffsetPosition(m_viewContainer, m_view, CONTAINER_FRAME_SIZE, viewTop);
		else
			m_viewContainer.setWidgetPosition(m_view,CONTAINER_FRAME_SIZE, viewTop);				
	}
	
	public void Hide()
	{
		m_tabBar.deselectAllButtons();
	}
	
	public int getTabClientWidth()
	{
		return m_tabBar.getClientSize();
	}
	
	public void onLoad()
	{
		m_loaded = true;
		positionWidgets();
		m_spliter.setSplitPosition(m_tabBar.getOffsetWidth());
		m_spliter.setDragEnabled(false);
//		m_spliter.setSpliterBarVisible(false);
	}
	
/*	public void showTab(int index)
	{
		if(!m_loaded)
		{
			this.m_currentTab=index;
			return;
		}
		
	}*/
	
	public void enableTab(boolean enable, int index)
	{
		MyButton btn = (MyButton)m_tabBar.getWidget(index);
		btn.setEnabled(enable);
/*		if(enable)
			m_view.showView(btn);
		else
			m_view.showView(null);*/
	}

	/**
	 * Uklanja sve tabove sa tab panela
	 */
	public void clearAll()
	{
		
	}
	
	/**
	 * 
	 * @param index index taba koji je selektovan
	 * Izvedene klase mogu da overrajduju ovaj metod
	 * 
	 */
	public void onTabSelected(int index)
	{
		
	}
}
