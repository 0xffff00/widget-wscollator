package hzk.widgets.swing;

import javax.swing.JComponent;

/**
 * transformation of Swing Components include such actions as translation,
 * rotation, scaling, shearing
 * 
 * @author HZK
 * 
 */
public class JTransformUtils {

	public static class Direction {
		public static final int NORTH = 2, SOUTH = 3, WEST = 5, EAST = 7;
		public static final int TOP = 2, BOTTOM = 3, LEFT = 5, RIGHT = 7;
	}

	public static void dragBorder(JComponent comp, int direction, int offset) {
		if (comp == null) {
			return;
		}
		int x = comp.getX(), y = comp.getY(), w = comp.getWidth(), h = comp.getHeight();
		int k = offset;
		switch (direction) {
		case Direction.TOP:
			comp.setBounds(x, y - k, w, h + k);
			break;
		case Direction.BOTTOM:
			comp.setBounds(x, y, w, h + k);
			break;
		case Direction.LEFT:
			comp.setBounds(x - k, y, w + k, h);
			break;
		case Direction.RIGHT:
			comp.setBounds(x, y, w + k, h);
		}

	}

	public static void dragTopBorderToFixedHeight(JComponent comp, int aimHeight) {
		dragBorder(comp, Direction.TOP, aimHeight - comp.getHeight());
	}

	public static void dragBottomBorderToFixedHeight(JComponent comp, int aimHeight) {
		dragBorder(comp, Direction.BOTTOM, aimHeight - comp.getHeight());
	}

	public static void dragLeftBorderToFixedWidth(JComponent comp, int aimWidth) {
		dragBorder(comp, Direction.LEFT, aimWidth - comp.getWidth());
	}

	public static void dragRightBorderToFixedWidth(JComponent comp, int aimWidth) {
		dragBorder(comp, Direction.RIGHT, aimWidth - comp.getWidth());
	}

	public static void setBoundsBy2Points(JComponent comp, int x1, int y1, int x2, int y2) {
		comp.setBounds(x1, y1, x2 - x1, y2 - y1);
	}
}
