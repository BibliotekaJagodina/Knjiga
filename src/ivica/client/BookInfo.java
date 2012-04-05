package ivica.client;

import java.util.*;
import com.google.gwt.json.client.*;
import com.google.gwt.http.client.*;
public class BookInfo 
{
	public static final String IMAGE_FILE_PREFIX="prefix";
	public static final String EXTENSION="ekstenzija";
	public static final String COUNTER="brojac";
	public static final String PAGE_COUNT="broj_strana";
	public static final String QUALITY="kvalitet";
	public static final String HEIGHT="v"; //v- visina
	public static final String WIDTH="s"; //s- sirina
	public static final String NAME="naziv"; //naziv klvaliteta i naziv strane
	public static final String BASE_URL="url";
	public static final String DEFAULT_DIMENSION="podrazumevana_dimenzija";
	public static final String SPECIFIC_DIMENSIONS="pojedinacne_dimenzije";
//	public static final String BOOK_ID="id_knjige";
	public static final String PICTURES_SUPPORTED="podrzane_slike";
	public static final String TEXT_SUPPORTED="podrzan_tekst";
	public static final String NAMED_PAGES="imenovane_strane";
	public static final String INDEX="i"; //i -index
	public static final String PAGE_NUMBERING="numeracija";
	public static final String SKIP="preskoci";
	public static final String START_NUM="pocetni_br";
	public static final String SPAN="narednih";
	public static final String TEXT_BASE_URL="url_teksta";
	public static final String BOOK_TITLE="naslov_knjige";
	public static final String THUMBNAILS="minijaturne";
	public static final String CONTINOUS_DEFAULT  = "neprekidne_default";
	public static final String SLIDE_SUPPORTED = "podrzan_slajd_prikaz";
	public static final String CONTINOUS_SUPPORTED = "podrzan_neprekidni_prikaz";
	public static final String CONTENTS_SUPPORTED = "podrzan_sadrzaj";
//	public static final String THUMBNAIL_COVER="naslovna_minijaturna";
	
	
	
	private String m_bookId = null;
	private JSONObject m_json = null;

	private PageName[] m_pageNames=null;
	
	private List<Quality> m_qualityDefinitions = null;
	
	/**
	 * 
	 * Sve stranice moraju da imaju ime. 
	 * Ako se radi o stranici koja samo prikazuje sistemsku poruku
	 * na primer stranice od 1-10 su izostavljene
	 * onda je info="poruka" a fileName=null i textGet=null
	 *u suprotnom fileName = prefix001.ext i textGet="bid=12&pid=1" 
	 *pod uslovom da su slike i ili tekst podrzani.
	 * 
	 */
	protected class PageName
	{
		protected String name="";
		protected String fileName=null;
		protected String textGet=null;
		protected String info=null;
	}
	
	
	public static class Dimension
	{
		protected int height=0;
		protected int width=0;
		public int getHeight()
		{
			return height;
		}
		public int getWidth()
		{
			return width;
		}
	}
	/*
	 *  naziv:string,
	 *  podrazumevana_dimenzija {visina: number, sirina:number },
	 *  pojedinacne_dimenzije[{visina: number ,sirina: number}]
	 */
	public static class Quality
	{
		protected String name=null; //npr: manji kvalitet
		protected String url=null;  // osnovni url na koji se dodaje prefiks+brojac+.+ekstenzija
		
		protected Dimension defaultDimension=null; //podrazumevane dimenzije svih strana
		protected JSONArray specificDimensions=null; //niz koji cuva dimenzije svake strane
		
		protected Quality()
		{
			
		}
		
		protected Dimension getDefaultDimension()
		{
			return defaultDimension;
		}
		
