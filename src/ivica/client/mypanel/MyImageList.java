package ivica.client.mypanel;

/*import ivica.client.mypanel.MyPanelLayout;
import ivica.client.mypanel.MyVerticalLayout;*/

import com.google.gwt.user.client.ui.*;
import java.util.*;

/**
 * 
 * @author ivica.lazarevic
 *
 *Prikazuje tabelu sa dve kolone. U prvoj koloni se nalazi slika a u drugoj tekst Naslov u jednom stilu
 *i ispod naslova proizvoljan broj redova teksta u drugom stilu.
 *
 */
public class MyImageList extends Composite implements TableListener, MyPager.PageChangeListener
{		
		private static final int ITEMS_PER_PAGE = 10; 		
		private MyImageListClickListener m_listener = null;
		private int m_selectedItem = -1;
		private FlexTable m_table = new FlexTable();
		private ScrollPanel m_scroll = new ScrollPanel();
		private VerticalPanel m_vertical = new VerticalPanel();
		private HorizontalPanel m_header = new HorizontalPanel();
		private ArrayList m_items = new ArrayList(); 
		private MyPager m_pager = new MyPager();
		
		private AbsolutePanel m_canvas = new AbsolutePanel();
		
		public void onCellClicked(SourcesTableEvents sender, int row, int cell) 
		{
			if(m_selectedItem != -1)
				deselect(m_selectedItem);
			m_selectedItem = row;
			m_table.getRowFormatter().setStyleName(row, "ImageList-selected-item");
			if(m_listener != null)
				m_listener.onMyImageListClick(this);
		}

		public void setClickListener(MyImageListClickListener listener)
		{
			this.m_listener = listener;
		}
		
		public MyImageList()
		{
			initWidget(m_canvas);
			
			m_header.add(m_pager);
//			m_vertical.setSpacing(0);
			m_vertical.setStyleName("ImageList-header");
			m_header.setStyleName("ImageList-header");
			m_pager.setStyleName("ImageList-header");
			m_canvas.add(m_header,0,0);
			m_header.setHeight("18px");
			m_scroll.setWidget(m_table);
			m_scroll.setAlwaysShowScrollBars(false);
			m_canvas.add(m_scroll, 0, 18);
			
			m_table.addTableListener(this);
			m_table.setStyleName("ImageList");
			m_table.setCellPadding(3);
			m_table.setCellSpacing(0);
//			m_canvas.setStyleName("Brisi");
		}
		
		public void onLoad()
		{
			m_pager.setPageChangeListener(this);
		}
		
		private void deselect(int row)
		{
			setRowStyle(row);
		}
		
		public Object getSelectedItemData()
		{
			return m_items.get(m_selectedItem);
		}
		
		private void setRowStyle(int row)
		{
			if( row % 2 == 0)
				m_table.getRowFormatter().setStyleName(row, "ImageList-item");
			else 
				m_table.getRowFormatter().setStyleName(row, "ImageList-item1");			
		}
		
		public void addItem(String thumbUrl, String title, String[] descriptions, Object data)
		{
			Pair[] pairs = new Pair[descriptions.length];
			for(int i=0; i<descriptions.length; i++)
				pairs[i] = new Pair(null, descriptions[i]);
			addItem(thumbUrl, title, pairs, data);						
		}
		
		public void addItem(String thumbUrl, String title, Pair[] descriptions, Object data) 
		{
			MyImageListItem item = new MyImageListItem();
			item.thumbUrl = thumbUrl;
			if(title == null)
				title = "?";
			if(title.trim().equals(""))
				title = "?";
			item.title = title;
			item.descriptions = descriptions;
			item.data = data;
			m_items.add(item);
		}
		
		//TODO
		public void clearContents()
		{
			m_items.clear();
			if(m_pager != null)
			{
				//m_canvas.remove(m_pager);
				m_table.removeRow(0);  //TODO
				m_pager = null;
			}
			clearDisplay();
		}
		
