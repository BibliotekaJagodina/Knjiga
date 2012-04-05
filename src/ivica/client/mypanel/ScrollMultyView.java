package ivica.client.mypanel;

import com.google.gwt.user.client.ui.*;
public class ScrollMultyView extends MultyView
{
	private ScrollPanel m_canvas;
	
	protected void attachWidget(Widget w) 
	{
		m_canvas.setWidget(w);
	}


	public ScrollMultyView()
	{
		m_canvas=new ScrollPanel();
		m_canvas.setStylePrimaryName("WhiteBackground");
		initWidget(m_canvas);
	}
	


}