		protected  boolean isSpecificDimensionsSet()
		{
			return !(specificDimensions==null);
		}
		protected Dimension getSpecificDimension(int pageIndex)
		{
			if(specificDimensions==null)
				return null;
			if(specificDimensions.isArray()==null)
				return null;
			Assertion.asert(pageIndex>=0 && pageIndex<specificDimensions.isArray().size(), "Indeks je van granica");
			if(!(pageIndex>=0 && pageIndex<specificDimensions.isArray().size()))
				return null;
			JSONValue val;
			val=specificDimensions.get(pageIndex);
			if(val==null)
				return null;
			JSONObject obj=val.isObject();
			if(obj==null)
				return null;		
			JSONValue heightVal=obj.get(HEIGHT);
			JSONValue widthVal=obj.get(WIDTH);
			if(heightVal==null || widthVal==null)
				return null;
			Dimension dim = new Dimension();	
			dim.height = (int)heightVal.isNumber().doubleValue();
			dim.width = (int)widthVal.isNumber().doubleValue();
			return dim;			
		}
		
		public String getName(){
			return name;
		}
		protected String getUrl(){
			return url;
		}		
	}
	
	public BookInfo(JSONObject obj, String bookId)
	{
		m_bookId = bookId;
		m_json = obj;
		Assertion.asert(isPictureSupported()||isTextSupported(), "Mora biti podrzano bar jedno, tekst ili slike");

		int pageCount=this.getPageCount();
		m_pageNames=new PageName[pageCount];
		PageNumbering numbering=this.getNextPageNumberingInfo();
		NamedPage named=getNextNamedPageInfo();
		int nonInfoPageIndex=0;
		boolean infoPageIsCurrent;
		for(int i=0;i<pageCount;i++)
		{
			infoPageIsCurrent=false;
			//kreiram strukturu numeracije/imena
			m_pageNames[i]=new PageName();			
			//prvo numerisem strane
			//svaka strana ima svoj indeks i moze da ima 
			//ime koje je u formatu broja ili stringa (ovde trazim broj)
			if(numbering!=null)
			{
				//ako je numeracija vazi za tekuci indeks i definise
				//preskocene strane onda upisujem informacije o info strani
				//i prelazim na sledecu numeraciju
				if(numbering.index == i  &&  numbering.skip)
				{
					int first=numbering.startNum;
					int last=numbering.startNum+numbering.span-1;
					if(first-last == 0)
						m_pageNames[i].info="Страна "+first+" је изостављена";
					else
						m_pageNames[i].info="Стране од "+numbering.startNum+" до "+last+" су изостављене!";
					m_pageNames[i].name="Информација";
					infoPageIsCurrent=true;
					numbering=this.getNextPageNumberingInfo();
				}
				else //u suprotnom SE NE RADI O INFO STRANI
				{
					//strana ne sme da sadrzi info tekst
					infoPageIsCurrent=false;
					m_pageNames[i].info=null;
					int lastIndexInRange = numbering.index + numbering.span - 1;
					//ako se trenutni indeks nalazi u opsegu numeracije
					//onda numerisem stranu, strana moze biti i ne numerisana 
					if( (i >= numbering.index) && (i <= lastIndexInRange))
						m_pageNames[i].name = i - numbering.index + numbering.startNum +"";
					if(i >= lastIndexInRange)
						numbering=this.getNextPageNumberingInfo();	
				}
			}
			//ako se radi o info strani sve je podeseno i nista vise se ne sme podesavati
			//ovo nije uradjeno u predhodom bloku jer strana moze biti ne numerisana
			// AKO SE NE RADI O INFO STRANI :
			if(!infoPageIsCurrent)
			{
				//ako je podrzano prikazivanje slike izracunavam ime fajla
				//kao prefix+brojac+.ekstenzija				
				if(this.isPictureSupported())
					m_pageNames[i].fileName=this.getNamePrefix() + 
											this.getCounterNumber(nonInfoPageIndex)+
											"."+this.getExtension();							
				//ako je podrzano prikazivanje teksta izracunavam
				//get deo URL-a za tekst kao bid=brKnjgie&pid=brStrane
				if(this.isTextSupported())
					m_pageNames[i].textGet="?bid="+getBookID()+
											"&pid="+String.valueOf(nonInfoPageIndex);
				nonInfoPageIndex++; 
			}
			
			//ako je postavljeno posebno ime za stranu onda se numeracja gubi
			//ako je bila postavljena			
			if(named != null)
			{
				if(named.index == i)
				{
					m_pageNames[i].name = named.name;
					named=this.getNextNamedPageInfo();
				}
			}			
		}
		this.goToFirstNamedPageInfo();
		this.goToFirstPageNumberingInfo();
	}
	
