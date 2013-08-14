package hzk.widgets.wscollator;

import hzk.widgets.swing.JImagePane;
import hzk.widgets.util.MyIOUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImageRearrangerUI {
	static Log log = LogFactory.getLog(ImageRearrangerUI.class);
	final static String TITLE = "Image Rearranger UI";
	private JFrame frame;
	private JTextField textField;

	JScrollPane scrollPane;
	List<JImagePane> imgpanes = new ArrayList<JImagePane>();
	List<JButton> btnsSwap = new ArrayList<JButton>();
	List<JButton> btnsTop = new ArrayList<JButton>();
	List<JLabel> lblsSN = new ArrayList<JLabel>();
	private JButton btnSave, btnLoad, btnClear, btnMerge;
	private JLabel lblFlag;
	private JTextField textDir;
	private JPanel panelMain;
	File[] imgFiles;
	BufferedImage[] imgs;
	int[] idx; // image file order index
	boolean[] selected;
	int n;
	int max_w = 460, max_h = 604; // each image's visual max width and height
	String fn_prefix; // file name first 4 chars
	private int batch_select_flag = 0;
	private boolean isLoaded, isModified;
	// the toTop stack flag
	private int top_flag;
	//fonts & colors
	static Font SNfont0=new Font("Arial", Font.BOLD, 36);
	static Font SNfont1=new Font("Arial", Font.BOLD, 36);
	static Font SNfont2=new Font("Arial", Font.BOLD, 48);
	static Color SNcolor0=new Color(180, 150, 200);
	static Color SNcolor1=new Color(255, 20, 0);
			

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		final String[] args1 = args;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				String dir = (args1 != null && args1.length > 0) ? args1[0] : null;
				try {
					ImageRearrangerUI window = new ImageRearrangerUI(dir);
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
	public ImageRearrangerUI() {
		initialize(null);
	}

	public ImageRearrangerUI(String ui_default_arg_dir) {
		initialize(ui_default_arg_dir);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String ui_default_arg_dir) {
		frame = new JFrame();
		frame.setBounds(50, 100, 900, 550);
		frame.setTitle(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		JScrollBar vscrollBar = scrollPane.getVerticalScrollBar();
		JScrollBar hscrollBar = scrollPane.getHorizontalScrollBar();
		vscrollBar.setBlockIncrement(max_h / 2);
		vscrollBar.setUnitIncrement(max_h / 2);
		hscrollBar.setBlockIncrement(max_w / 2);
		hscrollBar.setUnitIncrement(max_w / 2);
		panelMain = new JPanel();
		panelMain.setLayout(null);
		scrollPane.setViewportView(panelMain);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnSave = new JButton("Save & Reload");
		btnSave.setFont(new Font("Arial", Font.BOLD, 14));
		btnSave.setForeground(Color.BLACK);
		btnSave.setEnabled(false);
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				save();
				load();
			}
		});

		btnMerge = new JButton("Merge...");
		btnMerge.setEnabled(false);
		btnMerge.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				startMerging();
			}
		});
		btnMerge.setForeground(Color.BLACK);
		btnMerge.setFont(new Font("Arial", Font.BOLD, 14));
		panel.add(btnMerge);
		panel.add(btnSave);

		JButton btnBatchSelect = new JButton("All/None");

		btnBatchSelect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				batch_select_flag = 1 - batch_select_flag;
				if (batch_select_flag == 0) {
					for (int i = 0; i < n; i++) {
						lblsSN.get(i).setForeground(SNcolor0);
						selected[i] = false;
					}
				} else {
					for (int i = 0; i < n; i++) {
						lblsSN.get(i).setForeground(SNcolor1);
						selected[i] = true;
					}
				}
				updateUIForNumSelect();
			}
		});
		panel.add(btnBatchSelect);

		textDir = new JTextField();
		panel.add(textDir);
		textDir.setColumns(30);
		textDir.setText(ui_default_arg_dir);

		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(10);
		textField.setText("18");
		btnLoad = new JButton("Load");
		btnLoad.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				load();
				scrollPane.repaint();
			}
		});
		panel.add(btnLoad);

		btnClear = new JButton("Clear");
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				isLoaded = false;
				btnSave.setEnabled(false);
				panelMain.removeAll();
				scrollPane.repaint();
				frame.setTitle(TITLE);
			}
		});
		panel.add(btnClear);

		lblFlag = new JLabel("0");
		panel.add(lblFlag);

	}

	private void updateUIForNumSelect() {
		int c = 0;
		for (int i = 0; i < selected.length; i++)
			if (selected[i])
				c++;
		btnMerge.setText("Merge(" + c + ")");

	}

	private void updateUIForTopFlag() {
		if (top_flag > 0){
			btnsTop.get(top_flag - 1).setVisible(false);
			btnsSwap.get(top_flag-1).setVisible(false);
			lblsSN.get(top_flag-1).setFont(SNfont2);
		}
		lblFlag.setText(String.valueOf(top_flag));
	}

	private void loadImages(final JComponent context, String dirPath, final String prefix) throws IOException {
		File dir = new File(dirPath);

		if (!dir.exists()) {
			return;
		}

		final String[] prefixes = prefix.split("[,;]");
		for (String pref : prefixes)
			pref = pref.trim();

		imgFiles = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (!name.endsWith(".jpg"))
					return false;
				for (String pref : prefixes) {
					if (name.startsWith(pref))
						return true;
				}
				return false;
			}

		});
		Arrays.sort(imgFiles, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return (o1.getName().compareTo(o2.getName()));
			}
		});

		n = imgFiles.length;
		fn_prefix = "";
		frame.setTitle("(" + n + ") " + TITLE);
		if (n > 0) {
			fn_prefix = imgFiles[0].getName().substring(0, 4);
			if (!StringUtils.isNumeric(fn_prefix)) {
				fn_prefix = "0000";
			}
			for (int i = 0; i < imgFiles.length; i++)
				log.info("load file: " + imgFiles[i].getName());
			log.info("loaded files count: " + n + "  the file name prefix is set to be: " + fn_prefix);
		} else {
			log.warn("no files loaded! ");
		}

		idx = new int[n];
		selected = new boolean[n];
		imgs = new BufferedImage[n];
		context.removeAll();
		imgpanes.clear();
		btnsSwap.clear();
		btnsTop.clear();
		lblsSN.clear();
		top_flag = 0;
		updateUIForNumSelect();
		updateUIForTopFlag();
		int padding = 10, btnSwap_w = 60,btnTop_w=120;
		int x, y; // flow cursor
		x = 10;
		y = 5;
		boolean recalculated = false;
		for (int i = 0; i < n; i++) {
			final int k = i;

			idx[i] = i;
			selected[i] = false;
			imgs[i] = ImageIO.read(imgFiles[i]);
			if (!recalculated) {
				int w0 = imgs[i].getWidth(), h0 = imgs[i].getHeight();
				max_w = (int) (w0 * max_h / (double) h0);
				recalculated = true;
			}

			int w0 = imgs[i].getWidth(), h0 = imgs[i].getHeight();
			double ratio = calcZoomRatio(w0, h0, max_w, max_h);
			int w = (int) (w0 * ratio), h = (int) (h0 * ratio);
			JImagePane imgpane = new JImagePane(imgs[i], JImagePane.SCALED, w, h);
			imgpane.setImageTitle(imgFiles[i].getName());
			imgpane.setLocation(x + max_w / 2 - w / 2, y);
			x += max_w + padding;
			JButton btnSwap = new JButton("<    >");
			btnSwap.setBounds(x - btnSwap_w / 2 - padding / 2, max_h + 15, btnSwap_w, 40);
			btnSwap.setFont(new Font("Arial", Font.PLAIN, 12));
			btnSwap.setForeground(Color.GRAY);
			btnSwap.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					swapImagesByDisplayedOrdinal(k, k + 1);
				}
			});

			JButton btnTop = new JButton("<<");
			btnTop.setBounds(btnSwap.getX()-padding-btnTop_w, max_h + 15, btnTop_w, 40);
			btnTop.setFont(new Font("Arial", Font.PLAIN, 12));
			btnTop.setForeground(Color.GRAY);
			btnTop.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					swapImagesByDisplayedOrdinal(k, top_flag);
					if (top_flag < n - 1)
						top_flag++;
					updateUIForTopFlag();
					
				}
			});

			JLabel lblSN = new JLabel(to3digits(i + 1));

			lblSN.setBounds(x - max_w / 2 - 30, max_h + 10, 100, 70);
			lblSN.setFont(SNfont0);
			lblSN.setForeground(SNcolor0);
			lblSN.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					selected[k] = !selected[k];
					if (selected[k]) {
						lblsSN.get(k).setForeground(SNcolor1);
					} else {
						lblsSN.get(k).setForeground(SNcolor0);

					}
					updateUIForNumSelect();
				}
			});

			btnsTop.add(i, btnTop);
			btnsSwap.add(i, btnSwap);
			imgpanes.add(i, imgpane);
			lblsSN.add(lblSN);

			context.add(lblSN);
			context.add(imgpane);
			context.add(btnSwap);
			context.add(btnTop);
		}
		if (n > 0) {
			btnsSwap.get(n - 1).setVisible(false);
		}
		x += padding;
		y += 70;
		context.setPreferredSize(new Dimension(x, y));
		context.revalidate();
	}

	private void swapImagesByDisplayedOrdinal(int x1, int x2) {
		swapComponentLocation(imgpanes.get(idx[x1]), imgpanes.get(idx[x2]));
		int t = idx[x1];
		idx[x1] = idx[x2];
		idx[x2] = t;

		isModified = true;
		if (!btnSave.isEnabled())
			btnSave.setEnabled(true);
		if (btnMerge.isEnabled())
			btnMerge.setEnabled(false);
		log.debug(Arrays.toString(idx));
	}

	private void swapComponentLocation(JComponent src, JComponent dest) {
		Point p = src.getLocation();
		src.setLocation(dest.getLocation());
		dest.setLocation(p);
		src.validate();
		dest.validate();
	}

	protected void load() {
		btnLoad.setEnabled(false);
		try {
			loadImages(panelMain, textDir.getText(), textField.getText());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			isLoaded = true;
			isModified = false;
			btnLoad.setEnabled(true);
			btnSave.setEnabled(false);
			btnMerge.setEnabled(true);
			btnClear.setEnabled(true);
		}

	}

	protected void save() {
		if (!isLoaded || !isModified)
			return;
		btnSave.setEnabled(false);
		int n_s = 0, n_f = 0;

		// reorder & rename all ".jpg" files to ".jpg.tmpXXXX"
		File[] tmpfiles = new File[n];
		int n_tmp = 0;
		for (int i = 0; i < n; i++) {
			File f = imgFiles[idx[i]];
			String oldname = f.getName();
			String tmpname = textField.getText() + "_R" + to3digits(i + 1) + ".jpg.tmp" + fn_prefix;
			File nf = MyIOUtils.renameFileSafely(f, tmpname, null);
			if (nf == null) {
				n_f++;
				log.error("!!!!! rename failed : " + oldname + " -> " + tmpname);
			} else {
				log.debug("rename done : " + oldname + " -> " + tmpname);
				tmpfiles[n_tmp++] = nf;
			}

		}

		// rename all ".jpg.tmpXXXX" to ".jpg"
		for (File f : tmpfiles) {
			String tmpname = f.getName();
			String newname = tmpname.substring(0, tmpname.length() - 8);
			File nf = MyIOUtils.renameFileSafely(f, newname, null);
			if (nf == null) {
				n_f++;
				log.error("!!!!! rename failed : " + tmpname + " -> " + newname);
			} else {
				n_s++;
			}
		}
		log.info("result for saving " + n + " file(s): " + n_s + " succeeded, " + n_f + " failed. ");
		JOptionPane.showMessageDialog(frame, "result for saving " + n + " file(s): \n" + n_s + " succeeded, " + n_f + " failed. ");
		btnSave.setEnabled(true);
	}

	protected void startMerging() {
		int c = 0;
		for (int i = 0; i < n; i++)
			if (selected[i])
				c++;
		if (c == 0)
			return;
		BufferedImage[] imgsSelected = new BufferedImage[c];
		int j = 0;
		for (int i = 0; i < n; i++) {
			if (selected[i]) {
				imgsSelected[j++] = imgs[i];
			}
		}
		String save_conf_path = imgFiles[0].getParent();
		String save_conf_prefix = imgFiles[0].getName().substring(0, 4);
		startMergerUI(imgsSelected, save_conf_path, save_conf_prefix);
	}

	private void startMergerUI(final BufferedImage[] imgs, final String save_conf_path, final String save_conf_prefix) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					ImageVMergerUI merger = new ImageVMergerUI(imgs, save_conf_path, save_conf_prefix);
					merger.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected boolean isLegalName(String arg) {
		if (arg == null || arg.length() < 6)
			return false;
		if (arg.charAt(4) != '_')
			return false;
		if (arg.charAt(5) != 'R')
			return false;
		if (!StringUtils.isNumeric(arg.substring(6, 9)))
			return false;
		return true;
	}

	private String to3digits(int arg) {
		return MyIOUtils.to3digits(String.valueOf(arg));
	}

	private double calcZoomRatio(int src_w, int src_h, int max_w, int max_h) {
		if (src_w * max_h > src_h * max_w) {
			return max_w / (double) src_w;
		} else {
			return max_h / (double) src_h;
		}

	}
}
