package ivica.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.*;

import ivica.client.BookInfo.Quality;
import ivica.client.mypanel.*;

import ivica.client.page.*;
import com.google.gwt.user.client.*; 
import com.google.gwt.user.client.EventListener;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.json.client.*;

import java.util.*;
import ivica.client.viewport.MousePanel;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.GwtEvent;

public class Knjiga implements EntryPoint, RequestCallback, ChangeHandler, PageChangeListener
{
		public static String META_DATA_URL="";
		private static final String SETTINGS="podesavanja";
		private static final String SERVICE_URL="servis";
		private static final String BOOK_ID="id_knjige";
		private static final String BOOK="knjiga1";
		private static final String BOOK_META = "knjiga_meta";
		private static final String BOOK_META_JSON_STRING = "jsonString";
		private static final String CONTENTS_META = "sadrzaj_meta";
		private static final String CONTENTS_META_JSON_STRING = "jsonString";
		private static final String LINKOVI_JSON_STRING = "jsonString";
		private static final String BOOK_CONTENT = "knjiga_sadrzaj";
		private static final String TOOLBAR_LINKS = "linkovi";
		private static final String TOOLBAR_LINKS_A = "a";
		private static final String TOOLBAR_LINKS_HREF = "href";
		private static final String CONTENTS_ID = "sadrzaj";		
		private static final String START_PAGE = "pocetna_stranica";
		private static final String PAGE_INDEX = "index";
		
		private static final String PREVIOUS_PAGE = "Претходна страна (Лева стрелица <-)";
		private static final String NEXT_PAGE = "Следећа страна (Десна стрелица ->)";
		private static final String ZOOM_IN = "Увећање (Shift +)"; 
		private static final String ZOOM_OUT = "Смањење (Shift -)";
		private static final String PAGE_NAME = "Назив стране: ";
		private static final String IMAGE_QUALITY = "Квалитет слике";
		private static final String CONTINOUS = "Непрекидни приказ страница";
		private static final String SLIDE = "Приказ појединачних страница";
		
		private static final String SERVER_BINDING_ERROR = "Грешка при повезивању са сервером!";
		private static final String BAD_DATA_FORMAT = "Подаци које је вратио сервер нису у одговарајућем формату!";
		private static final String UNESPECTED_SERVER_RESPONSE = "Грешка, сервер није одговорио на очекивани начин!";

		//indeksi moraju da idu istim redosledom kao dugmad na vert. tabu
		private static final int THUMB_TAB = 0;
		private static final int CONTENTS_TAB = 1;
		
		private static final int TOOLTIP_DELAY = 3000;
		
		private final FocusPanel m_dummyFocus = new FocusPanel();
		
		private final MyPanelLayout layout = new MyVerticalLayout(true);
		private final PageView view = new PageView();

		private final MyVerticalTab m_tabBar = new MyVerticalTab(/*barSize*/ 40, /*buttonWidth=*/ 36, /*buttonHeight*/45, true);
		private final ThumbView m_thumbView = new ThumbView();
		private final ContentstView m_contentsView = new ContentstView();
		private final BrowseView m_browseView = new BrowseView();

		
		private Request m_bookRequest = null;
		private Request m_contentsRequest = null;
		//pre nego sto je ucitana current je null a requested je id
		private String m_requestedBookId = null;
		//kad je ucitana onda je requested null a current je id
		private String m_currentBookId = null;
		
		private final MyToolbar m_toolbar = new MyToolbar(/*horizontal*/true,/*pushToolbar*/ false, 36, 5, 30);

		 private final ListBox m_qualityList = new ListBox();
		 private final TextBox m_pageName = new TextBox();
		 private final TextBox m_pageIndex = new TextBox();
		 private final TextBox m_pageCount = new TextBox();
		 MyButton m_btnZoomIn = null;
		 MyButton m_btnZoomOut = null;
		 MyButton m_btnNext = null;
		 MyButton m_btnBack = null;
	
