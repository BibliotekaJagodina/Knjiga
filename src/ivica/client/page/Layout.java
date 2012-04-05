package ivica.client.page;

import com.google.gwt.user.client.ui.Widget;

import ivica.client.*;

public abstract class Layout //implements PageLoadListener
{
//	protected AbsolutePanel m_canvas=null;
	protected LayoutService m_service=null;
	protected boolean m_showPictures=true;
	protected int m_currentPage=-1;
	protected int m_zoom=100;
	protected int m_scrollTop=0;
	protected int m_scrollLeft=0;
	
	protected String m_loadingMessage = null;
	protected String m_errorMessage = null;
	protected String m_tryAgainMessage = null;
//	protected String m_infoPageBackgroundStyle = null;
	protected boolean m_showInfoText = true;
	
	protected BookInfo.Quality m_quality=null;
	protected BookInfo m_bookInfo=null;
	protected BookInfo.PageDefinition[] m_pageDefinitions=null;
	
	protected abstract boolean showPage(int index);
//	protected abstract boolean canGoToNextPage();
	
	
	protected abstract void refresh();
	protected abstract void setCanvasWidth();
	protected abstract void setCanvasHeight();
	protected abstract void onClick(int x, int y);
	protected void onScroll(int ScrollTop, int ScrollLeft){}
	protected void RotateLeft(){}
	protected void RotateRight(){}
//	protected abstract void onComponentChanged(Page p);

	protected abstract void setService(LayoutService service);	
	protected abstract void zoom(int factor);
	protected abstract void clearCanvas();
	protected abstract void onQualityChanged();
	protected abstract Widget getPage(int index);
	
	protected Layout()
	{
		m_currentPage=-1;
	}
	
	/**
	 * @return false ako je moguc sledeci zum ( nije veci od 400%)
	 */
	protected boolean zoomIn()
	{
		if(m_zoom + 20 <= 400)
			zoom(m_zoom+20);
		return (m_zoom + 20) <= 400;
	}
	
	/**
	 * 
	 * @return false ako moguc sledeci zoom t.j. ako je manji od 20
	 */
	protected boolean zoomOut()
	{
		if(m_zoom-20 >= 20)
			zoom(m_zoom-20);
		return (m_zoom-20) >= 20;			
	}
	
	protected void setShowInfoText(boolean show)
	{
		this.m_showInfoText = show;
	}
/**
 * ako se ne postave, onda se prikazuju podrazumevane poruke
 * @param loading
 * @param error
 * @param tryAgain
 */
	protected void setPageMessages(String loading, String error, String tryAgain)
	{
		m_loadingMessage = loading;
		m_errorMessage = error;
		m_tryAgainMessage = tryAgain;
	}
	
/*	protected void setInfoPageBackgroundStyle(String style)
	{
		m_infoPageBackgroundStyle = style;
	}*/
	
	protected void Init(BookInfo book, BookInfo.Quality quality, LayoutService service, int firstPageToShow,  boolean showPictures)
	{
		m_pageDefinitions=null;
		m_bookInfo=book;
		m_quality=quality;
		m_showPictures=showPictures;
		m_zoom=100;
		m_scrollTop=0;
		m_scrollLeft=0;
		clearCanvas();
		setService(service);
		showPage(firstPageToShow);		
	}
	
	
	protected void changeQuality(BookInfo.Quality quality)
	{
		m_quality = quality;
		m_pageDefinitions = null;
		m_zoom = 100;
		onQualityChanged();
	}
	
	protected BookInfo.PageDefinition getPageDefinition(int index)
	{
		if(m_pageDefinitions==null)
			m_pageDefinitions=new BookInfo.PageDefinition[m_bookInfo.getPageCount()];
		Assertion.asert(index>=0 && index<m_pageDefinitions.length, "Indeks je van granica!");
		if(m_pageDefinitions[index]!=null)
			return m_pageDefinitions[index];
		else
		{
			m_pageDefinitions[index]=m_bookInfo.getPageDefinition(m_quality, index);
			return m_pageDefinitions[index];
		}
	}
	
	protected int getCurrentPageIndex()
	{
		return m_currentPage;
	}
	
	protected void setShowPictures(boolean show)
	{
		m_showPictures=show;
		refresh();
	}
	
	public String getPageName(int index)
	{
		return getPageDefinition(index).getName();
	}
	
	public boolean showPage(String name)
	{
		if(name == null)
			return false;
		name = name.trim();
		if(name.equals(""))
			return false;
		for(int i=0;i<m_bookInfo.getPageCount();i++)
		{
			if(getPageDefinition(i) != null)
			{
				if(name.equals(getPageDefinition(i).getName()))
				{
					showPage(i);
					return true;
				}
			}
		}
		return false;
	}
	
	public String getCurrentPageName()
	{
		if(m_currentPage < 0)
			return "";
		return getPageDefinition(m_currentPage).getName();			
	}
		
}
