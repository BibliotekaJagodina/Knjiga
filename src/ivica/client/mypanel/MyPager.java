package ivica.client.mypanel;
import com.google.gwt.user.client.ui.*;

		/**
		 * 
		 * @author ivica.lazarevic
		 * prikazuje < 1 2 3 4 ... >
		 * 1,2,3,4... su brojevi stranica
		 *
		 * |               canvas               |
		 * |left| |      hider        |   |rigth| 			hider odseca slider
		 * |			slider					|  		     slider sadrzi brojeve strana 1 2 3 ...
		 */
		public class MyPager extends Composite implements ClickListener
		{
			public static interface PageChangeListener
			{
				public void onPageChanged(int pageNum, int itemsPerPage);
			}
			private final static int SPACEING = 3;
			private final static int HEIGHT = 22;
			private final static int WIDTH = 80;
			private PageChangeListener m_listener = null;
			private int m_itemCount = 0;
			private int m_itemsPerPage = 0;
			private int m_currentPage = 0;
			private boolean m_inited = false;
			
			private HorizontalPanel m_canvas = new HorizontalPanel();
			
			private MyButton m_back = new MyButton(false, "Pager-button", "Pager-button-hover", "Pager-button-down", 15,15);			
			private MyButton m_forward = new MyButton(false, "Pager-button", "Pager-button-hover", "Pager-button-down", 15,15);

			private AbsolutePanel m_hider= new AbsolutePanel(); 
			private HorizontalPanel m_slider = new HorizontalPanel();
			private Label[] m_labels = null;
			public MyPager()
			{
				initWidget(m_canvas);
				m_hider.setStyleName("Pager-hider");
				m_back.setImageUrl("pager-back.gif");
				m_back.setDisabledImageUrl("pager-back-disabled.gif");
				m_back.setClickListener(new ClickListener()
				{
					public void onClick(Widget sender)
					{
						Label lbl = getFirstVisible();
						int labelLeft = m_hider.getOffsetWidth() - (lbl.getOffsetWidth() + SPACEING);
						setLabelLeft(lbl, labelLeft);
						checkButtons();
					}
				});

				m_forward.setImageUrl("pager-forward.gif");
				m_forward.setDisabledImageUrl("pager-forward-disabled.gif");
				m_forward.setClickListener(new ClickListener()
				{
					public void onClick(Widget sender)
					{
						Label right = getLastVisible();
						setLabelLeft(right, 0);
						checkButtons();
					}
				});		
				
				m_hider.setWidth(WIDTH +"px");
				
				m_forward.setPixelSize(HEIGHT, HEIGHT);
				m_back.setPixelSize(HEIGHT, HEIGHT );
				m_canvas.add(m_back);			
				m_hider.setHeight( HEIGHT + "px");
				m_canvas.add(m_hider);
				m_canvas.add(m_forward);
				m_slider.setSpacing(SPACEING);
				
				m_hider.add(m_slider, 0, 0);				
			}
			
			private void checkButtons()
			{
				if(m_slider.getOffsetWidth() < m_hider.getOffsetWidth())
				{
					m_back.setEnabled(false);
					m_forward.setEnabled(false);
					return;
				}				
				
				int sliderLeft = m_hider.getWidgetLeft(m_slider);
				int sliderRight = sliderLeft + getSliderWidth();
				
				int hiderRight = m_hider.getOffsetWidth();
				
				if(sliderLeft == 0)
					m_back.setEnabled(false);
				else
					m_back.setEnabled(true);
				
				if(sliderRight <= hiderRight)
					m_forward.setEnabled(false);
				else
					m_forward.setEnabled(true);				
			}
			
			//relativno u odnosu na hider panel
			private int getLabelRight(Label lbl)
			{
				int width = 0;
				for(int i=0; i<m_labels.length; i++)
				{
					width += SPACEING + m_labels[i].getOffsetWidth();
					if(m_labels[i]==lbl)
						break;
				}
				return m_hider.getWidgetLeft(m_slider) + width;
			}
			
			private int getLabelLeft(Label lbl)
			{
				return getLabelRight(lbl) - (lbl.getOffsetWidth() +  SPACEING);
			}
			
			//relativno u odnosu na hider panel
			private void setLabelLeft(Label lbl, int newLabelLeft)
			{
				//ako je sirina slidera manja od sirine hidera
				//onda nema potrebe za skrolovanjem !!!
				int sliderWidth = getSliderWidth();								
				if(sliderWidth <= m_hider.getOffsetWidth())
					return;				
				int sliderLeft = m_hider.getWidgetLeft(m_slider);
				int oldLabelLeft = getLabelLeft(lbl);
				int delta = newLabelLeft - oldLabelLeft;
								
				int newSliderLeft = sliderLeft + delta; 
				
				//proveravam da li je nova pozicija izvan granica
				//da li je ispred leve
				if(newSliderLeft > 0)
					newSliderLeft = 0;
				
				//da li je iza desne
				int sliderRight = newSliderLeft + sliderWidth;
				int hiderRight = m_hider.getOffsetWidth();
				if(sliderRight < hiderRight)
					newSliderLeft = hiderRight - sliderWidth;				
				m_hider.setWidgetPosition(m_slider, newSliderLeft, 0);				
			}
			
			private int getSliderWidth()
			{
				return m_slider.getOffsetWidth();
			}
						
			private Label getFirstVisible()
			{
				//trazi se prvi ciji je right veci od 0
				int labelLeft = m_hider.getWidgetLeft(m_slider);
				for(int i = 0; i<m_labels.length; i++)
				{
					int labelRight = labelLeft + m_labels[i].getOffsetWidth() + SPACEING;
					labelLeft = labelRight;
					if(labelRight >=0)
						return m_labels[i];
				}
				return null;
			}
			
			private Label getLastVisible()
			{
				//proveri za svaki da li mu se koordinate nalaze u u okviru vidljivog dela hider -a
				Label lastLabel = m_labels[m_labels.length -1];
				
				//trazi se prvi ciji je left manji od hider.right
				int labelRight = getLabelRight(lastLabel);
				int hiderRight = m_hider.getOffsetWidth();
				for(int i = m_labels.length-1; i>=0; i--)
				{
					int labelLeft = labelRight - (m_labels[i].getOffsetWidth()+ SPACEING);
					labelRight = labelLeft;
					if(labelLeft <= hiderRight)
					{
						if(i > 0)
							return m_labels[i-1];
						else
							return m_labels[i];
					}
				}
				return null;
			}
			
			public void setItemCount(int count)
			{
				m_itemCount = count;
				//redraw();
			}
			
			public void setItemsPerPage(int itemsPerPage)
			{
				m_itemsPerPage = itemsPerPage;
				//redraw();
			}
			
			public void setPageChangeListener( PageChangeListener listener)
			{
				m_listener = listener;
			}
			
			private void deselect()
			{
				if(m_currentPage != -1)
				{
					setStyle(m_currentPage);
					m_currentPage = -1;
				}
			}
			
			/**
			 * 
			 * 
			 * deselektuje staru stranu.
			 * Postavlja m_current page na vrednost index.
			 * Postavlja stil selektovanoj labeli.
			 * Obavestava listenere.
			 */
			private void select(int index)
			{
				deselect();
				m_currentPage = index;
				setSelectedStyle(m_currentPage);
				if(m_listener != null)
					m_listener.onPageChanged(m_currentPage, m_itemsPerPage);
			}			
			
			/**
			 *  @return zero-based page index
			 *  -1 ako nije selektovana ni jedna stranica
			 */
			public int getCurrentPageIndex()
			{
				return m_currentPage;
			}
			
			public int getPageCount()
			{
				return (int) Math.ceil((double) m_itemCount / (double) m_itemsPerPage);
			}
			
			/**
			 * postavlja se stil za neselektovanu labelu sa indeksom labelIndex
			 * @param labelIndex
			 */
			private void setStyle(int labelIndex)
			{
				Label lbl = m_labels[labelIndex];
				if( (labelIndex+1) % 2 == 0)
					lbl.setStyleName("Pager-item-even");
				else
					lbl.setStyleName("Pager-item-odd");
			}
			
			private void setSelectedStyle(int labelIndex)
			{
				Label lbl = m_labels[labelIndex];
				lbl.setStyleName("Pager-item-selected");
			}
			
			public void redraw()
			{
				m_slider.clear();
				
				if(m_itemCount == 0 || m_itemsPerPage == 0)
					return;
				int pageCount = getPageCount();
				m_labels = new Label[pageCount];
				for(int i=0; i < pageCount; i++)
				{
					Label lbl = new Label(i+1+"");
					m_labels[i] = lbl;
					lbl.addClickListener(this);
					lbl.setWordWrap(false);
					setStyle(i);					
					m_slider.add(lbl);
				}
				int top = 0;
				if(m_hider.getOffsetHeight() > m_slider.getOffsetHeight())
					top = (m_hider.getOffsetHeight()- m_slider.getOffsetHeight()) / 2;
				m_hider.setWidgetPosition(m_slider, 0, top);
				setSelectedStyle(m_currentPage);
				if(! m_inited)
				{
					m_inited = true;
					select(0);
				}
			}
			
			public void onLoad()
			{
				super.onLoad();
				checkButtons();
			}
			
			private int getLabelIndex(Label lab)
			{
				for(int i=0; i<m_labels.length;i++)
				{
					if(m_labels[i] == lab)
						return i;
				}
				return -1;
			}
			
			//poziva se kad je kliknuto na labelu
			public void onClick(Widget sender)
			{				
				int pageIndex = getLabelIndex((Label)sender);
				if(m_currentPage != pageIndex)
					select(pageIndex);
			}
			
		}