		private final MyToolbar m_layoutToolbar = new MyToolbar(/*horizontal*/true,/*pushToolbar*/ true, 36, 5, 30);
		 MyButton m_btnContinous = null;
		 MyButton m_btnSlide = null;
		
		
		private Widget createToolbar()
		{
			AbsolutePanel ret = new AbsolutePanel();
			ret.setHeight(m_toolbar.getClientSize()+"px");
			
			m_toolbar.setStyleName("MyToolbar");
			m_toolbar.setWidth("440px");
			
			m_layoutToolbar.setStyleName("MyToolbar");
			m_layoutToolbar.setOptionToolbar();
			m_layoutToolbar.setWidth("250px");
			
			m_pageName.setStyleName("PageNameTextBox");
			m_pageIndex.setStyleName("PageNameTextBox");
			m_pageIndex.setWidth("25px");
			m_pageCount.setStyleName("PageNameTextLabel");
			m_pageCount.setWidth("35px");
			m_pageIndex.addKeyDownHandler(new KeyDownHandler() {
				public void onKeyDown(KeyDownEvent event) {
			    	  if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			    	  {
							int i=0;
							try
							{
								i = Integer.parseInt(m_pageIndex.getText());
							}
							catch(Exception e)
							{	
								return;
							}
							if(i-1 >= 0 && i-1 < view.getPageCount())
								view.showPage(i-1);
							else
								m_pageIndex.setText(view.getCurrentPage()+1+"");
			    	  }
				}
			});
			
			m_pageIndex.addBlurHandler(new BlurHandler(){
				public void onBlur(BlurEvent b)
				{
					m_pageIndex.setText(view.getCurrentPage()+1+"");
				}
			});
			
			m_pageName.addKeyDownHandler(new KeyDownHandler() {
			      public void onKeyDown(KeyDownEvent event) {
			    	if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
					{			    		
						if(!view.showPage(m_pageName.getText()))
							m_pageName.setText(view.getCurrentPageName());
					}
				}
			});
			
			m_pageName.addBlurHandler(new BlurHandler(){
				public void onBlur(BlurEvent b)	{
					if(view != null && m_pageName != null)
						m_pageName.setText(view.getCurrentPageName());
				}
			});			
			m_qualityList.setStyleName("QualityList");
			m_pageCount.setReadOnly(true);
			m_qualityList.setWidth("100px");
			m_pageName.setWidth("120px");
			
			
			MousePanel mousePageName = new MousePanel();
			mousePageName.setWidget(m_pageName);
			mousePageName.addMouseListener(new TooltipListener("",TOOLTIP_DELAY, "Tooltip")
			{
				public void onMouseEnter(Widget sender)
				{
					if(m_pageName.getText().trim().equals(""))
						return;
				    if (tooltip != null) {
				        tooltip.hide();
				      }
				      tooltip = new Tooltip(sender, offsetX, offsetY, PAGE_NAME+ m_pageName.getText(), delay, styleName);
				      tooltip.show();
				}
			});
			Image separator1 = new Image("separator.gif");
			separator1.setPixelSize(3,17);
			m_toolbar.addWidget(separator1);
			mousePageName.setHeight("20px");
			m_toolbar.addWidget(mousePageName);			
			m_toolbar.addWidget(m_pageIndex);
			m_toolbar.addWidget(m_pageCount);
		//back
			m_btnBack = m_toolbar.newButton(new MyButton.Command()
				{
					public void doCommand(MyButton btn)
					{
						if(!view.previousPage())
							m_btnBack.setEnabled(false);
						m_btnNext.setEnabled(true);
					}
				},
				"back1.gif","back1-disabled.gif", 19, 18, 24, false);
//				"Nazad", 5000, "Tooltip");
			m_btnBack.addMouseListener(new TooltipListener(PREVIOUS_PAGE, TOOLTIP_DELAY, "Tooltip"));
		//next
			m_btnNext = m_toolbar.newButton(new MyButton.Command()
			{
				public void doCommand(MyButton btn)
				{
					m_btnNext.setEnabled(view.nextPage());
				}
			}, "next1.gif","next1-disabled.gif" ,19, 18, 24, false);
			m_btnNext.addMouseListener(new TooltipListener(NEXT_PAGE, TOOLTIP_DELAY, "Tooltip"));

			Image separator2 = new Image("separator.gif");
			separator2.setPixelSize(3,17);
			m_toolbar.addWidget(separator2);
			
		//zoomIn
			m_btnZoomIn = m_toolbar.newButton(new MyButton.Command()
				{
					public void doCommand(MyButton btn)
					{					
						if(!view.zoomIn())
							m_btnZoomIn.setEnabled(false);
						m_btnZoomOut.setEnabled(true);
					}
				},"plus.gif","plus-disabled.gif", 14, 14, 24, false);
			m_btnZoomIn.addMouseListener(new TooltipListener(ZOOM_IN, TOOLTIP_DELAY, "Tooltip"));
		
		//zoomOut	
			m_btnZoomOut = m_toolbar.newButton(new MyButton.Command()
			{
				public void doCommand(MyButton btn)
				{					
					if(!view.zoomOut())
						m_btnZoomOut.setEnabled(false);
					m_btnZoomIn.setEnabled(true);
				}
			},"minus.gif","minus-disabled.gif", 14, 14, 24, false);
			m_btnZoomOut.addMouseListener(new TooltipListener(ZOOM_OUT, TOOLTIP_DELAY, "Tooltip"));
			

			MousePanel mouseQualityList = new MousePanel();
			mouseQualityList.setWidget(m_qualityList);
			mouseQualityList.addMouseListener(new TooltipListener(IMAGE_QUALITY, TOOLTIP_DELAY, "Tooltip"));
			m_qualityList.setVisibleItemCount(1);
			m_qualityList.addChangeHandler(this);								
			
			m_toolbar.addWidget(mouseQualityList);
			Image separator3 = new Image("separator.gif");
			separator3.setPixelSize(3,17);
			m_toolbar.addWidget(separator3);

			
	
		//drugi toolbar
			m_btnContinous = m_layoutToolbar.newButton(
				new MyButton.Command()
				{
					public void doCommand(MyButton btn)
					{
						view.changeLayout(PageView.CONTINOUS);
					}
				},
				"continous.gif", "continous-disabled.gif", 19, 15, 24, false);
			m_btnContinous.addMouseListener(new TooltipListener(CONTINOUS,TOOLTIP_DELAY,"Tooltip"));
			
			m_btnSlide =  m_layoutToolbar.newButton(
					new MyButton.Command()
					{
						public void doCommand(MyButton btn)
						{
							view.changeLayout(PageView.SLIDE);
						}
					},
					"slide.gif", "slide-disabled.gif", 19, 15, 24, false);
			m_btnSlide.addMouseListener(new TooltipListener(SLIDE, TOOLTIP_DELAY, "Tooltip"));
			Image separator4 = new Image("separator.gif");
			separator4.setPixelSize(3,17);
			m_layoutToolbar.addWidget(separator4);

			this.loadToolbarLinksFromDictionary();
			
			//Anchor digital = new Anchor("Дигитална библиотека", "http://www.jabooka.org.rs/digital", "");
			//Anchor jutro = new Anchor("Јутро", "http://www.dobrojutro.co.rs", "_blank");
			
			//m_layoutToolbar.addWidget(digital);
			//m_layoutToolbar.addWidget(jutro);
			m_layoutToolbar.addWidget(m_dummyFocus);
			ret.add(m_toolbar,0,0);
			ret.add(m_layoutToolbar,440,0);
			return ret;
		}
		
