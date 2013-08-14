package hzk.widgets.wscollator;

import hzk.widgets.swing.JDraggablePane;
import hzk.widgets.swing.JImagePane;
import hzk.widgets.swing.JTransformUtils;
import hzk.widgets.util.ImageTransformUtils;
import hzk.widgets.util.MyIOUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImageVMergerUI {
	static Log log = LogFactory.getLog(ImageVMergerUI.class);
	final static String TITLE = "Image VMerger UI";
	private JFrame frame;
	JButton btnSave;

	JScrollPane scrollPane;
	JPanel panelCons;
	JLayeredPane panelImgs;
	BufferedImage[] imgs;

	List<JImagePane> imgpanes = new ArrayList<JImagePane>();
	List<JDraggablePane> masks1 = new ArrayList<JDraggablePane>();
	List<JDraggablePane> masks2 = new ArrayList<JDraggablePane>();
	List<JTextField> texts1 = new ArrayList<JTextField>();
	List<JTextField> texts2 = new ArrayList<JTextField>();
	List<JButton> btnMasks = new ArrayList<JButton>();
	double ratio = 0.75; // image zoom ratio
	int n; // image count
	// each image's visual max width and height
	int max_w = 460, max_h = (int) (604 * 0.75);
	int def_msk_h = 25; // default height of mask panels height
	boolean isLoaded;
	private JLabel label;
	private JButton btnReload;
	private JPanel panel;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;

	String save_conf_path;
	String save_conf_prefix;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageVMergerUI window = new ImageVMergerUI(null, null, null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void show() {
		// frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public ImageVMergerUI(BufferedImage[] imgs, String save_conf_path, String save_conf_prefix) {
		this.imgs = imgs;
		this.save_conf_path = save_conf_path;
		this.save_conf_prefix = save_conf_prefix;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				// testDemo();
				try {
					loadImages(panelImgs, imgs);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				scrollPane.repaint();
			}
		});
		frame.setBounds(150, 20, 600, 700);
		frame.setTitle(TITLE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane);
		JScrollBar vscrollBar = scrollPane.getVerticalScrollBar();
		JScrollBar hscrollBar = scrollPane.getHorizontalScrollBar();
		vscrollBar.setBlockIncrement(max_h / 2);
		vscrollBar.setUnitIncrement(max_h / 2);
		hscrollBar.setBlockIncrement(max_w / 2);
		hscrollBar.setUnitIncrement(max_w / 2);

		panelCons = new JPanel();
		panelCons.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		frame.getContentPane().add(panelCons, BorderLayout.WEST);
		panelImgs = new JLayeredPane();
		panelImgs.setBounds(79, 0, 36, 35);
		panelImgs.setLayout(null);
		scrollPane.setViewportView(panelImgs);
		panelCons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		panel = new JPanel();
		panel.setBounds(0, 0, 50, 80);
		panelCons.add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		btnSave = new JButton("Save");
		panel.add(btnSave);
		btnSave.setEnabled(false);

		label_1 = new JLabel("");
		panel.add(label_1);

		label_2 = new JLabel("");
		panel.add(label_2);

		label_3 = new JLabel("");
		panel.add(label_3);

		label = new JLabel("0");
		panel.add(label);

		btnReload = new JButton("Reload");
		panel.add(btnReload);
		btnReload.setHorizontalAlignment(SwingConstants.LEFT);
		btnReload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				isLoaded = false;
				btnSave.setEnabled(false);
				try {
					loadImages(panelImgs, imgs);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				scrollPane.repaint();
			}
		});
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					mergeImgsAndSave();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

	}

	private void loadImages(JLayeredPane context, BufferedImage[] imgs) throws IOException {
		if (imgs == null || imgs.length == 0) {
			JOptionPane.showMessageDialog(frame, "no image loaded!", null, JOptionPane.ERROR_MESSAGE);
			return;
		}
		this.imgs = imgs;
		this.n = imgs.length;
		context.removeAll();
		isLoaded = false;
		imgpanes.clear();
		masks1.clear();
		masks2.clear();
		texts1.clear();
		texts2.clear();
		btnMasks.clear();
		int x, y; // flow cursor
		x = 10;
		y = 0;

		int w, h = 0;
		for (int i = 0; i < n; i++) {
			final int _i = i;
			int w0 = imgs[i].getWidth(), h0 = imgs[i].getHeight();

			w = (int) (w0 * ratio);
			h = (int) (h0 * ratio);
			JImagePane imgpane = new JImagePane(imgs[i], JImagePane.SCALED, w, h);
			imgpane.setLocation(x, y);

			JDraggablePane mask1 = new JDraggablePane(JDraggablePane.DRAG_S, 0, 0, 255, 60);
			JDraggablePane mask2 = new JDraggablePane(JDraggablePane.DRAG_N, 0, 30, 233, 60);

			mask1.setBounds(x, y, w, 0);
			mask2.setBounds(x, y + h, w, 0);

			JTextField text1 = new JTextField("0");
			JTextField text2 = new JTextField("0");
			text1.setBounds(x + w + 5, y, 70, 25);
			text2.setBounds(x + w + 5, y + h - 25, 70, 25);
			JButton btnmask = new JButton();
			if (i < n - 1) {
				btnmask.setBounds(x + w + 75, y + h - 25, 30, 50);
				btnmask.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						JDraggablePane _mask2 = masks2.get(_i);
						JDraggablePane _mask1n = masks1.get(_i + 1);

						if (_mask2.getHeight() + _mask1n.getHeight() > 0) {
							JTransformUtils.dragTopBorderToFixedHeight(_mask2, 0);
							JTransformUtils.dragBottomBorderToFixedHeight(_mask1n, 0);
						} else {
							JTransformUtils.dragTopBorderToFixedHeight(_mask2, def_msk_h);
							JTransformUtils.dragBottomBorderToFixedHeight(_mask1n, def_msk_h);
						}
					}
				});
			} else {

			}

			btnMasks.add(btnmask);
			imgpanes.add(imgpane);
			masks1.add(mask1);
			masks2.add(mask2);
			texts1.add(text1);
			texts2.add(text2);

			JLabel lblSN = new JLabel(MyIOUtils.to3digits(String.valueOf(i + 1)));
			lblSN.setBounds(x + w + 10, y + h / 2 + 10, 70, 50);
			lblSN.setFont(new Font("Arial", Font.BOLD, 36));
			lblSN.setForeground(new Color(180, 150, 200));

			context.add(btnmask, JLayeredPane.PALETTE_LAYER);
			context.add(imgpane, JLayeredPane.DEFAULT_LAYER);
			context.add(lblSN, JLayeredPane.DEFAULT_LAYER);
			context.add(mask1, JLayeredPane.PALETTE_LAYER);
			context.add(mask2, JLayeredPane.PALETTE_LAYER);
			context.add(text1, JLayeredPane.PALETTE_LAYER);
			context.add(text2, JLayeredPane.PALETTE_LAYER);

			y += h;
		}
		// the last btnmask is different

		w = (int) (imgs[n - 1].getWidth() * ratio);

		JButton btnmaskLast = new JButton();
		btnmaskLast.setBounds(x + w + 75, y - 25, 30, 25);
		btnmaskLast.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JDraggablePane _mask2 = masks2.get(n - 1);

				if (_mask2.getHeight() > 0) {
					JTransformUtils.dragTopBorderToFixedHeight(_mask2, 0);
				} else {
					JTransformUtils.dragTopBorderToFixedHeight(_mask2, def_msk_h);
				}
			}
		});

		// the first btnmask is different
		w = (int) (imgs[0].getWidth() * ratio);
		JButton btnmaskFirst = new JButton();
		btnmaskFirst.setBounds(x + w + 75, 0, 30, 25);
		btnmaskFirst.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JDraggablePane _mask1 = masks1.get(0);

				if (_mask1.getHeight() > 0) {
					JTransformUtils.dragBottomBorderToFixedHeight(_mask1, 0);

				} else {
					JTransformUtils.dragBottomBorderToFixedHeight(_mask1, def_msk_h);
				}
			}
		});

		btnMasks.add(btnmaskLast);
		btnMasks.add(btnmaskFirst);
		context.add(btnmaskLast, JLayeredPane.PALETTE_LAYER);
		context.add(btnmaskFirst, JLayeredPane.PALETTE_LAYER);

		x += 10;
		y = y - h + 10;

		for (int i = 0; i < n; i++) {
			final int k = i;
			JTextField text1 = texts1.get(i);
			JTextField text2 = texts2.get(i);
			JDraggablePane mask1 = masks1.get(i);
			JDraggablePane mask2 = masks2.get(i);
			text1.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						int h = toInt(texts1.get(k).getText());
						if (h < 0)
							return;
						JDraggablePane m = masks1.get(k);
						m.setBounds(m.getX(), m.getY(), m.getWidth(), h);
					}
				}

			});
			text2.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						int h = toInt(texts2.get(k).getText());
						if (h < 0)
							return;
						JDraggablePane m = masks2.get(k);
						m.setBounds(m.getX(), m.getY() + m.getHeight() - h, m.getWidth(), h);
					}
				}

			});
			text1.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					int h = toInt(texts1.get(k).getText());
					if (h < 0)
						return;
					JDraggablePane m = masks1.get(k);
					m.setBounds(m.getX(), m.getY(), m.getWidth(), h);
				}
			});
			text2.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					int h = toInt(texts2.get(k).getText());
					if (h < 0)
						return;
					JDraggablePane m = masks2.get(k);
					m.setBounds(m.getX(), m.getY() + m.getHeight() - h, m.getWidth(), h);
				}
			});

			mask1.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					int h = e.getComponent().getHeight();
					texts1.get(k).setText(String.valueOf(h));
				}
			});
			mask2.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					int h = e.getComponent().getHeight();
					texts2.get(k).setText(String.valueOf(h));
				}
			});
		}
		y += h;
		context.setPreferredSize(new Dimension(x, y));
		context.revalidate();
		isLoaded = true;
		label.setText(String.valueOf(n));
		btnSave.setEnabled(true);

	}

	protected void mergeImgsAndSave() throws IOException {
		int[] y1_arr = new int[n];
		int[] y2_arr = new int[n];
		for (int i = 0; i < n; i++) {
			y1_arr[i] = (int) (masks1.get(i).getHeight() / ratio);
			y2_arr[i] = (int) (masks2.get(i).getHeight() / ratio);
		}

		BufferedImage merged_img = ImageTransformUtils.mergeVertically(imgs, y1_arr, y2_arr);
		String fn = "MG_" + getNowTimeStr_yyyyMMddHHmmss() +"_"+ save_conf_prefix + "_(" + n + ")_" + ".jpg";
		ImageIO.write(merged_img, "jpeg", new File(save_conf_path + "\\" + fn));
		JOptionPane.showMessageDialog(frame, "merged and saved successfully:\n" + fn);
	}

	protected static String getNowTimeStr_yyyyMMddHHmmss() {
		return DateFormatUtils.format(Calendar.getInstance(), "yyyyMMddHHmmss");
	}

	protected static int toInt(String text) {
		if (text == null || text.isEmpty())
			return -1;
		if (StringUtils.isNumeric(text))
			return Integer.valueOf(text);
		return -1;
	}
}
