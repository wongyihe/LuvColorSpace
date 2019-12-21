
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ReadPNG extends JFrame {
	JFrame parent;
	myPane mypane;

	public ReadPNG() {
		super("demo image");
		setSize(800, 800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		parent = this;
		mypane = new myPane();

		Container c = getContentPane();
		loadImage(new File("input/768px-CIE_1976_UCS.png"));
		JButton grayButton = new JButton("Load Data");

		grayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

//				JFileChooser chooser = new JFileChooser("csv");
//				int option = chooser.showOpenDialog(parent);
//				if (option == JFileChooser.APPROVE_OPTION) {
//					File file = chooser.getSelectedFile();
				File file = new File("input/Draw-Luv-color-space.csv");
				mypane.LoadData(file);
				mypane.repaint();
//					System.out.println(file);
//				} else {
//				}

			}
		});

		JPanel top = new JPanel();
		top.add(grayButton);
		c.add(top, "First");
		c.add(new JScrollPane(mypane), "Center");
	}

	private void loadImage(File file) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			System.out.println("read error: " + e.getMessage());
		}
		mypane.setImage(image);
	}

	public static void main(String args[]) {
		ReadPNG vc = new ReadPNG();
		vc.setVisible(true);
	}
}

class myPane extends JPanel {

	Dataset data = new Dataset();
	BufferedImage image;
	Dimension size = new Dimension();

	public myPane() {
	}

	public myPane(BufferedImage image) {
		this.image = image;
		setComponentSize();
	}

	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		if (data.index.size() <= 1) {
			g.drawImage(image, 0, 0, this);
		} else {
			g.drawImage(image, 0, 0, this);
			int size_w = 5;
			int size_h = 5;

			for (int index = 1; index < data.index.size(); index++) {
				int standardu = UtoPos(Float.valueOf(data.standardU.get(index)));
				int standardv = VtoPos(Float.valueOf(data.standardV.get(index)));
				int tv1u = UtoPos(Float.valueOf(data.TV1U.get(index)));
				int tv1v = VtoPos(Float.valueOf(data.TV1V.get(index)));
				int tv2u = UtoPos(Float.valueOf(data.TV2U.get(index)));
				int tv2v = VtoPos(Float.valueOf(data.TV2V.get(index)));

				// standard
				g.drawRect(standardu, standardv, size_w, size_h);
				g.drawString(index + " ", standardu, standardv);
				
				// tv1
				g.drawOval(tv1u, tv1v, size_w, size_h);
				
				// tv2
				DrawTriangle t = new DrawTriangle(tv2u, tv2v);
				t.draw(g);
				
				// draw line standard to tv1
				g.drawLine(standardu, standardv, tv1u, tv1v);
				
				// draw line standard to tv2
				g.drawLine(standardu, standardv, tv2u, tv2v);

			}

		}

	}

	public Dimension getPreferredSize() {
		return size;
	}

	public void setImage(BufferedImage bi) {
		image = bi;
		setComponentSize();
		repaint();
	}

	public void LoadData(File file) {

		CSVReader cvsfile = new CSVReader(file, data);
		data.print();
	}

	private void setComponentSize() {
		if (image != null) {
			size.width = image.getWidth();
			size.height = image.getHeight();
			revalidate();
		}
	}

	private int UtoPos(float u) {
		int pos = 0;
		int delta = 727 - 36;
		pos = (int) (36 + (u / 0.6) * delta);
		return pos;
	}

	private int VtoPos(float v) {
		int pos = 0;
		int delta = 727 - 36;
		pos = (int) (727 - (v / 0.6) * delta);
		return pos;
	}

}

class CSVReader {

	public CSVReader(File file, Dataset info) {

		String csvFile = file.getPath();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				String[] data = line.split(cvsSplitBy);
				for (int i = 0; i < data.length; i++) {

					switch (i) {
					case 0:
						info.index.addElement(data[i]);
						break;
					case 1:
						info.standardU.addElement(data[i]);
						break;
					case 2:
						info.standardV.addElement(data[i]);
						break;
					case 3:
						info.TV1U.addElement(data[i]);
						break;
					case 4:
						info.TV1V.addElement(data[i]);
						break;
					case 5:
						info.TV2U.addElement(data[i]);
						break;
					case 6:
						info.TV2V.addElement(data[i]);
						break;
					default:
						break;
					}
				}

			}

		} catch (Exception e) {
			System.out.println("catch1:" + e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("catch2:" + e);
				}
			}
		}

	}

}

class Dataset {

	Vector<String> index = new Vector<String>();
	Vector<String> standardU = new Vector<String>();
	Vector<String> standardV = new Vector<String>();
	Vector<String> TV1U = new Vector<String>();
	Vector<String> TV1V = new Vector<String>();
	Vector<String> TV2U = new Vector<String>();
	Vector<String> TV2V = new Vector<String>();

	public void print() {

		for (int i = 0; i < index.size(); i++)
			System.out.print(index.get(i) + "|");
		System.out.println();

		for (int i = 0; i < standardU.size(); i++)
			System.out.print(standardU.get(i) + "|");
		System.out.println();

		for (int i = 0; i < standardV.size(); i++)
			System.out.print(standardV.get(i) + "|");
		System.out.println();

		for (int i = 0; i < TV1U.size(); i++)
			System.out.print(TV1U.get(i) + "|");
		System.out.println();

		for (int i = 0; i < TV1V.size(); i++)
			System.out.print(TV1V.get(i) + "|");
		System.out.println();

		for (int i = 0; i < TV2U.size(); i++)
			System.out.print(TV2U.get(i) + "|");
		System.out.println();

		for (int i = 0; i < TV2U.size(); i++)
			System.out.print(TV2V.get(i) + "|");
		System.out.println();
	}
}

abstract class draw_element {
	protected Point p;

	public draw_element(Point p) {
		super();
		this.p = p;
	}

	public draw_element(int x, int y) {
		p = new Point();
		p.x = x;
		p.y = y;
	}

	abstract void draw(Graphics g);
}

class DrawTriangle extends draw_element {
	int x[] = { 0, 8, 8 };
	int y[] = { 4, 0, 8 };

	public DrawTriangle(int x, int y) {
		super(x, y);
		System.out.println("x:" + x);
		System.out.println("y:" + y);
	}

	@Override
	void draw(Graphics g) {
		for (int i = 0; i < x.length; i++) {

			x[i] = x[i] + p.x;
			y[i] = y[i] + p.y;
			// System.out.println("x " + i + ": " + x[i]);
			// System.out.println("y " + i + ": " + y[i]);
		}

		for (int j = 0; j < x.length; j++) {
			System.out.println("x " + j + ": " + x[j]);
			System.out.println("y " + j + ": " + y[j]);
		}

		// System.out.println("yeahhh!!!");
		g.drawPolygon(x, y, 3);

	}
}
