package ivica.client.page;
import com.google.gwt.user.client.ui.*;

public interface LayoutService
{
/*	int getDefaultPageHeight();
	int getDefaultPageWidth();
	int getPageCount();*/
	int getScrollPosition();
	int getHorizontalScrollPosition();
	int getOffsetHeight(); //visina viewporta
//	Page getPage(int i);
	void onPageChanged(int currentPage,int scrollTop);
	void setScrollPosition(int pageTop);
	void setHorizontalScrollPosition(int left);
	void setView(Widget view); //da bi postavio canvas iz layout-a
	void onPageClicked(int index, Widget w);
	public int getClientHeight();
	public int getClientWidth();
}
