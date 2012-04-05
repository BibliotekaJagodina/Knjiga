package ivica.client.page.pagecomponent;

import com.google.gwt.user.client.ui.*;;

public class InfoPage extends Composite 
{
	private Label m_label;
	private AbsolutePanel m_canvas;
	private boolean m_showInfo = true;
	public InfoPage()
	{
		m_canvas = new AbsolutePanel();
		m_label=new Label();
		m_label.setWordWrap(true);
		m_label.setText("");
		m_canvas.add(m_label, 0, 0);

		initWidget(m_canvas);
		setStyleName("PageComponent");
		addStyleName("WhiteBackground");
	}
	
	public void ShowInfoText(boolean show)
	{
		m_showInfo = show;
	}
	
	public void setDataOnce(String text)
	{
		if(!m_showInfo)
			text = "";
		if(m_label.getText().equals(""))
			m_label.setText(text);
	}
	public void setData(String text)
	{
		if(!m_showInfo)
			text = "";
		m_label.setText(text);
	}	
}