		public void onChange(ChangeEvent e) 
		{
		//	if(sender == this.m_qualityList)
		//	{
				int i = m_qualityList.getSelectedIndex();
				view.changeQuality(i);
				m_btnZoomIn.setEnabled(true);
				m_btnZoomOut.setEnabled(true);
				m_qualityList.setFocus(false);
				m_dummyFocus.setFocus(true);
		//	}
		}
		
		public void onPageChange(Widget sender, int index, int scrollTop, String name) 
		{
			m_pageName.setText(name);
			m_pageIndex.setText(view.getCurrentPage()+1+"");
			m_btnBack.setEnabled(view.getCurrentPage() != 0);
			m_btnNext.setEnabled(view.getCurrentPage() != view.getPageCount()-1);
		}
			
		private void HideLoadingMessage()
		{
			Element ucitavanje=DOM.getElementById("ucitavanje");
			Element parent=DOM.getParent(ucitavanje);
			DOM.removeChild(parent, ucitavanje);				
		}
		
		private void createComponents()
		{
			HideLoadingMessage();
			layout.addAbsoluteSizeSlot(createToolbar(), 36, 500, 36);
			
			final AbsolutePanel horizontalBar = new AbsolutePanel();
			horizontalBar.setStyleName("Silver");
			horizontalBar.setWidth("2px");
			layout.addAbsoluteSizeSlot(horizontalBar, 2, 1,1);
			layout.setStyleName("MainLayout");			

			//tabBar.setStyleName("MyVerticalTabBar");
			m_tabBar.setStyleName("Silver");
			m_tabBar.setStyles("MyVerticalTabBar", "MyVerticalTabButton", 
					"MyVerticalTabButtonHover", "MyVerticalTabButtonDown",
					"MyVerticalTabViewContainer");
								
			

			//TODO
//test
			
/*		MyImageList zbirka = new MyImageList();
		MyStackPanel stack = new BrowseView();
			//MyStackPanel stack = new MyStackPanel();
			
			stack.setStyleName("Silver");
			
			AbsolutePanel brisi1 = new AbsolutePanel();
			brisi1.setStyleName("Brisi");

			AbsolutePanel brisi2 = new AbsolutePanel();
			brisi2.setStyleName("Brisi1");
			
			stack.setHeaderImageDimensions(16, 16);
			stack.setHeaderHeight(40);
			
			stack.add(brisi1, "Категорија", new Image("folder.gif"));
			stack.add(zbirka, "Збирка", new Image("collection.gif"));
			stack.add(brisi2, "Књига", new Image("book.gif"));

	
			String urlk1 = "http://localhost/k1.jpg";
			String urlk2 = "http://localhost/k2.jpg";
			for(int i = 0; i<80; i++)
			{
				MyImageList.Pair[] d={new MyImageList.Pair("autor", "mika"), new MyImageList.Pair("izdavac:", "Zika")};
				String[] d2 = {"Дигитална копија комплетног рукописа Студеничког типика који је свети Сава написао 1208.г, по доласку у Србију на молбу брата Стефана. Свети Сава постаје старешина манастира где активно ради на организацији монашког живота у Студеници. Овај црквени устав, тј. правилник о богослужењима и животу монаха у манастирима, Сава пише по узору на Хиландарски типик. Значај Студеничког типика је највише у томе што ће постати главна основа за устав Српске православне цркве. Дигитална копија Студеничког типика добијена је љубазношћу директора Националног музеја у Прагу, господина Михала Лукеша. Оригинал се чува у том музеју под сигнатуром IX H 8 – Š10. Дигитална копија добијена је за потребе израде фототипског издања, које ће НБС објавити у сарадњи са Манастиром Студеницом. Пет примерака фототипског издања биће послато у Национални Музеј у Прагу као узвратни поклон."};
				
				zbirka.addItem(null,"Наслов", d,null);
				zbirka.addItem(urlk1, "Jos jedan naslov", d2,null);
				zbirka.addItem(urlk2, "I jos jedan naslov",d,null);
			}
			zbirka.redraw();*/
			

//end test

			
			//izbacen izbor knjige
			/*			BrowseView stack = new BrowseView();		
						m_tabBar.addTab(stack, "Избор књиге", "find.gif", "find.gif", 18, 21);
						stack.redraw();*/
			
			m_tabBar.addTab(m_thumbView, "Странице", "thumbs1.gif", "thumbs1-disabled.gif", 22, 18);
			
			m_contentsView.setBook(this);
			m_tabBar.addTab(m_contentsView,"Садржај", "content.gif", "content-disabled.gif", 23, 18);
			
			
			VerticalPanel vert = new VerticalPanel();
			//vert.setStyleName("Brisi");
			

			
			vert.add(new Image("citanka.png"));
			
			//dodatao pri objavljivanju na autentik
			String zaglavlje = "Књига коју читате:";
			Dictionary ucitanaKnjiga = Dictionary.getDictionary(BOOK);
			String naslovKnjige = ucitanaKnjiga.get("naslov").trim();
			String autorKnjige = ucitanaKnjiga.get("autor").trim();
			String izdanjeKnjige = ucitanaKnjiga.get("izdanje").trim();
			String invBrKnjige = ucitanaKnjiga.get("invBr").trim();
			String cobiss = ucitanaKnjiga.get("cobiss").trim();
			String brPregledaKnjige = ucitanaKnjiga.get("brPregleda").trim();
			

			String prikaz="";
			if(naslovKnjige.length()>0)
				prikaz += "Наслов: "+naslovKnjige+"\n";
			if(autorKnjige.length() >0)
				prikaz += "Аутор: "+autorKnjige+"\n";
			if(izdanjeKnjige.length()>0)
				prikaz += "Издање: "+izdanjeKnjige+"\n";
			if(invBrKnjige.length() >0)
				prikaz += "Инвентарни број:"+invBrKnjige+"\n";
			if(cobiss.length() >0)
				prikaz += "Cobiss број:"+cobiss+"\n";
			if(brPregledaKnjige.length() >0)
				prikaz += "Kњига је отварана: "+brPregledaKnjige + " пут(а)"+"\n";

			
		    TextArea ta = new TextArea();
		    ta.setStylePrimaryName("infoOKnjizi");
		    ta.setSize("90%", "90%");
		    ta.setCharacterWidth(40);
		    ta.setVisibleLines(20);
		    ta.setReadOnly(true);
		    
		    vert.add(new Label(zaglavlje));
		    ta.setText(prikaz);
		    vert.add(ta);

			//
			vert.add(new Label("Аутор програма: Лазаревић Ивица, email: lazarevic.ivica@gmail.com"));
			vert.add(new Label("Народна билиотека у Јагодини, email: nbjag@ptt.rs"));
			m_tabBar.addTab(vert,"", "help.gif", "help.gif", 20, 20);
			
			view.addPageChangeListener(this);
			MyHorizontalSpliter spliter=new MyHorizontalSpliter(false,m_tabBar ,view,
																/*split position*/m_tabBar.getTabClientWidth(),
																/*min height */50, 
																/*min left width*/m_tabBar.getTabClientWidth(),
																/*min right width*/50);
			m_tabBar.setSpliter(spliter);
			//TODO ne radi dobro ako je visina tabBara veca od minHeight poslednji parametar u addRelative size slot
			layout.addRelativeSizeSlot(spliter, 1, 300,/*mora biti vece od visine tabBar-a*/300);
			spliter.addChangeListener(view);
			spliter.addChangeListener(m_thumbView);
			layout.bringToFrontSlot(0);
			RootPanel.get().add(layout);			
			Window.addResizeHandler(new ResizeHandler()
			{
				public void onResize(ResizeEvent e){
					layout.setPixelSize(e.getWidth(), e.getHeight());							      
				}
			});
		}
					
