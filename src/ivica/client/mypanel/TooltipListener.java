package ivica.client.mypanel;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class TooltipListener extends MouseListenerAdapter {
  private static final String DEFAULT_TOOLTIP_STYLE = "TooltipPopup";
  protected static final int DEFAULT_OFFSET_X = 10;
  protected static final int DEFAULT_OFFSET_Y = 35;


  protected static class Tooltip extends PopupPanel {
    private int delay;

    public Tooltip(Widget sender, int offsetX, int offsetY, 
        final String text, final int delay, final String styleName) {
      super(true);

      this.delay = delay;

      HTML contents = new HTML(text);
      add(contents);

      int left = sender.getAbsoluteLeft() + offsetX;
      int top = sender.getAbsoluteTop() + offsetY;

      setPopupPosition(left, top);
      setStyleName(styleName);
    }

    public void show() {
      super.show();

      	Timer t = new Timer() {

        public void run() {
          Tooltip.this.hide();
        }

      };
      t.schedule(delay);
    }
  }

  protected Tooltip tooltip;
  protected String text;
  protected String styleName;
  protected int delay;
  protected int offsetX = DEFAULT_OFFSET_X;
  protected int offsetY = DEFAULT_OFFSET_Y;

  public TooltipListener(String text, int delay) {
    this(text, delay, DEFAULT_TOOLTIP_STYLE);
  }

  public TooltipListener(String text, int delay, String styleName) {
    this.text = text;
    this.delay = delay;
    this.styleName = styleName;
  }

  public void onMouseEnter(Widget sender) {
    if (tooltip != null) {
      tooltip.hide();
    }
    tooltip = new Tooltip(sender, offsetX, offsetY, text, delay, styleName);
    tooltip.show();
  }

  public void onMouseLeave(Widget sender) {
    if (tooltip != null) {
      tooltip.hide();
    }
  }

  public String getStyleName() {
    return styleName;
  }

  public void setStyleName(String styleName) {
    this.styleName = styleName;
  }

  public int getOffsetX() {
    return offsetX;
  }

  public void setOffsetX(int offsetX) {
    this.offsetX = offsetX;
  }

  public int getOffsetY() {
    return offsetY;
  }

  public void setOffsetY(int offsetY) {
    this.offsetY = offsetY;
  }
} 
