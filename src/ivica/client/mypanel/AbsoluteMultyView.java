package ivica.client.mypanel;

import com.google.gwt.user.client.ui.*;

public class AbsoluteMultyView extends MultyView {

	private AbsolutePanel m_canvas;
	
	public AbsoluteMultyView()
	{
		m_canvas = new AbsolutePanel();
		initWidget(m_canvas);
	}

	protected void attachWidget(Widget w)
	{
		m_canvas.add(w);
		m_canvas.setWidgetPosition(w,0,0);
	}


}