		public void onError(Request request, Throwable arg1) 
		{
			if(request.equals(m_bookRequest))
			{
				m_bookRequest = null;
				m_requestedBookId = null;
				Window.alert("Грешка, сервер није одговорио на очекивани начин!");
				return;
			}
			
			if(request.equals(m_contentsRequest))
			{
				//TODO uradi
			}
			
		}
//Poziva se kada server vrati meta podatke
		public void onResponseReceived(Request request, Response response) 
		{
			if(request.equals(m_bookRequest))
			{
				loadBook(response);
				return;
			}
			
			if(request.equals(m_contentsRequest))
			{
				loadContents(response);
				return;
			}			
		}
		
		
		private void loadContentsFromDictionary()
		{
			Dictionary metaPodaci = null;
			//ucitavam metapodatke iz dictonary (JS var promenljiva)			
			try
			{
				metaPodaci = Dictionary.getDictionary(CONTENTS_META);
			}
			catch(Exception e)
			{
				return;
			}
			if(metaPodaci != null)
			{
				String jsonString= metaPodaci.get(CONTENTS_META_JSON_STRING).trim();
				loadContents(jsonString); //cak i ako id knjige nije tacan knjiga se ucitava!
			}
		}
		
		private void loadContents(String jsonString)
		{
			try
			{
				JSONValue val = JSONParser.parseStrict(jsonString);
				m_contentsView.load(val.isArray());
			}
			catch(Exception e)
			{
				Window.alert(BAD_DATA_FORMAT);
			}
		}
		
