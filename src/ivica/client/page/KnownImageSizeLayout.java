package ivica.client.page;

import ivica.client.BookInfo;
import ivica.client.page.pagecomponent.InfoPage;
import ivica.client.page.pagecomponent.MyImage;
import ivica.client.page.pagecomponent.MyTextArea;
import com.google.gwt.user.client.ui.*;

import java.util.HashMap;

import com.google.gwt.user.client.ui.AbsolutePanel;

public abstract class KnownImageSizeLayout extends Layout {

	protected AbsolutePanel m_canvas=null;
	protected MyImage[] m_images=null;
	protected MyTextArea[] m_texts=null;
	protected HashMap m_infos=null;
	
	protected abstract void positionWidget(Widget w,int index);
	
	protected void setService(LayoutService service)
	{
		m_service=service;
		m_service.setView(m_canvas);
		setCanvasHeight();
		setCanvasWidth();
	}
	
	
	protected void onQualityChanged()
	{
		zoom(m_zoom); //zadrzavam stari zum ali posto se promenio kvalitet, promenile su se i njihove dimenzije
	}
	
	protected void showInfoPage(int index)
	{
		if(m_infos == null)
			m_infos=new HashMap();
		Integer i=new Integer(index);
		InfoPage infoPage=(InfoPage)m_infos.get(i);
		if(infoPage == null)
		{
			infoPage=new InfoPage();
			infoPage.ShowInfoText(m_showInfoText);
			String infoText=getPageDefinition(index).getInfoText();
			infoPage.setDataOnce(infoText);
			m_infos.put(i, infoPage);
		}
		positionWidget(infoPage, index);
	}
	
	protected void clearCanvas()
	{
		if(m_canvas==null)
			return;
		if(m_service==null)
			return;
		m_canvas=new AbsolutePanel();
		m_service.setView(m_canvas);
	}
	
	protected void showPicture(int index)
	{
		BookInfo.PageDefinition p=this.getPageDefinition(index);
		if(m_texts != null)
			if(m_texts[index] != null)
				m_texts[index].setVisible(false);
		if(! p.isInfoPage()) 
		{
			if(m_images == null)
				m_images =  new MyImage[m_bookInfo.getPageCount()];
			MyImage img = m_images[index];
			if(img == null)
			{
				img=new MyImage();
				if(this.m_errorMessage != null)
					img.setErrorText(m_errorMessage);
				if(this.m_loadingMessage != null)
					img.setLoadingMsg(m_loadingMessage);
				if(this.m_tryAgainMessage != null)
					img.setTryAgain(m_tryAgainMessage);
				m_images[index]=img;
			}
			img.setData(p.getImageUrl());
			positionWidget(img,index);		
		}
		else //info page prikazuje informaciju o preskocenim stranama
			showInfoPage(index);
	}
	
	protected Widget getPage(int index)
	{		
		BookInfo.PageDefinition pd = getPageDefinition(index);
		
		if(pd.isInfoPage())
		{			
			showInfoPage(index);
			return (Widget)m_infos.get(new Integer(index));
		}
		if(this.m_showPictures)
		{
			showPicture(index);
			return m_images[index];
		}
		else
		{
			showText(index);
			return this.m_texts[index];
		}
	}
	
	protected void showText(int index)
	{
		//TODO uradi implementaciju funkcije !
	}


}
