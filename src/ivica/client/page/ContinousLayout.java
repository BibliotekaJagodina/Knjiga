package ivica.client.page;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.*;
import ivica.client.helper.*;
import ivica.client.page.pagecomponent.*;

public class ContinousLayout extends KnownImageSizeLayout 
{

	private static final int m_pageDistance=20;
	private static final int m_delay=250;
	
	private int m_pageOffsetWidthDiference=2;
	private int m_pageOffsetHeightDiference=2;
	private boolean m_firstLoad=true;
	private boolean m_scrollDown=true;
	
	
	private Timer m_timer=new Timer()
	{
		public void run()
		{
			loadPages(m_scrollTop);
		}
	};
	
	
	protected ContinousLayout()
	{
		super();
		m_canvas=new AbsolutePanel();
	}
	
	public void zoom(int factor)
	{
		m_firstLoad=true;
			
		int oldHeight=m_canvas.getOffsetHeight();
		int oldWidth=m_canvas.getOffsetWidth();
		int oldTop=m_service.getScrollPosition();
		int oldLeft=m_service.getHorizontalScrollPosition();
		
				
		m_zoom=factor;
		
		clearCanvas();
/*		m_canvas=new AbsolutePanel();
		m_service.setView(m_canvas);*/
		setCanvasHeight();
		setCanvasWidth();
		refresh();
		
		int newHeight=m_canvas.getOffsetHeight();
		int newWidth=m_canvas.getOffsetWidth();
		int newTop=(int)((double)oldTop*(double)newHeight/(double)oldHeight);
		int newLeft=(int)((double)oldLeft*(double)newWidth/(double)oldWidth);

		m_service.setScrollPosition(newTop);
		m_service.setHorizontalScrollPosition(newLeft);
	}
	
	
/*	public void onComponentChanged(Page p)
	{
		
	}*/
	
	protected void setCanvasHeight()
	{
		int height;
		int pageCount=m_bookInfo.getPageCount();
		height=(getPageOffsetHeight() + m_pageDistance) * pageCount;
		m_canvas.setHeight(height+"px");
	}
	
	private int getPageOffsetWidth()
	{
		return m_pageOffsetWidthDiference+getPageWidth();
	}
	private int getPageOffsetHeight()
	{
		return m_pageOffsetHeightDiference+getPageHeight();
	}
	
	protected void setCanvasWidth()
	{
		int pageWidth=getPageOffsetWidth();
		m_canvas.setWidth("100%");
		if(pageWidth>m_canvas.getOffsetWidth())
			m_canvas.setWidth(pageWidth+"px");
	}
	
	private int getPageLeft()
	{
		return Helper.getCentredLeft(getPageOffsetWidth(),m_canvas);
	}

	private int getPageTop(int index) {
		//za dati index stranice izracunava njenu x koordinatu	
		int height=getPageOffsetHeight()+m_pageDistance;
		return height*index;
	}
	
	private int getPageHeight()
	{
		
		int pageHeight=m_bookInfo.getDefaultPageDimension(m_quality).getHeight();// m_service.getDefaultPageHeight();
				
		if(m_zoom==100)
			return pageHeight;
		return (int) (pageHeight * (m_zoom/100.0));
	}
	
	private int getPageWidth()
	{
		int pageWidth=m_bookInfo.getDefaultPageDimension(m_quality).getWidth();//m_service.getDefaultPageWidth();		
		if(m_zoom==100)
			return pageWidth;
		return (int) (pageWidth * (m_zoom/100.0));
	}
	
	/**
	 * osvezava prikaz
	 */
	public void refresh()
	{
		if(m_service == null)
			return;
		if(m_canvas == null)
			return;
		setCanvasWidth();
		int scrollTop=m_service.getScrollPosition();
		loadPages(scrollTop);
		
		m_service.setHorizontalScrollPosition(m_scrollLeft);
	}
	
