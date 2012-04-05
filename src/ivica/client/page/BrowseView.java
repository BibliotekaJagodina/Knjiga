package ivica.client.page;
import ivica.client.mypanel.*;

import com.google.gwt.user.client.ui.*;
public class BrowseView extends MyStackPanel
{
	//MyVerticalLayout m_canvas = new MyVerticalLayout(false);
	private MyImageList m_categorie = new MyImageList();
	private MyImageList m_collection = new MyImageList();
	private MyImageList m_book = new MyImageList();

	private boolean m_loaded = false;
	
/*	public BrowseView()
	{
		String urlk1 = "http://localhost/k1.jpg";
		String urlk2 = "http://localhost/k2.jpg";
		for(int i = 0; i<80; i++)
		{
			MyImageList.Pair[] d={new MyImageList.Pair("autor", "mika"), new MyImageList.Pair("izdavac:", "Zika")};
			String[] d2 = {"Дигитална копија комплетног рукописа Студеничког типика који је свети Сава написао 1208.г, по доласку у Србију на молбу брата Стефана. Свети Сава постаје старешина манастира где активно ради на организацији монашког живота у Студеници. Овај црквени устав, тј. правилник о богослужењима и животу монаха у манастирима, Сава пише по узору на Хиландарски типик. Значај Студеничког типика је највише у томе што ће постати главна основа за устав Српске православне цркве. Дигитална копија Студеничког типика добијена је љубазношћу директора Националног музеја у Прагу, господина Михала Лукеша. Оригинал се чува у том музеју под сигнатуром IX H 8 – Š10. Дигитална копија добијена је за потребе израде фототипског издања, које ће НБС објавити у сарадњи са Манастиром Студеницом. Пет примерака фототипског издања биће послато у Национални Музеј у Прагу као узвратни поклон."};
			
			m_collection.addItem(/null,"Наслов", d,null);
			m_collection.addItem(urlk1, "Jos jedan naslov", d2,null);
			m_collection.addItem(urlk2, "I jos jedan naslov",d,null);
		}
		m_categorie.redraw();
		m_collection.redraw();

		add(m_categorie, "Категорија", new Image("folder.gif"));
		add(m_collection, "Збирка", new Image("collection.gif"));
		add(m_book, "Књига", new Image("book.gif"));			

	}*/
	
	public void redraw()
	{
		setHeaderImageDimensions(16, 16);
		setHeaderHeight(40);
		
		add(m_categorie, "Категорија", new Image("folder.gif"), null);
		add(m_collection, "Збирка", new Image("collection.gif"), null);
		add(m_book, "Књига", new Image("book.gif"), null);
		
		String urlk1 = "http://localhost/k1.jpg";
		String urlk2 = "http://localhost/k2.jpg";
		
		int br =0;
		
		for(int i = 0; i<80; i++)
		{
			MyImageList.Pair[] d={new MyImageList.Pair("autor", "mika"), new MyImageList.Pair("izdavac:", "Zika")};
			String[] d2 = {"Дигитална копија комплетног рукописа Студеничког типика који је свети Сава написао 1208.г, по доласку у Србију на молбу брата Стефана. Свети Сава постаје старешина манастира где активно ради на организацији монашког живота у Студеници. Овај црквени устав, тј. правилник о богослужењима и животу монаха у манастирима, Сава пише по узору на Хиландарски типик. Значај Студеничког типика је највише у томе што ће постати главна основа за устав Српске православне цркве. Дигитална копија Студеничког типика добијена је љубазношћу директора Националног музеја у Прагу, господина Михала Лукеша. Оригинал се чува у том музеју под сигнатуром IX H 8 – Š10. Дигитална копија добијена је за потребе израде фототипског издања, које ће НБС објавити у сарадњи са Манастиром Студеницом. Пет примерака фототипског издања биће послато у Национални Музеј у Прагу као узвратни поклон."};
			
			m_collection.addItem(/*img url*/null, ++br + " Наслов", d,null);
			m_collection.addItem(urlk1, ++br + " Jos jedan naslov", d2,null);
			m_collection.addItem(urlk2, ++br + " I jos jedan naslov",d,null);
		}
		m_collection.redraw();

		openStack(0);
	}	
	
}