		//TODO proveri da li se pager nalazi u tabeli ili u okviru vertical panela!
		private void clearDisplay()
		{
			m_table.clear();
		}
		
		
		private void createPager()
		{
			//ako ne postoji pager i ako je broj stavki za prikazivanje veci od definisane vrednosti, onda kreiraj pager
			
			if(m_pager == null)
			{
				if(m_items.size() > ITEMS_PER_PAGE) //ima vise stavki nego sto moze stati na jednu stranu
				{
					m_pager = new MyPager();
					m_pager.setPageChangeListener(this);
					//m_pager.setPixelSize(200, 18);
					m_pager.setItemCount(m_items.size());
					m_pager.setItemsPerPage(ITEMS_PER_PAGE);
					m_vertical.insert(m_pager, 0);								
				}
			}
			else
			{
				m_pager.setItemCount(m_items.size());
				m_pager.setItemsPerPage(ITEMS_PER_PAGE);				
			}
		}
		
		
		/**
		 * 
		 *  funkcija mora biti pozvana da bi sadrzaj bio prikazan.
		 *  
		 */
		public void redraw()
		{
			//ocisti predhodni prikaz
			clearDisplay();
			//ako je potrebno kreiraj pager 
			createPager();	
			
			//popuni tabelu podacima
			int currentRow = 0; //m_table.getRowCount();
			int itemCount = m_items.size();
			int i = 0; //i je indeks prvog elementa koji treba prikazati
			//odredjujem indeks prve stavke stranice koju treba prikazati
			int pageSet = 1;
			if(m_pager != null)
			{
				i = m_pager.getCurrentPageIndex()  * ITEMS_PER_PAGE;
				pageSet = m_pager.getCurrentPageIndex() + 1;
			}
			for( /*skip*/; (i < (ITEMS_PER_PAGE * pageSet)) && (i < itemCount); i++)
			{		
				MyImageListItem item = (MyImageListItem)m_items.get(i);
				if(item.thumbUrl != null)
				{
					Image img = new Image(item.thumbUrl );
					m_table.setWidget(currentRow, 0, img);
				}
				MyLabel desc = new MyLabel();
				desc.setTitle(item.title);
				if(item.descriptions != null)
					desc.setDescriptions(item.descriptions);		
				m_table.setWidget(currentRow,1,desc);
				//m_table.getColumnFormatter().setWidth(1, "100%");				
				setRowStyle(currentRow);
				currentRow++;
			}
			m_pager.redraw();
		}
		
		public static interface MyImageListClickListener
		{
			public void onMyImageListClick(Widget sender);
		}
		
	/********************************************************************************************************
	 * 
	 * @author ivica.lazarevic
	 *Klasa MyLabel prikazuje vertikalnu listu redova, stil prvog reda, reda title, se razlikuje od stilova ostalih redova
	 *
	 *********************************************************************************************************/
		public static class Pair
		{
			public Pair(String key, String value)
			{
				this.key = key;
				this.value = value;
			}
			public String key = null;
			public String value = null;
		}
		
		protected class MyLabel extends Composite
		{			
			private VerticalPanel m_vertical = new VerticalPanel();
			private Label m_title = new Label();
			public MyLabel()
			{
				initWidget(m_vertical);
				this.setStyleName("pointerCursor");
				m_title.addStyleName("ImageList-title");
				m_vertical.add(m_title);			
			}
			
			public void setTitle(String title)
			{
				m_title.setText(title);
			}
			
			public void setDescriptions(Pair[] descriptions)
			{
				for(int i=0; i<descriptions.length; i++)
				{
					Label line = new Label();
					if(descriptions[i].key != null)
						line.setText(descriptions[i].key + ":" + descriptions[i].value);
					else
						line.setText(descriptions[i].value);
					line.addStyleName("ImageList-description");
					m_vertical.add(line);					
				}
			}			
		}
		
		
		public void setPixelSize(int width, int height)
		{
			m_canvas.setPixelSize(width, height);
			m_scroll.setPixelSize(width, height - m_header.getOffsetHeight());
			m_canvas.setWidgetPosition(m_scroll, 0, m_header.getOffsetHeight());
		}
		
		public void setSize(String width, String height)
		{
			m_canvas.setSize(width,height);
			int iHeight = ivica.client.helper.Helper.getPixelSize(height);
			m_scroll.setSize(width, (iHeight - m_header.getOffsetHeight()) + "px");

		}
		
		public void setWidth(String width)
		{
			m_canvas.setWidth(width);
			m_scroll.setWidth(width);
		}
		
		public void setHeight(String height)
		{
			m_canvas.setHeight(height);
			int iHeight = ivica.client.helper.Helper.getPixelSize(height);
			m_scroll.setHeight( (iHeight - m_header.getOffsetHeight()) + "px");
		}
		
		public void onPageChanged(int pageNum, int itemsPerPage)
		{
			redraw();
		}
		
	/*********************************************************************************************************/
		
		public static class MyImageListItem
		{
			public String thumbUrl = null;
			public String title =null;
			public Pair[] descriptions = null;
			public Object data = null;
		}
	
	/************************************************************************************************************/
}