	private String getCounterNumber(int index)
	{
		String snum;
		if(this.getCounterType().equals("001"))
		{
			snum=String.valueOf(index+1);
			if(snum.length()==1)
				snum="00"+snum;
			if(snum.length()==2)
				snum="0"+snum;
		}
		else if(this.getCounterType().equals("1"))
		{
			snum=String.valueOf(index+1);
		}
		else
			snum=String.valueOf(index+1);
		return snum;
	}
	
	private int getIntegerVal(String key)
	{
		JSONValue val=m_json.get(key);
		if(val!=null)
		{
			JSONNumber num=val.isNumber();
			if(num!=null)
				return (int)num.doubleValue();
		}
		return 0;
	}
	
	private String getStringVal(String key)
	{
		JSONValue val=m_json.get(key);
		if(val!=null)
		{
			JSONString str=val.isString();
			if(str!=null)
				return str.stringValue();
		}
		return "";
	}
	/**
	 * 
	 * @return prefiks imena fajla, ili "" ako nije postavljen
	 */
	protected String getNamePrefix()
	{
		return getStringVal(IMAGE_FILE_PREFIX);
	}
	
	/**
	 * 
	 * @return ako nije postavljen nalsov onda "" inace naslov
	 */
	public String getBookTitle()
	{
		return getStringVal(BOOK_TITLE);
	}
	
	/**
	 * 
	 * @return url do servisa koji vraca tekst ili null ako nije naveden
	 */
	protected String getTextUrl()
	{
		String ret=getStringVal(TEXT_BASE_URL);
		if(ret.equals(""))
			return null;
		ret=ret.trim();
		if(ret.charAt(ret.length()-1)!='/')
			ret+="/";
		ret=URL.encode(ret);
		return ret;
	}
	
	protected String getExtension()
	{
		String ext=getStringVal(EXTENSION);
		if(ext.equals(""))
			ext="jpg";
		return ext;
	}
	
	protected String getCounterType()
	{
		String counterType=getStringVal(COUNTER);
		if(counterType.equals(""))
			counterType="1";
		return counterType;
	}
	
	public int getPageCount()
	{
		return getIntegerVal(PAGE_COUNT);
	}
	
	//kvalitet:
	//[
	// {
	//  naziv:string,                              -obavezno
	//  url:string,								   -obavezno
	//  podrazumevana_dimenzija:{				   -opciono (ne mora ako su navede pojedinacne dimenzije
	//							  visina:number  
	//							  sirina:number,
	//							}
	//  pojedinacne_dimenzije:[ 					-opciono (ne mora ako je navedena podrazumevana dimenzija)
	//							{
	//							  visina:number,
	//							  sirina:number
    //							}
	//						   ]
	// }
	//]
								
