package ivica.client.page;
import com.google.gwt.user.client.ui.*;

import ivica.client.mypanel.MultyShowListener;
import ivica.client.*;

public class ThumbView extends PageView implements MultyShowListener, PageChangeListener
{
	private PageView m_master=null;
	private boolean m_inited = false;
	private boolean m_showed = false;
	
	private boolean m_disableScroll = false;
	
	private Widget m_selectedPage = null;
	
	public void onShowView()
	{
		if(m_master == null)
			return;
		
		m_showed = true;
		if(!m_inited)
		{
			BookInfo.Quality thumb = m_master.m_bookInfo.getThumbQuality();
			Init(m_master.m_bookInfo, thumb, true, m_master.m_currentPage, PageView.CONTINOUS);			
			m_inited = true;			
		}
		else
		{
			showPage(m_master.m_currentPage);
		}
		selectPage(m_master.m_currentPage);
	}
	
	protected void onLayoutCreate(Layout l)
	{
		l.setPageMessages("Учитава", "Грешка", "Понови");
		l.setShowInfoText(false);
	}
	
	public void onHide()
	{
		m_showed = false;
	}
	
	/**
	 * master ne sme biti null
	 * @param master
	 */
	public void setMasterView(PageView master)
	{
		//ocisti predhodni sadrzaj thumbnaila ako je postojao
		//master ne sme biti null
		m_inited = false;
		m_master = master;
		master.addPageChangeListener(this);
		if(m_showed)
			onShowView();
	}
	
	public void onScroll(Widget sender,int scrollLeft, int scrollTop)
	{
		super.onScroll(sender, scrollLeft, scrollTop);
		if(sender == m_master)
		{
			
		}
	}
	
	public void onPageChange(Widget sender, int index, int scrollTop, String name) 
	{
		if(sender == m_master)
		{
			if(!this.m_disableScroll)
			{
				if(m_showed)
				{
					showPage(index);
					selectPage(index);
				}
			}
			m_disableScroll = false;
		}
	}
	
	private void selectPage(int index)
	{
		if(m_selectedPage != null)
			m_selectedPage.removeStyleName("RedBorder");
		Widget page = this.getPage(index);
		m_selectedPage = page;
		page.addStyleName("RedBorder");
	}
	
	public void onPageClicked(int i, Widget w)
	{
//		w.setStyleName(style);
		this.m_disableScroll = true;
		m_master.showPage(i);
		selectPage(i);
	}
}
