package ivica.client.page.pagecomponent;

//import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.http.client.*;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
//import com.google.gwt.core.client.*;


//URADI - razmotri, ako je potrebno uništavanje i kreiranje MyTextArea više puta
//Napraviti keš gde će se MyTextArea data čuvati pod ključem url
//to bi trebalo da omogući uništavanje i kreiranje 
//više puta stranice sa istim tekstom bez
//potrebe da se tekst zahteva od servera svaki put

//Uradi, Izvedi MyTextArea iz Composite

public class MyTextArea extends TextArea 
{

	protected class TextRequestCallback implements RequestCallback {

		private MyTextArea m_textArea=null;
		public TextRequestCallback(MyTextArea textArea)
		{
			m_textArea=textArea;
		}
		
		public void onError(Request r, Throwable arg1) 
		{
			m_textArea.m_listeners.fireError(m_textArea);
		}

		public void onResponseReceived(Request request, Response response) 
		{
			//URADI ??? --razmotriti: Da li je Moguc RACE problem ako dva uzastopna zahteva dodju obrnutim redosledom
			//u veoma kratkom vremenskom peridu, cini se da je malo verovatno u praksi ali...
			
			//ako ovo nije poslednji zahtev onda se zahtev otkazuje i ne radi se nista
			//jer se poslednji zahtev vec odigrao ili tek treba da se odigra
			if(!request.equals(m_textArea.m_lastRequest))
				return;
			
			if(response.getStatusCode()!=200)
			{
				m_textArea.m_listeners.fireError(m_textArea);
				return;
			}
			m_textArea.setText(response.getText());
			m_textArea.m_listeners.fireLoad(m_textArea); 
		}
	}
	
	
	
	protected LoadListenerCollection m_listeners=null;
	//promenljiva koja cuva poslednji 
	protected Request m_lastRequest=null;
	private String m_url;
	private boolean m_empty=true;
	private Message m_message=null;
	
	private boolean m_isLoading=false;
//	private static String baseUrl=null;

	
//URADI sve stranice imaju isti deo url-a, razlika je samo u delu iza ?
//zato bi trebalo pamtiti zajednicki deo u okviru staticke promenljive!
	
	
	public boolean isLoading()
	{
		return m_isLoading;
	}
	
	public void setLoading(boolean loading)
	{
		m_isLoading=loading;
	}

	//metod podrazumeva da je url vec enkodovan
	//ucitava stranicu knjige u TextArea kontrolu svaki put kad se pozove, bez obzira da li je tekst 
	//ranije vec bio ucitan ili ne!
	public void setData(String url)
	{
		//String[] urlParts=url.split("?");
		
		//ako postoji zahtev na koji se ceka onda se taj zahtev otkazuje --razmotriti moguc RACE problem!
		if(m_lastRequest!=null)
			if(m_lastRequest.isPending())
				m_lastRequest.cancel();
		
		RequestBuilder rb=new RequestBuilder(RequestBuilder.GET,url);
		try
		{
			//ovo je potrebno da bi se vrednosti automatski nasle u _POST 
			//PHP varijabli: 
			//rb.setHeader("Content-Type","application/x-www-form-urlencoded");
			
			//String postData=URL.encode("knjiga="+bookID+"&strana="+pageID);
			m_lastRequest=rb.sendRequest("",new TextRequestCallback(this));
		}
		catch(RequestException e)
		{
			m_listeners.fireError(this);
		}
	}
	
	public boolean isEmpty()
	{
		return m_empty;
	}
	
	public void setEmpty(boolean empty)
	{
		m_empty=empty;
	}
	
	public String getUrl()
	{
		return m_url;
	}
	
	public int getHeight()
	{
		return getOffsetHeight();
	}
	
	public int getWidth()
	{
		return getOffsetWidth();
	}
	public void addLoadListener(LoadListener l)
	{
		if(m_listeners==null)
			m_listeners=new LoadListenerCollection();
		m_listeners.add(l);
	}
	
	public void removeLoadListener(LoadListener l)
	{
		if(m_listeners!=null)
			m_listeners.remove(l);
	}
	public boolean isSizeSetOnLoad()
	{
		return false;
	}
	public Widget getWidget()
	{
		return (Widget)this;
	}
	
	public void setLoadingMsg()
	{
		if(m_message==null)
			m_message=new Message();
		m_message.setData(Message.LOADING_MSG);
	}
	
	public void setErrorMsg()
	{
		if(m_message==null)
			m_message=new Message();
		m_message.setData(Message.ERR_MSG);
	}
	
	public void clearMsg()
	{
		m_message=null;
	}
	
	public Message getMsg()
	{
		if(m_message!=null)
			return m_message;
		else
			return null;
	}
	
	public void setVisible(boolean visible)
	{
//		if(m_message!=null)
//			m_message.getWidget().setVisible(visible);
		super.setVisible(visible);
	}	
	
	
}
