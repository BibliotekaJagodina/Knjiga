package ivica.client.page;

//import ivica.client.BookInfo;
import ivica.client.mypanel.MultyShowListener;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.json.client.*;
//import com.google.gwt.core.client.GWT;
import ivica.client.Knjiga;
public class ContentstView extends Composite implements MultyShowListener, TreeListener
{
	public static final String PAGE_INDEX = "i";
	public static final String CONTENTS_TEXT = "txt";
	public static final String SUB_ITEM = "sub";
	private Tree m_tree;
	private Knjiga m_book = null;
	private Widget m_selectedWidget = null;
	boolean m_requested = false;
//	private ScrollPanel m_scroll = new ScrollPanel();
	
	 interface MyTreeImages extends TreeImages
	 {
	    
	    /**
	     * @gwt.resource sadrzaj_malo.gif
	     */
	    AbstractImagePrototype treeOpen();
	    
	    /** 
	     * @gwt.resource sadrzaj_malo.gif
	     */
	    AbstractImagePrototype treeClosed();
	}
	 
	protected class MyItemWidget extends Composite
	{
		private HorizontalPanel m_canvas = new HorizontalPanel();
		private int m_pageIndex = -1;
		private Label m_label = new Label();
		public MyItemWidget(String text, int pageIndex)
		{
			m_pageIndex = pageIndex;
			m_label.setText(text);
			Image img = new Image("sadrzaj_malo.gif");
			img.setPixelSize(12, 16);
			m_canvas.add(img);
			Label l = new Label();
			l.setPixelSize(6, 1);
			m_canvas.add(l);
			m_canvas.add(m_label);
			initWidget(m_canvas);
		}
		public int getPageIndex()
		{
			return m_pageIndex;
		}
	}
	
	public ContentstView()
	{
		//m_scroll.setWidget(m_tree);
		//TreeImages images = (TreeImages)GWT.create(MyTreeImages.class);		
		m_tree = new Tree();
		m_tree.addTreeListener(this);
		initWidget(m_tree);		
	}
	
	public void setBook(Knjiga book)
	{
		m_book = book;
	}
	
	public void onHide() 
	{
		
	}

	public void onShowView() 
	{
		//TODO prikazati poruku ucitavanje u toku..
/*		if(! m_requested)
		{
			m_requested = true;
			m_book.requestContents();
		}*/
	}
	
	public void clear()
	{
		m_requested = false;
		m_tree.clear();
	}
	
	public void load(JSONArray ar)
	{
		m_requested = true;//postavljam na true da ne bi trazio od servisa sadrzaj u funkciji onShowView()
		//TODO ukloniti poruku:  ucitavanje u toku...
		if(ar == null)
			return;
		load(ar,null);
	}
	
	/**
	 * @param ar
	 * @param parent ako je null onda se elementi iz niza dodaju na root 
	 */	
	private void load(JSONArray ar, TreeItem parent)
	{
		
		int size = ar.size();
		for(int i = 0; i < size; i++)
		{
			JSONObject obj = ar.get(i).isObject();
			int pageIndex = (int)obj.get(PAGE_INDEX).isNumber().getValue();
			String text = obj.get(CONTENTS_TEXT).isString().stringValue();
			TreeItem newItem = new TreeItem();
			
			//newItem.setStyleName("MyTreeItem");
			
			newItem.setWidget(new MyItemWidget(text, pageIndex));
			
			//newItem.setText(text);
			//			newItem.m_pageIndex = pageIndex;
			if(parent == null)
				m_tree.addItem(newItem);
			else
				parent.addItem(newItem);
			
			JSONValue subItemVal = obj.get(SUB_ITEM);
			if(subItemVal != null)
			{
				JSONArray subItemArray = subItemVal.isArray();
				load(subItemArray, newItem);
			}			
		}
	}
	public void onTreeItemSelected(TreeItem item)
	{
		MyItemWidget widget = (MyItemWidget) item.getWidget();		
		if(m_selectedWidget != widget)
		{
			widget.setStyleName("gwt-TreeItem-selected");
			if(m_selectedWidget != null)
				m_selectedWidget.setStyleName("gwt-TreeItem");
			m_selectedWidget = widget;			
		}
		else //znaci da je kliknuto na selektovani
		{
			item.setState(!item.getState());
		}
		m_book.showPage(widget.m_pageIndex);
	}

	public void onTreeItemStateChanged(TreeItem arg0) {
		// TODO Auto-generated method stub
		
	}
}