		//asinhrono ucitava sadrzaj pri prvom otvaranju taba m_contentsView
		private void loadContents(Response response)
		{
			if(response.getStatusCode()!=200)
			{
				Window.alert(UNESPECTED_SERVER_RESPONSE);
				return;
			}
			String jsonString=response.getText();
			loadContents(jsonString);			
		}
/**Funkcija dodata pri izmeni za objavljivanje na Autentik.net
 * 		Ucitava metapodatke o knjizi iz dictionary-ja, t.j. iz
 * 		JavaScript var promenljive koja je upisana u glavni html dokument.
 * 		Html dokument bi bilo najbolje generisati nekom skriptom koja bi
 * 		upisala odgovarajuce vrednosti u var promenljivu "knjiga_meta".
*/
		private void loadBookFromDictionary()
		{
			//ucitavam id knjige, mozda bude zatrebalo nekad ako nadogradim prog
			//da moze da se pristupa knjigama iz samog programa!
			String bookId = "0";//ne bi trebalo da postoji knjiga 0 u bazi!
			Dictionary book=Dictionary.getDictionary(BOOK);
			if(book != null)
				bookId = book.get(BOOK_ID).trim();
			//ucitavam metapodatke iz dictonary (JS var promenljiva)
			Dictionary metaPodaci = Dictionary.getDictionary(BOOK_META);
			if(metaPodaci != null)
			{
				String jsonString= metaPodaci.get(BOOK_META_JSON_STRING).trim();
				loadBook(jsonString, bookId); //cak i ako id knjige nije tacan knjiga se ucitava!
			}
		}
		
