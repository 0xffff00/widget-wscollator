package hzk.widgets.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

/**
 * 
 * @author HZK
 * @createTime 2013-8-2
 */
public class JDraggablePane extends JPanel {

	private static final long serialVersionUID = 2368132020130808L;
	private int dragMode;
	// allow drag a border upside down to the other paralleled one
	private boolean allowDragOver;
	// zone types
	private static final int ZONE_N = 2, ZONE_S = 3, ZONE_W = 5, ZONE_E = 7, ZONE_INLAND = 11, ZONE_OUTLAND = 37;
	// 6 basic drag mode types (they are drag status type also)
	private static final int DRAG_NONE = 0;
	public static final int DRAG_N = 2, DRAG_S = 3, DRAG_W = 5, DRAG_E = 7, DRAG_ENTIRE = 11;
	// composite drag mode types
	public static final int DRAG_PROHIBED = 1, DRAG_VERTICAL = 6, DRAG_HONRIZONTAL = 35, DRAG_ALL_BORDERS = 210, DRAG_ALL = 2310;
	// drag_status can only be basic drag mode type
	private int drag_status;
	// the radius of every border zone(eg. ZONE_N). Drag action will be enabled
	// when the cursor hovered over the border zone.
	int r;

	protected static class Cursors {
		public static final Cursor DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);
		public static final Cursor MOVE = new Cursor(Cursor.MOVE_CURSOR);
		public static final Cursor N_RESIZE = new Cursor(Cursor.N_RESIZE_CURSOR);
		public static final Cursor S_RESIZE = new Cursor(Cursor.S_RESIZE_CURSOR);
		public static final Cursor E_RESIZE = new Cursor(Cursor.E_RESIZE_CURSOR);
		public static final Cursor W_RESIZE = new Cursor(Cursor.W_RESIZE_CURSOR);

		public static Cursor getPreferred(int zoneType) {
			switch (zoneType) {
			case ZONE_INLAND:
				return Cursors.MOVE;
			case ZONE_E:
				return E_RESIZE;
			case ZONE_S:
				return S_RESIZE;
			case ZONE_W:
				return W_RESIZE;
			case ZONE_N:
				return N_RESIZE;
			default:
				return DEFAULT;
			}
		}
	}

	private Color fillingColor;

	public JDraggablePane() {
		this(DRAG_ALL);
	}

	public JDraggablePane(int dragMode) {
		this(dragMode, 127, 127, 127, 63);
	}

	public JDraggablePane(int dragMode, int color_r, int color_g, int color_b, int color_alpha) {
		super();
		setOpaque(false);
		setDragMode(dragMode);
		setFillingColor(new Color(color_r, color_g, color_b, color_alpha));
		addDragListeners();
		drag_status = DRAG_NONE;
		r = 10;
		allowDragOver = false;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(fillingColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

	}

	protected void addDragListeners() {

		this.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (drag_status == DRAG_NONE)
					return;

				int x1 = getX(), y1 = getY();
				int x2 = x1 + getWidth(), y2 = y1 + getHeight();
				int xC = x1 + e.getX(), yC = y1 + e.getY();
				switch (drag_status) {
				case DRAG_N:
					if (allowDragOver || yC <= y2)
						setBoundsBy2Points(x1, yC, x2, y2);
					break;
				case DRAG_S:
					if (allowDragOver || yC >= y1)
						setBoundsBy2Points(x1, y1, x2, yC);
					break;
				case DRAG_W:
					if (allowDragOver || xC <= x2)
						setBoundsBy2Points(xC, y1, x2, y2);
					break;
				case DRAG_E:
					if (allowDragOver || xC >= x1)
						setBoundsBy2Points(x1, y1, xC, y2);
					break;
				case DRAG_ENTIRE:
					// TODO not implemented yet

				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();

				int zoneType = locateToZoneType(x, y);
				if (dragMode % zoneType == 0) {
					setCursor(Cursors.getPreferred(zoneType));
				} else {
					setCursor(Cursors.DEFAULT);
				}

			}
		});
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				drag_status = DRAG_NONE;
			}

			@Override
			public void mousePressed(MouseEvent e) {

				int x = e.getX();
				int y = e.getY();
				int zoneType = locateToZoneType(x, y);
				if (dragMode % zoneType == 0) {
					drag_status = zoneType;
				}
			}
		});
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return zone type
	 */
	private int locateToZoneType(int x, int y) {
		int x1 = 0, y1 = 0;
		int x2 = this.getWidth(), y2 = this.getHeight();

		if (isInside(x, y, x1 + r, y1 + r, x2 - r, y2 - r))
			return ZONE_INLAND;
		if (isInside(x, y, x1, y1 - r, x2, y1 + r))
			return ZONE_N;
		if (isInside(x, y, x1, y2 - r, x2, y2 + r))
			return ZONE_S;
		if (isInside(x, y, x1 - r, y1, x1 + r, y2))
			return ZONE_W;
		if (isInside(x, y, x2 - r, y1, x2 + r, y2))
			return ZONE_E;
		return ZONE_OUTLAND;
	}

	/**
	 * judge whether the point(x,y) is inside of the rectangle(x1,y1,x2,y2)
	 * 
	 * @param x
	 *            X of the point
	 * @param y
	 *            Y of the point
	 * @param x1
	 *            X of the rectangle left-top corner
	 * @param y1
	 *            Y of the rectangle left-top corner
	 * @param x2
	 *            X of the rectangle right-bottom corner
	 * @param y2
	 *            Y of the rectangle right-bottom corner
	 * @return
	 */
	boolean isInside(int x, int y, int x1, int y1, int x2, int y2) {
		return (x1 <= x && x <= x2 && y1 <= y && y <= y2);
	}

	public void setBoundsBy2Points(int x1, int y1, int x2, int y2) {
		setBounds(x1, y1, x2 - x1, y2 - y1);
	}

	public int getDragMode() {
		return dragMode;
	}

	public void setDragMode(int dragMode) {
		this.dragMode = dragMode;
	}

	public Color getFillingColor() {
		return fillingColor;
	}

	public void setFillingColor(Color fillingColor) {
		this.fillingColor = fillingColor;
	}

}
