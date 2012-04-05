package ivica.client.page.pagecomponent;

import com.google.gwt.user.client.ui.*;
import ivica.client.helper.*;
public class Message extends Composite 
{

	public static String  ERR_MSG="    Грешка при учитавању странице!";
	public static String LOADING_MSG="    У току је учитавање странице...";

	private AbsolutePanel m_canvas=new AbsolutePanel();
	
	private Label m_lblUp = new Label();
	private Label m_lblDown = new Label();
	private Widget m_widget=null;
	public Message()
	{
		initWidget(m_canvas);
		m_canvas.add(m_lblUp);
		m_canvas.add(m_lblDown);
		m_canvas.setWidgetPosition(m_lblUp,0, 0);
		setDownLabelPosition();
		setStyleName("Message");
	}
	
	
	private void centerWidget()
	{
		if(m_widget == null)
			return;
		int left=Helper.getCentredLeft(m_widget.getOffsetWidth(), m_canvas);
		int top=Helper.getCentredTop(m_widget.getOffsetHeight(), m_canvas);
		m_canvas.setWidgetPosition(m_widget, left, top);
	}
	public void setCentredWidget(Widget w)
	{
		m_widget=w;
		m_canvas.add(w);
		centerWidget();
	}
	private void setDownLabelPosition()
	{
		int height=m_canvas.getOffsetHeight();
		int top=height-m_lblDown.getOffsetHeight();
		m_canvas.setWidgetPosition(m_lblDown,0, top);
		
		if(m_lblUp.getOffsetHeight() + m_lblDown.getOffsetHeight() > m_canvas.getOffsetHeight())
			m_lblDown.setVisible(false);
		else
			m_lblDown.setVisible(true);
	}
	public void setPixelSize(int width, int height)
	{
		super.setPixelSize(width, height);
		setDownLabelPosition();
		centerWidget();
	}
	
	public void setHeight(String height)
	{
		super.setHeight(height);
		setDownLabelPosition();
		centerWidget();
	}

	public boolean isSizeSetOnLoad() {
		return false;
	}

	public void setData(String text) {
		m_lblUp.setText(text);
		m_lblDown.setText(text);
		this.setDownLabelPosition();
	}
}