		private void loadToolbarLinksFromDictionary()
		{
			
			Dictionary linkovi = null;
			try
			{
				linkovi = Dictionary.getDictionary(TOOLBAR_LINKS);
			}
			catch(Exception e)
			{
				return;
			}
			try
			{
				String jsonString = linkovi.get(LINKOVI_JSON_STRING);				
				JSONValue val = JSONParser.parse(jsonString);
				JSONArray ar = val.isArray();
				if(ar != null)
				{
					for(int i=0; i<ar.size();i++)
					{
						JSONValue objVal = ar.get(i);
						JSONObject obj = objVal.isObject();						
						String a = obj.get(TOOLBAR_LINKS_A).isString().stringValue();
						String href = obj.get(TOOLBAR_LINKS_HREF).isString().stringValue();							
						Anchor aLink = new Anchor(a, href, "");							
						m_layoutToolbar.addWidget(aLink);							
					}
				}
			}
			catch(Exception e)
			{
				Window.alert(BAD_DATA_FORMAT +" linkovi:"+ e.getMessage());
			}
		}
		
		private void loadBook(String jsonString, String requestedBookId)
		{
			BookInfo bookInfo=null;
			try
			{
				JSONValue val = JSONParser.parse(jsonString);
				bookInfo=new BookInfo(val.isObject(),requestedBookId);
								
			}
			catch(Exception e)
			{
				Window.alert(BAD_DATA_FORMAT);
				return;
			}
			m_currentBookId = requestedBookId;
			List<Quality> q=bookInfo.getQualityDefinitions();
			
			for(int i = 0; i<q.size(); i++)
			{
				BookInfo.Quality qItem=(BookInfo.Quality)q.get(i);
				String item = qItem.name;
				m_qualityList.addItem(item);
			}			
			m_tabBar.Hide();			
			boolean enableThumb = (bookInfo.getThumbQuality() != null);			
			m_tabBar.enableTab(enableThumb, THUMB_TAB);
			
			boolean enableContents = bookInfo.isContentsSupported();
			m_tabBar.enableTab(enableContents, CONTENTS_TAB);
			
			m_pageCount.setText("/ "+bookInfo.getPageCount());
			
			m_btnBack.setEnabled(true);
			m_btnNext.setEnabled(true);
			m_btnZoomIn.setEnabled(true);
			m_btnZoomOut.setEnabled(true);
			
			m_btnSlide.setEnabled(bookInfo.isSlideSupported());
			m_btnContinous.setEnabled(bookInfo.isContinousSupported());
			
			if(!(bookInfo.getPageCount()>1))
			{
				m_btnBack.setEnabled(false);
				m_btnNext.setEnabled(false);
			}
			
			BookInfo.Quality quality=(BookInfo.Quality)q.get(0);
			int layout = PageView.CONTINOUS;
			m_layoutToolbar.selectButton(m_btnContinous,false);
			if(! bookInfo.isContionusDefaultLayout())
			{
				layout = PageView.SLIDE;
				m_layoutToolbar.selectButton(m_btnSlide,false);
			}
			int maxIndex = bookInfo.getPageCount() - 1;
			int firstPage = this.getFirstPage(maxIndex);
			view.Init(bookInfo, quality, true, firstPage, layout);
						
			if(view.getCurrentPage() == 0)
				m_btnBack.setEnabled(false);
			if(view.getCurrentPage() == bookInfo.getPageCount()-1)
				m_btnNext.setEnabled(false);
			m_thumbView.setMasterView(view);
			m_thumbView.addStyleName("WhiteBackground");
		}
		
