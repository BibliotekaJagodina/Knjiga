package ivica.client.page.pagecomponent;


import com.google.gwt.user.client.ui.*;
import ivica.client.*;
import ivica.client.helper.*;

public class MyImage extends Composite implements LoadListener
{
	private AbsolutePanel m_canvas=new AbsolutePanel();
	private MyImageComponent m_img=null;
	private MyImageComponent m_newImg=null;
	private Message m_message=null;
	
	private int m_width=0;
	private int m_height=0;
	private boolean m_errorMessage = false;
	private String m_url = null;
	
	private String m_errorMsg = null;
	private String m_loadingMsg = null;
	private String m_tryAgain = null;
	private static String TRY_AGAIN = "Покушајте поново";
//	private ChangeListenerCollection m_listeners=null;
	
//	LoadListenerCollection m_listeners=null;

	protected class MyImageComponent extends Image
	{
		protected MyImageComponent()
		{
			super();
			setStyleName("MyImageComponent");
		}
		private boolean m_error=false;
		
		protected void setError(boolean error)
		{
			m_error=error;
		}
		protected boolean isError()
		{
			return m_error;
		}	
	}
	
	public MyImage()
	{		
		initWidget(m_canvas);
		setStyleName("PageComponent");
	}
	/*
	 * dodaje se listener koji prati promenu velicine komponente
	 * koja je izazvana ucitavanjem slike
	 * kojoj predhodno nije postavljena velicina
	 */	
/*	public void addSizeChangeListener(ChangeListener listener)
	{
		if(m_listeners==null)
			m_listeners=new ChangeListenerCollection();
		m_listeners.add(listener);
	}*/
	
/*	public void removeSizeChangeListener(ChangeListener listener)
	{
		if(m_listeners!=null)
			m_listeners.remove(listener);
	}*/
	
	private void setWidgetSize(Widget w)
	{
		if(m_width>0)
			w.setWidth(m_width+"px");
		if(m_height>0)
			w.setHeight(m_height+"px");
	}
	
	private MyImageComponent createImage()
	{
		MyImageComponent img=new MyImageComponent();
		setWidgetSize(img);
		img.addLoadListener(this);
		return img;
	}
	
	private void attachImage(MyImageComponent img)
	{
		m_canvas.add(img);
		m_canvas.setWidgetPosition(img, 0, 0);			

	}
	public void onLoad(Widget sender)
	{	
		//TODO - listener koji obavestava o ucitavanju slike
		
		MyImageComponent img=(MyImageComponent)sender;
		img.setError(false);
		
		//ako je ucitana nova slika (slika u vecoj rezoluciji)
		//stara se unistava
		if(img==m_newImg)
		{
			m_img.removeFromParent();
			m_img=m_newImg;
			m_newImg=null;
		}
		
		clearMsg();
		
/*		if(m_listeners!=null)
			m_listeners.fireLoad(this);*/
	}
	
	public void onError(Widget sender)
	{
//		com.google.gwt.user.client.Window.alert("error loading image");
		Assertion.asert(sender instanceof MyImageComponent, "nije instanca myimage component");
		MyImageComponent img=(MyImageComponent)sender;
		img.setError(true);
		if(img==m_img)	
			setErrorMsg();
		else if(img==m_newImg)
		{
			m_newImg.removeFromParent();
			m_newImg=null;
			//posto ucitavanje nove slike nije uspelo vracam url na poslednji uspesni
			if(m_img != null)
			{
				m_url = m_img.getUrl();
			}
		}
				
/*		if(m_listeners!=null)
			m_listeners.fireError(this);*/
	}
	
/*	public void removeLoadListener(LoadListener l)
	{
		if(m_listeners!=null)
			m_listeners.remove(l);
	}
	
	public void addLoadListener(LoadListener l)
	{
		if(m_listeners==null)
			m_listeners=new LoadListenerCollection();
		m_listeners.add(l);
	}*/
	

	public void setHeight(String height)
	{
		m_height=Integer.parseInt(height.substring(0, height.length()-2));
		m_canvas.setHeight(height);
		if(m_img!=null)
			m_img.setHeight(height);
		if(m_newImg!=null)
			m_newImg.setHeight(height);
		if(m_message!=null)
			m_message.setHeight(height);
	}
	
