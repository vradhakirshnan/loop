import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.filechooser.FileFilter;
import java.lang.Integer;



import data.object;
import data.point;
import data.face;

public class frame extends JFrame implements ActionListener{


	int num=0;
	JButton button1,button2,button3,button4,button5;
	JPanel pane;
	int val = 6;
	//object[] figure= new object[5];
	ArrayList objects;



	public frame()
	{
		JButton button1 =  new JButton("open");
		button1.setActionCommand("open");
		button1.addActionListener(this);

		JButton button2 =  new JButton("save");
		button2.setActionCommand("save");
		button2.addActionListener(this);

		JButton button3 =  new JButton("subdivide");
		button3.setActionCommand("divide");
		button3.addActionListener(this);

		JButton button4 =  new JButton("retrace");
		button4.setActionCommand("retrace");
		button4.addActionListener(this);

		JButton button5 =  new JButton("get mesh");
		button5.setActionCommand("getmesh");
		button5.addActionListener(this);

		JComboBox valence= new JComboBox();
		valence.addItem((String)"3");
		valence.addItem((String)"4");
		valence.addItem((String)"5");
		valence.addItem((String)"6");
		valence.addItem((String)"7");
		valence.addItem((String)"8");
		valence.setSelectedIndex(3);
		valence.setActionCommand("combo");
		valence.addActionListener(this);




		JPanel pane =  new JPanel();
		pane.add(button1);
		pane.add(button2);
		pane.add(button3);
		pane.add(button4);
		pane.add(button5);
		pane.add(valence);

		this.getContentPane().add(pane, BorderLayout.NORTH);
		pane.setVisible(true);
		this.pack();
		this.setSize(700,700);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		      	System.exit(0);}
        });

		objects =  new ArrayList();
	}
	 public void actionPerformed(ActionEvent e) {


		 if (e.getActionCommand()=="open")
		 {
			 System.out.println("open");
			 final JFileChooser fc = new JFileChooser("h:\\");
			 //FileFilter f =new FileFilter();
			 int returnVal = fc.showOpenDialog(this.button1);
			 String filename = fc.getSelectedFile().getPath();
			// this.figure[0] =  new object(filename);
			objects.clear();
			 objects.add(new object(filename));
			 num=1;
			 this.repaint();

		 }
		 if (e.getActionCommand()=="save")
		 {
			 if(num>0)
			 {
				 System.out.println("save");
			 	final JFileChooser fc = new JFileChooser("h:\\");
			 	int returnVal = fc.showSaveDialog(this.button2);
			 	String filename = fc.getSelectedFile().getPath();
			 	((object)objects.get(num-1)).save(filename);
			}
		 }
		 if (e.getActionCommand()=="divide")
		 {
			if(num>0)
			{
				System.out.println("sub");
				//figure[num]=figure[num-1].subdivide();
				objects.add(((object)objects.get(num-1)).loop());
				num++;
				this.repaint();
			}

		 }
		 if (e.getActionCommand()=="retrace")
		 {
			 System.out.println("ret");
			 num--;
			 objects.remove(num);
			 repaint();

		 }
		 if (e.getActionCommand()=="getmesh")
		 {
			 //System.out.println(val);

		 }
		 if (e.getActionCommand()=="combo")
		 {
		 	JComboBox cb = (JComboBox)e.getSource();
		 	System.out.println("Aaaaa\n" + (String)cb.getSelectedItem());
		 	Integer I=new Integer((String)(cb.getSelectedItem()));
		 	this.val = I.intValue();
		 }
	 }
	public void paint(Graphics g)
	{
		super.paint(g);
		if(num>0)
		{
			Graphics2D g2 = (Graphics2D)g;
			//figure[num-1].plot_object(g2);
			((object)objects.get(num-1)).plot_object(g2);

		}
	}

	public static void main(String args[])
	{
		frame fr = new frame();
	}
}