	public List<Quality> getQualityDefinitions()
	{
		
		if(this.m_qualityDefinitions != null)
			return m_qualityDefinitions;
		
		List<Quality> ret=new ArrayList<Quality>();
		JSONValue val = m_json.get(QUALITY);
		if(val == null)
			return null;
		JSONArray ar = val.isArray();
		if(ar==null)
			return null;
		for(int i=0;i < ar.size();i++)
		{
			JSONValue value=ar.get(i);
			if(value != null)
			{
				Quality quality = new Quality();
				Assertion.asert(value.isObject() != null, "Clan niza mora biti objekt");
				JSONObject obj = value.isObject() ;
				quality.name = obj.get(NAME).isString().stringValue();
				String url = obj.get(BASE_URL).isString().stringValue();
				//dodajem na kraj url-a / ako ne postoji
				url=url.trim();
				if(url.charAt(url.length()-1) != '/')
					url+="/";
				quality.url = URL.encode(url);			
				JSONValue dimensionVal=obj.get(DEFAULT_DIMENSION);
				if(dimensionVal == null)
					quality.defaultDimension = null;
				else
				{	
					JSONObject dimensionObj=dimensionVal.isObject();
					JSONValue tmpVal=dimensionObj.get(HEIGHT);
					quality.defaultDimension=new Dimension();
					quality.defaultDimension.height = (int)tmpVal.isNumber().doubleValue();
					tmpVal=dimensionObj.get(WIDTH);
					quality.defaultDimension.width  = (int)tmpVal.isNumber().doubleValue();
				}
				
				JSONValue specificDimensionsVal=obj.get(SPECIFIC_DIMENSIONS);
				quality.specificDimensions = null;
				if(specificDimensionsVal != null)
				{	
					JSONArray specificDimensionsArray=specificDimensionsVal.isArray();
					if(specificDimensionsArray != null)
						quality.specificDimensions=specificDimensionsArray;
				}
				Assertion.asert(quality.defaultDimension != null  ||
						 		quality.specificDimensions != null, "Mora biti poznate podrazumevana dimenzija za sve strane ili pojedinacna dimenzija svake strane!");
				ret.add(quality);				
			}
		}
		this.m_qualityDefinitions = ret;
		return ret;
	}
	
	/**
	 * 
	 * @return  null ako nije definisano
	 */
	public Quality getThumbQuality()
	{
		JSONValue val=m_json.get(THUMBNAILS);
		if(val == null)
			return null;
		JSONObject obj=val.isObject();
		if(obj == null)
			return null;
		Quality q=new Quality();
		q.name=null;
		JSONString str=obj.get(BASE_URL).isString();
		if(str==null)
			return null;
		q.url=str.stringValue();
		val=obj.get(DEFAULT_DIMENSION);
		if(val == null)
			return null;
		JSONObject dimObj=val.isObject();
		if(dimObj == null)
			return null;		
		q.defaultDimension=new Dimension();
		q.defaultDimension.height=(int)dimObj.get(HEIGHT).isNumber().doubleValue();
		q.defaultDimension.width=(int)dimObj.get(WIDTH).isNumber().doubleValue();
		return q;		
	}
	
	/*
	 *  id_knjige : number
	 */
	protected String getBookID()
	{
/*		JSONValue bookIdVal = m_json.get(BOOK_ID);
		if(bookIdVal!=null)
			return (int)bookIdVal.isNumber().getValue();
		return -1;*/
		return m_bookId;
	}

	
	/*
	 * podrzan_tekst:boolean
	 */
	public boolean isTextSupported()
	{
		JSONValue textSupportedVal=m_json.get(TEXT_SUPPORTED);
		if(textSupportedVal==null)
			return false;
		return textSupportedVal.isBoolean().booleanValue();
	}
	
	public boolean isContentsSupported()
	{
		JSONValue contentsSupportedVal = m_json.get(CONTENTS_SUPPORTED);
		if(contentsSupportedVal == null)
			return false;
		return contentsSupportedVal.isBoolean().booleanValue();
	}
	
	public boolean isPictureSupported()
	{
		JSONValue val=m_json.get(PICTURES_SUPPORTED);
		if(val==null)
			return false;
		return val.isBoolean().booleanValue();
	}
	
	public boolean isContinousSupported()
	{
		JSONValue val = m_json.get(CONTINOUS_SUPPORTED);
		if(val == null)
			return false;
		return val.isBoolean().booleanValue();
	}
	
	
	public boolean isSlideSupported()
	{
		JSONValue val = m_json.get(SLIDE_SUPPORTED);
		if(val == null)
			return false;
		return val.isBoolean().booleanValue();
	}

	public boolean isContionusDefaultLayout()
	{
		JSONValue val = m_json.get(CONTINOUS_DEFAULT);
		if(val == null)
			return true;
		return val.isBoolean().booleanValue();
	}
	
	protected class NamedPage
	{
		public int index;
		public String name;
	}
	
	private int m_currentNamedPageInfo=0;
	