	public void setWidth(String width)
	{
		m_width=Integer.parseInt(width.substring(0, width.length()-2));
		m_canvas.setWidth(width);
		if(m_img!=null)
			m_img.setWidth(width);
		if(m_newImg!=null)
			m_newImg.setWidth(width);
		if(m_message!=null)
			m_message.setWidth(width);
	}
	
	public void setPixelSize(int width, int height)
	{
		m_height=height;
		m_width=width;
		m_canvas.setPixelSize(width, height);
		if(m_img!=null)
			m_img.setPixelSize(width, height);
		if(m_newImg!=null)
			m_newImg.setPixelSize(width, height);
		if(m_message!=null)
			m_message.setPixelSize(width, height);
	}
	
	private void setMsg(String text)
	{
		if(m_message!=null)
		{
			m_canvas.remove(m_message);			
			m_message=null;
		}		
		m_message=new Message();
		m_canvas.add(m_message);
		setWidgetSize(m_message);
		m_canvas.setWidgetPosition(m_message, 0, 0);
		m_message.setData(text);
	}
	
	private void setLoadingMsg()
	{
		if(this.m_loadingMsg != null)
			setMsg(m_loadingMsg);
		else
			setMsg(Message.LOADING_MSG);
	}
	
	public boolean isError()
	{
		return m_errorMessage;
	}
	
	public void reload()
	{
		Helper.getPreventMouseEvents().Pause();
		if(m_img != null)
		{
			m_img.removeFromParent();
			m_img = null;
		}
		if(m_message != null)
			m_message.removeFromParent();
		m_message=null;
		if(m_newImg != null)
		{
			m_newImg.removeFromParent();
			m_newImg=null;
		}
		String url = m_url;
		m_url="";
		setData(url);
		Helper.getPreventMouseEvents().Resume();
	}
	
	public void setErrorText(String error)
	{
		m_errorMsg = error;
	}
	
	public void setLoadingMsg(String loading)
	{
		m_loadingMsg = loading;
	}
	
	public void setTryAgain(String tryAgain)
	{
		this.m_tryAgain = tryAgain;
	}
	
	private void setErrorMsg()
	{
		m_errorMessage = true;
		if(m_errorMsg != null)
			setMsg(m_errorMsg);
		else
			setMsg(Message.ERR_MSG);
		String again = TRY_AGAIN;
		if(this.m_tryAgain != null)
			again = m_tryAgain;
		Button tryAgain=new Button(again);
		m_message.setCentredWidget(tryAgain);
		tryAgain.addClickListener(new ClickListener(){
			
			public void onClick(Widget sender)
			{
				reload();
			}
		});
	}
	
	private void clearMsg()
	{
		m_errorMessage = false; // ako nije prikazana poruka o gresci trebalo bi da je sve OK
		if(m_message!=null)
		{
			m_canvas.remove(m_message);
			m_message=null;
		}
	}
	
	public void setData(String url)
	{	
		
	//ako je vec ucitana ili se ucitava izlazi iz funkcije
		if(url.equals(m_url))
			return;
		m_url = url;
		//ako nije ucitan dati url ali postoji slika
		//prikazuje se nova preko postojece
		if(m_img!=null)
		{
			//ako je predhodna imala gresku onda 
			//nema potrebe prikazati novu preko nje
			if(m_img.isError())
			{
				clearMsg();
				m_img.setUrl(url);
			}//ako nema greske onda se nova prikazuje preko stare
			else if(m_newImg==null)
			{
				m_newImg=createImage();			
				m_newImg.setUrl(url);
				attachImage(m_newImg);
			}
			else
				m_newImg.setUrl(url);				
		}	
		//ako slika ne postoji
		//prikazuje se poruka o ucitavanju
		//preko poruke se prikazuje slika
		else
		{
			Assertion.asert(m_message==null, "ne sme postojati poruka pre kreiranja nove slike!");
			setLoadingMsg();
			m_img=createImage();
			m_img.setUrl(url);
			attachImage(m_img);
		}
	}
	
/*	public boolean isSizeSetOnLoad()
	{
		return true;
	}	*/
}
