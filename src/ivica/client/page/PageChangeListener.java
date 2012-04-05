package ivica.client.page;
import com.google.gwt.user.client.ui.*;
public interface PageChangeListener 
{
	public void onPageChange(Widget sender, int index, int scrollTop, String name);
}
