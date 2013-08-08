package hzk.widgets.wscollator;

import java.awt.EventQueue;

public class UITest {

	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				 String dir=MyLogicApp.pathwx;
				try {
					ImageRearrangerUI window = new ImageRearrangerUI(dir);
					window.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