	/**
	 *		ucitava stranice koje su ogranicene koordinatom scrollTop i visinom viewPorta
	 */ 
	private void loadPages(int scrollTop)
	{
		
		if(m_service.getOffsetHeight()<=0)
			return;	
		if(Window.getClientHeight()<m_service.getOffsetHeight())
			return;
		
		int pageHeight=getPageOffsetHeight()+m_pageDistance;
		//before scroll pokazuje koliko strana ima pre vidljivog dela
		//celobrojni deo prikazuje indeks vidljive strane a deo iza zareza
		//pomnozen sa 100 prikazuje koliko procenata strane nije prikazano	
		double beforeScrollTop=(double)scrollTop / pageHeight;		
		int firstVisible=(int)Math.floor(beforeScrollTop);
		double firstPercentageShown=1-(beforeScrollTop-firstVisible);
		
		int bottom=scrollTop + m_service.getOffsetHeight();
		
	//	Window.alert(m_service.getOffsetHeight()+"");
		
		
		//beforeBottom pokazuje koliko ima celih stranica iznad donjeg dela Viewporta
		//deo iza zareza prikazuje koliko je prikazano posle poslednje cele stranice
		double beforeBottom=(double)bottom / pageHeight;
		int lastVisible=((int)Math.ceil(beforeBottom))-1;
		double lastPercentageShown=beforeBottom-lastVisible;
		int pageCount=m_bookInfo.getPageCount();//m_service.getPageCount();
		if(lastVisible>pageCount-1)
		{
			lastPercentageShown=1.0;
			lastVisible=pageCount-1;
		}
		//prikazujem vidljive strane
		loadPages(firstVisible,lastVisible);
		
	//ucitavam nevidljive susedne strane		
		int numToLoad=lastVisible-firstVisible;
		if(numToLoad<=0)
			numToLoad=1;
	  //strane ispod Viewporta
		int lastIndex=m_bookInfo.getPageCount()-1; //m_service.getPageCount()-1;
		int firstUnder=lastVisible+1;
		if(firstUnder>lastIndex)
			firstUnder=lastIndex;
		int lastUnder=lastVisible+numToLoad;
		if(lastUnder>lastIndex)
			lastUnder=lastIndex;
	  //strane iznad viewporta	
		int firstAbove=firstVisible-numToLoad;
		if(firstAbove<0)
			firstAbove=0;
		int lastAbove=firstVisible-1;
		if(lastAbove<0)
			lastAbove=0;		
		if(m_scrollDown)
		{
			loadPages(firstUnder,lastUnder);
			loadPages(firstAbove,lastAbove);
		}
		else
		{
			loadPages(firstAbove,lastAbove);
			loadPages(firstUnder,lastUnder);
		}		
	//izracunavam indeks stranice koja se u najvecem procentu vidi na ekranu		
		//ako je vidljiva samo jedna stranica onda je njen indeks trazeni
		int currentPage=-1;
		if(firstVisible==lastVisible)
			currentPage=firstVisible;
		//ako su vidljive samo dve stranice prikazuje se ona koja je vidljiva u vecem procentu
		else if((lastVisible-firstVisible)==1)
		{
			if(firstPercentageShown>=lastPercentageShown)
				currentPage=firstVisible;
			else
				currentPage=lastVisible;
		}
		//ako je vidljivo vise stranica onda je trazeni indeks indeks prve kompletno vidljive
		else if((lastVisible-firstVisible)>1)
		{
			//ako je prva kompletno vidljiva => njen indeks u protivnom indeks sledece
			if(firstPercentageShown==1)
				currentPage=firstVisible;
			else
				currentPage=firstVisible+1;
		}			
		if(m_currentPage!=currentPage)
		{
			m_currentPage=currentPage;
			m_service.onPageChanged(currentPage,scrollTop);
//			System.out.println(m_currentPage);
//			System.out.println("range first:"+firstVisible+" last:"+lastVisible+" scrollTop:"+scrollTop);
		}			
	}
	

	
	protected void positionWidget(Widget w, int index)
	{
		if(w.getParent()!= m_canvas)
		{
			w.removeFromParent();
			m_canvas.add(w);
		}
		w.setVisible(true);		
		w.setPixelSize(this.getPageWidth(), this.getPageHeight());		
		if(m_firstLoad)
		{
			m_pageOffsetWidthDiference=w.getOffsetWidth()-getPageWidth();
			m_pageOffsetHeightDiference=w.getOffsetHeight()-getPageHeight();
			setCanvasWidth();
			setCanvasHeight();
			m_firstLoad=false;
		}		
		m_canvas.setWidgetPosition(w, getPageLeft(), getPageTop(index));	
	}
	
	/*
	 * Prikazuje stranice od first do last
	 */
	private void loadPages(int first, int last)
	{
		for(int i=first;i<=last;i++)
		{
			if(m_showPictures)
				showPicture(i);
			else
				showText(i);
		}
	}
	
	protected void onScroll(int scrollTop,int scrollLeft)
	{
		m_scrollDown=true;
		if(scrollTop-m_scrollTop<0)
			m_scrollDown=false;
		m_scrollTop=scrollTop;
		m_scrollLeft=scrollLeft;
		m_timer.cancel();
		m_timer.schedule(m_delay);
	}
	
	/**
	 * @return vraca false ako se ne moze vise skrolovati ili ako se ne moze prikazati trenutna
	 */
	protected boolean showPage(int index) 
	{
		int pageCount = m_bookInfo.getPageCount(); //m_service.getPageCount();
		if((index>pageCount-1)||index<0)
			return false;
		int pageTop = getPageTop(index);
		m_service.setScrollPosition(pageTop);
		loadPages(pageTop);
		if(m_service.getScrollPosition()+m_service.getOffsetHeight() >= m_canvas.getOffsetHeight())
			return false;
		return true;
	}
	
/*	protected boolean canGoToNextPage()
	{
		if(m_currentPage+1 > m_bookInfo.getPageCount()-1)
			return false;
		if(m_service.getScrollPosition()+m_service.getOffsetHeight() >= m_canvas.getOffsetHeight())
			return false;
		return true;
	}*/
	
	protected void onClick(int x, int y)
	{
		int pageHeight=getPageOffsetHeight()+m_pageDistance;
		double beforeScrollTop=(double)y / pageHeight;		
		int first=(int)Math.floor(beforeScrollTop);
		
		if(m_showPictures)
		{
			if(first <0 || first > m_images.length-1)
				return;
			MyImage img=m_images[first];
			InfoPage info=null;
			Widget w=null;
			if(img !=null)
				w=img;
			else
			{
				info = (InfoPage)m_infos.get(new Integer(first));
				w = info;
			}
			if(w == null)
				return;
			if(w.getParent() != m_canvas)
				return;			
			int left = m_canvas.getWidgetLeft(w);
			int right = left + w.getOffsetWidth();
			int top = m_canvas.getWidgetTop(w);
			int bottom = top + w.getOffsetHeight();
			if( x> left && x<right && y>top && y<bottom)
			{
			/*	if(img != null)
				{
					if(img.isError())
						img.reload();
				}*/
				m_service.onPageClicked(first, w);
			}
		}
		else
		{
			ivica.client.Assertion.asert(false, "nije implementirano za text");
		}
	}
	
}