	/**
	 * 
	 * @return vraca strukturu sa indeksom i nazivom strane
	 * ukloliko nema vise imenovanih strana vraca null
	 */
	protected void goToFirstNamedPageInfo()
	{
		m_currentNamedPageInfo=0;
	}
	
	protected NamedPage getNextNamedPageInfo()
	{
			JSONValue val=m_json.get(NAMED_PAGES);
			if(val==null)
				return null;
			JSONArray ar=val.isArray();
			if(ar==null)
				return null;
			if(m_currentNamedPageInfo>ar.size()-1)
				return null;
			val=ar.get(m_currentNamedPageInfo);
			if(val==null)
				return null;
			JSONObject obj=val.isObject();
			if(obj==null)
				return null;
			
			NamedPage ret=new NamedPage();
			ret.index=(int)obj.get(INDEX).isNumber().doubleValue();
			ret.name=obj.get(NAME).isString().stringValue();
			m_currentNamedPageInfo++;
			return ret;
			
	}
	
	protected class PageNumbering
	{
		public int index; //indeks stranice
		public int startNum; //pocetni broj numeracije, ako je -1 onda nema numeracije
		public boolean skip=false; 
		public int span=1; //numeracija se odnosi na sledecih "span" stranica
	}
	
	protected void goToFirstPageNumberingInfo()
	{
		m_currentPageNumbering=0;
	}
	private int m_currentPageNumbering=0;
	
	protected PageNumbering getNextPageNumberingInfo()
	{
		JSONValue val=m_json.get(PAGE_NUMBERING);
		if(val == null)
			return null;
		JSONArray ar=val.isArray();
		if(ar==null)
			return null;
		if(ar.size() - 1 < m_currentPageNumbering)
			return null;
		val=ar.get(m_currentPageNumbering);
		JSONObject obj=ar.get(m_currentPageNumbering).isObject();
		if(obj==null)
			return null;
		PageNumbering ret=new PageNumbering();
		ret.index=(int)obj.get(INDEX).isNumber().doubleValue();
		ret.startNum=(int)obj.get(START_NUM).isNumber().doubleValue();
		ret.skip=obj.get(SKIP).isBoolean().booleanValue();
		ret.span=(int)obj.get(SPAN).isNumber().doubleValue();
		m_currentPageNumbering++;
		return ret;		
	}
	

	public static class PageDefinition
	{
		protected String imageUrl = null;
		protected String textUrl = null;
		protected String infoText=null;
		protected String name="";
		protected Dimension specificDimension=null;
		
		/**
		 * 
		 * @return prazan string ili ime
		 */
		public String getName()
		{
			return name;
		}
		
		public String getInfoText()
		{
			return infoText;
		}
		
		public String getImageUrl()
		{
			return imageUrl;
		}
		public String getTextUrl()
		{
			return textUrl;
		}
		public Dimension getSpecificDimension()
		{
			return specificDimension;
		}
		public boolean isInfoPage()
		{
			return  infoText != null;
		}
		public boolean isTextSupported()
		{
			return textUrl != null;
		}
		public boolean isImageSupported()
		{
			return imageUrl != null;
		}
		public boolean hasSpecificDimension()
		{
			return specificDimension != null;
		}
	}
	
	public Dimension getDefaultPageDimension(Quality q)
	{
		Dimension ret=q.getDefaultDimension();
		return ret;
	}
	
	public PageDefinition getPageDefinition(Quality q, int index)
	{
		if(index<0 || index>this.getPageCount()-1)
			return null;
		PageDefinition ret=new PageDefinition();
		if(m_pageNames[index].fileName != null)
		{
			Assertion.asert(q.getUrl()!=null, "Osnovni url mora biti naveden");
			ret.imageUrl=q.getUrl() + m_pageNames[index].fileName;
		}
		if(m_pageNames[index].textGet != null)
			ret.textUrl=this.getTextUrl()+m_pageNames[index].textGet;
		
		ret.infoText=m_pageNames[index].info;
		ret.name=m_pageNames[index].name;
		ret.specificDimension=q.getSpecificDimension(index);		
		return ret;
	}
}