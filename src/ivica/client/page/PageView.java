package ivica.client.page;

import com.google.gwt.user.client.ui.*;
//import com.google.gwt.http.client.*;

import ivica.client.mypanel.LayoutChangeListener;
import ivica.client.viewport.*;
import ivica.client.*;

import java.util.*;
//import com.google.gwt.json.client.*;

public class PageView extends Viewport  implements ScrollListener, LayoutService, LayoutChangeListener
{
	
	protected boolean m_loaded=false;
	protected boolean m_initOnLoad=false;
	protected int m_initialPageToShow=0;
	protected Layout m_layout;
	
	protected BookInfo m_bookInfo=null;
	protected BookInfo.Quality m_quality=null;
	protected boolean m_showPictures=true;
	
	public static final int CONTINOUS=0;
	public static final int SLIDE=1;

	//TODO	
	public ArrayList m_pageChangeListeners = null;
//	public static final int UNKNOWN_SIZE_SLIDE=2;

	
	public void addPageChangeListener(PageChangeListener l)
	{
		if(m_pageChangeListeners == null)
			m_pageChangeListeners = new ArrayList();
		m_pageChangeListeners.add(l);
	}
	
	
	protected int m_currentPage=-1;
	
	/**
	 * poziva se kad se promeni layout glavnog prozora
	 */
	public void onLayoutChange()
	{
		if(m_layout!=null)
			m_layout.refresh();
	}
	
/*	public boolean canGoToNextPage()
	{
		return m_layout.canGoToNextPage();
	}*/
	
	public boolean nextPage()
	{
		if(m_layout == null)
			return false;
		int nextPage =m_currentPage + 1;
		m_layout.showPage(nextPage);
		m_currentPage = nextPage;
		int lastIndex = m_bookInfo.getPageCount()-1;
		if(m_currentPage+1 > lastIndex)
			return false;
		return true;
	}
	
	/**
	 * 
	 * @return vraca false ako vise nema stranica na koje se moze vratiti
	 * t.j. ako se nalazi na nultoj.
	 * Ukoliko iz nekog razloga nije moguce prikazati stranicu takodje vraca false
	 */
	public boolean previousPage()
	{
		if(m_layout == null)
			return false;
		if(m_currentPage-1 <0)
			return false;
		m_layout.showPage(m_currentPage-1);
		if(m_currentPage-1 <0)
			return false;
		return true;
	}
	public int getCurrentPage()
	{
		return m_currentPage;
	}
	
	//za overrajdovanje
	protected void onLayoutCreate(Layout layout)
	{
		
	}
	
	public void Init(BookInfo bookInfo, BookInfo.Quality quality, boolean showPictures, 
			int firstPageToShow, int layoutType)
	{
		m_initOnLoad=true;
		m_bookInfo=bookInfo;
		m_quality=quality;
		m_showPictures=showPictures;
		m_initialPageToShow=firstPageToShow;
		switch(layoutType)
		{
		case CONTINOUS:
			m_layout=new ContinousLayout();
			break;
		case SLIDE:
			m_layout=new SlideLayout();
			break;
/*		case UNKNOWN_SIZE_SLIDE:
			break;*/
		default:
			Assertion.asert(false, "Tip layouta nije podrzan");
		}
		onLayoutCreate(m_layout);
		if(m_loaded)
		{
			m_layout.Init(bookInfo, quality, this, firstPageToShow, showPictures);
			m_initOnLoad=false;
		}
	}
	
/*	private void setPageMessages(String loading, String error, String tryAgain)
	{
		m_layout.setPageMessages(loading, error, tryAgain);
	}*/
	
	protected void setShowInfoText(boolean show)
	{
		m_layout.setShowInfoText(show);
	}
	
	//protected void set
	
	public void onScroll(Widget sender,int scrollLeft, int scrollTop) 
	{
		m_layout.onScroll(scrollTop,scrollLeft);
	}

	public void refresh()
	{
		Assertion.asert(m_loaded, "Mora biti ucitan");
		m_layout.refresh();
	}
	
	public boolean showPage(int index)
	{
		Assertion.asert(m_loaded, "Mora biti ucitan");
		return m_layout.showPage(index);
	}
	
	public boolean showPage(String name)
	{
		return m_layout.showPage(name);
	}
	
	public String getCurrentPageName()
	{
		return m_layout.getCurrentPageName();
	}
	
	public void onPageChanged(int currentPage, int scroolTop)
	{
		m_currentPage=currentPage;
		
		if(m_pageChangeListeners != null)
		{
			for(int i=0; i< m_pageChangeListeners.size();i++)
			{
				
				PageChangeListener l = (PageChangeListener)m_pageChangeListeners.get(i);
				String pageName = m_layout.getPageName(currentPage);
				l.onPageChange(this, m_currentPage, scroolTop, pageName);
			}
		}
	}
			
	public PageView()
	{
		addScrollListener(this);
	}
	
	public void onLoad()
	{	
		m_loaded=true;
		if(m_initOnLoad)
		{
			m_layout.Init(m_bookInfo, m_quality, this, m_initialPageToShow,m_showPictures);
			m_initOnLoad=false;
		}
	}

	public void changeQuality(BookInfo.Quality quality)
	{
		m_quality=quality;
		m_layout.changeQuality(quality);
	}
	
	public void changeQuality(int qualityIndex)
	{
		if( qualityIndex > m_bookInfo.getQualityDefinitions().size()-1)
			return;
		BookInfo.Quality q = (BookInfo.Quality)m_bookInfo.getQualityDefinitions().get(qualityIndex);
		this.changeQuality(q);
	}
	
	public void changeLayout(int layoutType)
	{
		int firstPageToShow=m_currentPage;
		if(m_currentPage<0)
			firstPageToShow=0;
		switch(layoutType)
		{
		case CONTINOUS:
			if( ! (m_layout instanceof ContinousLayout))
			{
				m_layout=new ContinousLayout();
				m_layout.Init(m_bookInfo, m_quality, this, firstPageToShow,m_showPictures);
			}
			break;
		case SLIDE:
			if( ! (m_layout instanceof SlideLayout))
			{
				m_layout=new SlideLayout();
				m_layout.Init(m_bookInfo, m_quality, this, firstPageToShow,m_showPictures);
			}
		}
	}
	

	public void scrollToPage(String name)
	{
		
	}
	
	public void zoom(int factor)
	{
		m_layout.zoom(factor);
	}
	
	public boolean zoomIn()
	{
		return m_layout.zoomIn();
	}
	
	public boolean zoomOut()
	{
		return m_layout.zoomOut();
	}
	protected Widget getPage(int index)
	{
		return m_layout.getPage(index);
	}
	
	public void onClick(int x, int y)
	{
		m_layout.onClick(x, y);
	}
	
	public int getPageCount()
	{
		return this.m_bookInfo.getPageCount();
	}
	//potrebno zbog overrajdovanja
	public void onPageClicked(int index, Widget w)
	{
		
	}
	
	private int m_clientWidth=-1;
	private int m_clientHeight=-1;
	public void setPixelSize(int width, int height)
	{
		super.setPixelSize(width, height);
		m_clientWidth = width;
		m_clientHeight = height;
	}
		
	public void setWidth(String width)
	{
		super.setWidth(width);
		m_clientWidth = ivica.client.helper.Helper.getPixelSize(width);
	}
	
	public void setHeight(String height)
	{
		super.setHeight(height);
		m_clientHeight = ivica.client.helper.Helper.getPixelSize(height);
	}
	
	public int getClientHeight()
	{
		return m_clientHeight;
	}
	public int getClientWidth()
	{
		return m_clientWidth;
	}
}