		private int getFirstPage(int maxIndex)
		{
			int index = 0;
			Dictionary stranica = null;
			try
			{
				stranica = Dictionary.getDictionary(START_PAGE);
				String strIndex = stranica.get(PAGE_INDEX);
				index = Integer.parseInt(strIndex);
				if(index > maxIndex || index < 0)
					index = 0;
			}
			catch(Exception e)
			{
			}
			return index;
		}
		
		private void loadBook(Response response)
		{
			m_bookRequest = null;
			String requestedBookId = m_requestedBookId;
			m_requestedBookId = null;
			if(response.getStatusCode()!=200)
			{
				Window.alert(UNESPECTED_SERVER_RESPONSE);
				return;
			}
			String jsonString=response.getText();
			loadBook(jsonString, requestedBookId);
		}
		

		
		private void init()
		{
		/*	Dictionary service=Dictionary.getDictionary(SETTINGS);
			if(service != null)
				META_DATA_URL=URL.encode(service.get(SERVICE_URL));				
			Dictionary book=Dictionary.getDictionary(BOOK);
			if(book != null)
			{
				String bookID= book.get(BOOK_ID).trim();
				requestBook(bookID);
			}*/	
			loadBookFromDictionary();
			loadContentsFromDictionary();
		}
		
		public void requestContents()
		{			
			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, META_DATA_URL);
			rb.setHeader("Content-Type","application/x-www-form-urlencoded");
			cancelRequest(m_contentsRequest);
			try
			{
				m_contentsRequest = rb.sendRequest(CONTENTS_ID+"="+m_currentBookId,this);
			}
			catch(RequestException e)
			{
				Window.alert(SERVER_BINDING_ERROR);
			}
		}
		
		private void cancelRequest(Request req)
		{
			if(req != null)
				if(req.isPending())
					req.cancel();
		}
		
		private void cancelAllRequests()
		{
			cancelRequest(m_bookRequest);
			cancelRequest(m_contentsRequest);
		}
		
		public void requestBook(String bookID)
		{
			cancelAllRequests();
			m_requestedBookId = bookID;
			
			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, META_DATA_URL);
			rb.setHeader("Content-Type","application/x-www-form-urlencoded");
			try
			{
				m_bookRequest = rb.sendRequest(BOOK_ID+"="+bookID,this);
			}
			catch(RequestException e)
			{
				m_requestedBookId = null;
				Window.alert(SERVER_BINDING_ERROR);
			}
		}
		
		public void showPage(int index)
		{
			view.showPage(index);
		}
		
		/**
		 *  Cita dogadjaje pre nego sto oni bivaju prosledjeni odgovarajucem hendleru.
		 *  Ako nisu aktivna polja za unos, ili padajuca lista za izbor kvaliteta slike i 
		 *  ako je pritisnut neki od tastera: 
		 *  	PageUp, PageDown, RightArrow, LeftArrow, shift-, shift+, UpArrow, DownArrow
		 *  onda obradjuje te dogadjaje (Sledeca stranica, prethodna, zoomIn, zoomOut, skrol gore, skrol dole)
		 */
		private boolean m_shift = false;		
		private void registerEventsPreview()
		{
			final Knjiga self = this;

			Event.addNativePreviewHandler(new NativePreviewHandler(){
				@Override
				public void onPreviewNativeEvent(NativePreviewEvent e) 
				{					
					NativeEvent ne = e.getNativeEvent();				
					Element elt = ne.getEventTarget().cast();//DOM element objekta za koji je generisan event.
					if( elt == self.m_qualityList.getElement())
						return;					
					boolean aktivnaPoljaZaUnos = (elt == self.m_pageIndex.getElement() || elt == self.m_pageName.getElement());
						
					if(e.getTypeInt() == Event.ONKEYDOWN)
					{								
						int kod = ne.getKeyCode();
						switch(kod)
						{					
							case KeyCodes.KEY_PAGEDOWN://napred						
								view.showPage(view.getCurrentPage()+1);
								break;
							case KeyCodes.KEY_RIGHT://napred
								if( ! aktivnaPoljaZaUnos)
									view.showPage(view.getCurrentPage()+1);
								break;						
							case KeyCodes.KEY_PAGEUP://nazad					
								view.showPage(view.getCurrentPage()-1);
								break;	
							case KeyCodes.KEY_LEFT://nazad
								if( ! aktivnaPoljaZaUnos)
									view.showPage(view.getCurrentPage()-1);
								break;	
							case KeyCodes.KEY_DOWN:					
								if( ! aktivnaPoljaZaUnos )
									view.setVerticalScrollPosition(view.getVerticalScrollPosition()+20);
								break;
							case KeyCodes.KEY_UP:
								if( ! aktivnaPoljaZaUnos )
									view.setVerticalScrollPosition(view.getVerticalScrollPosition()-20);
								break;						
						}
									
						if(ne.getShiftKey())
							m_shift = true;				
						if(kod == 109 && m_shift)// kod za - je 109
							view.zoomOut();
						else if(kod == 107 && m_shift)//+
							view.zoomIn();
					}
					
					if(e.getTypeInt() == Event.ONKEYUP)
					{
						m_shift = ne.getShiftKey();
					}									
				}});			
		}
		
		public void onModuleLoad()
		{							
			registerEventsPreview();
			createComponents();
			init();
											
			//RootPanel.get().add(new HTML("<p>Ydravo svete</p>"));
			//testZbirka();
		}						
}

