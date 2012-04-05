package ivica.client.page;
/*import java.util.HashMap;

import ivica.client.page.pagecomponent.*;
*/
import com.google.gwt.user.client.ui.*;
import ivica.client.*;
import ivica.client.page.pagecomponent.*;
import ivica.client.helper.*;

public class SlideLayout extends KnownImageSizeLayout
{
	protected SlideLayout()
	{
		m_currentPage=-1;
		m_canvas=new AbsolutePanel();
	}
	protected void positionWidget(Widget w, int index)
	{
		if(w.getParent() != m_canvas)
			m_canvas.add(w);
		int width = this.getWidth(index);
		int height = this.getHeight(index);
		w.setPixelSize(width, height);
		
//		m_canvas.setSize("100%", "100%")
		m_canvas.setWidth("100%");
		m_canvas.setHeight(m_service.getClientHeight() + "px");
		//m_canvas.setWidth(m_service.getClientWidth()+"px");
		//m_canvas.setPixelSize(m_service.getClientWidth(), m_service.getClientHeight());
		if(m_canvas.getOffsetHeight()<w.getOffsetHeight())
			m_canvas.setHeight(w.getOffsetHeight()+"px");
		if(m_canvas.getOffsetWidth()<w.getOffsetWidth())
			m_canvas.setWidth(w.getOffsetWidth()+"px");

		int left=Helper.getCentredLeft(width, m_canvas);
		int top=Helper.getCentredTop(height, m_canvas);
		m_canvas.setWidgetPosition(w, left, top);
	}
	
	private int getWidth(int index)
	{
		int width=0;
		BookInfo.PageDefinition p=this.getPageDefinition(index);
		if(p.getSpecificDimension() != null)
			width=p.getSpecificDimension().getWidth();
		else 
			width=m_bookInfo.getDefaultPageDimension(m_quality).getWidth();
		if(m_zoom == 100)
			return width;
		else
			return (int) ((double)width * (double)m_zoom/100.0 );
	}
	
	private int getHeight(int index)
	{
		int height=0;
		BookInfo.PageDefinition p=this.getPageDefinition(index);
		if(p.getSpecificDimension() != null)
			height=p.getSpecificDimension().getHeight();
		else 
			height=m_bookInfo.getDefaultPageDimension(m_quality).getHeight();
		if(m_zoom == 100)
			return height;
		else
			return (int) ((double)height * (double)m_zoom/100.0 );
	}
	
	protected void refresh() 
	{
		if(this.m_currentPage != -1)
			this.showPage(m_currentPage);
	}

	protected void setCanvasHeight() {
		// TODO Auto-generated method stub
		
	}

	protected void setCanvasWidth() {
		// TODO Auto-generated method stub
		
	}

	protected void setService(LayoutService service) 
	{
		m_service=service;
		m_service.setView(m_canvas);
		setCanvasHeight();
		setCanvasWidth();
	}

	private void detachWidget(int index)
	{
		BookInfo.PageDefinition p=this.getPageDefinition(index);
		if(p.isInfoPage())
		{
			if(m_infos == null)
				return;
			InfoPage info=(InfoPage)m_infos.get(new Integer(index));
			info.removeFromParent();
		}
		else if(this.m_showPictures)
		{
			if(this.m_images == null)
				return;
			if(m_images == null)
				return;
			if(m_images[index] == null)
				return;
			m_images[index].removeFromParent();
		}
		else
		{
			if(this.m_texts == null)
				return;
			if(m_texts[index] == null)
				return;
			m_texts[index].removeFromParent();
		}
	}
	/**
	 * vraca false ako je indeks van granica
	 * inace prikazuje stranicu
	 */
	protected boolean showPage(int index) 
	{
		if(index<0 || index>m_bookInfo.getPageCount()-1)
			return false;
		if(m_currentPage != -1)
			this.detachWidget(m_currentPage);
		
		if(this.m_showPictures)
			this.showPicture(index);
		else
			this.showText(index);
		m_currentPage=index;
		m_service.onPageChanged(m_currentPage, m_service.getScrollPosition());
		return true;	
	}
	
	protected void zoom(int factor) 
	{
		if(m_currentPage == -1)
			return;
		m_zoom=factor;
		this.showPage(m_currentPage);
	}

	protected void onClick(int x, int y)
	{
		
	}
	
/*	protected boolean canGoToNextPage()
	{
		if(m_currentPage+1 > m_bookInfo.getPageCount()-1)
			return false;
		return true;
	}*/
	
}
