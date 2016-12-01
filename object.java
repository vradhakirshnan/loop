package data;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;


import data.point;
import data.face;




public class object{
	int num_of_points,num_of_faces, num_of_edjes;
	ArrayList points;
	int faces[][];
	boolean formed;
	double max_x=(double)0, max_y=(double)0;
	double min_x=(double)0, min_y=(double)0;

	public int ret_num_faces(){return this.num_of_faces;}
	public int ret_num_points(){return this.num_of_points;}
	public int ret_num_edjes(){return this.num_of_edjes;}
	public ArrayList ret_points(){return this.points;}
	public int[][] ret_faces(){return this.faces;}


	public double ret_max_x(){return this.max_x;}
	public double ret_max_y(){return this.max_y;}
	public double ret_min_x(){return this.min_x;}
	public double ret_min_y(){return this.min_y;}


	double get_x(int i)
	{
		double x = ((point)this.points.get(i)).ret_x();
		double z = ((point)this.points.get(i)).ret_z();
		return conv(x,z);

	}

	double get_y(int i)
	{
		double y = -((point)this.points.get(i)).ret_y();
		double z = -((point)this.points.get(i)).ret_z();
		return conv(y,z);
	}

/**********************************************/
	object(int num_of_points,int num_of_faces,int num_of_edjes, ArrayList points, int[][] faces )
	{
		this.num_of_points=num_of_points;
		this.num_of_faces=num_of_faces;
		this.num_of_edjes=num_of_edjes;
		this.points = points;
		this.faces = faces;
		this.formed =false;
		tiangulate();
		bounding_box();

	}
/**********************************************/
	public object(String Filename)
	{
		try{
				FileInputStream fis = new FileInputStream(Filename);
				InputStreamReader in = new InputStreamReader((InputStream)fis);
				BufferedReader br = new BufferedReader(in);
				String off =(br.readLine()).trim();
				if(off.equals("OFF"))
				{
					String ln=(br.readLine()).trim();
					ArrayList a = split_string(ln);
					this.num_of_points = Integer.parseInt((String)a.get(0));
					this.num_of_faces = Integer.parseInt((String)a.get(1));
					this.num_of_edjes = Integer.parseInt((String)a.get(2));
					this.points =  new ArrayList();
					this.faces = new int[this.num_of_faces][];
					for(int i=0;i<this.num_of_points;i++)
					{
						String pts = (br.readLine()).trim();
						ArrayList ps = split_string(pts);
						float x = Float.parseFloat((String)ps.get(0));
						float y = Float.parseFloat((String)ps.get(1));
						float z = Float.parseFloat((String)ps.get(2));
						point p = new point(x,y,z);
						//p.print_point();
						this.points.add(p);
					}
					for(int i=0;i<this.num_of_faces;i++)
					{
						String fcs= (br.readLine()).trim();
						ArrayList fs = split_string(fcs);
						int np =  Integer.parseInt((String)fs.get(0));
						this.faces[i]= new int[np+1];
						this.faces[i][0]=np;
						//System.out.println(this.faces[i][0]);
						for(int j=1;j<=np;j++)
						{
						 	this.faces[i][j]=Integer.parseInt((String)fs.get(j));
						// 	System.out.print(this.faces[i][j]+ " ");

						}
						//System.out.println();
					}
					this.formed =true;
					//subdivide();
					tiangulate();
					bounding_box();

				}
				else
				{
					System.out.println("not an off file..");
					this.formed =false;
				}

			}
		catch(Exception e){	e.printStackTrace();}
	}
	public void tiangulate()
	{
		//number of faces to b removed,,,
		//number of triangles to b addes....
		int fcs=0;
		int cnt=0;
		for (int i=0;i<this.num_of_faces;i++)
		{
			if(this.faces[i][0]>3)
			{
				fcs =fcs+1;
				cnt = cnt + this.faces[i][0];
			}
		}
		int new_faces = this.num_of_faces +cnt - fcs;
		System.out.println(new_faces);
		int newfaces[][] =  new int[new_faces][4];

		cnt=0;
		for (int i=0;i<this.num_of_faces;i++)
		{

			if(this.faces[i][0]>3)
			{
				point newpoint = ((face)form_face(i)).calc_centroid();
				this.points.add(newpoint);
				int index= this.num_of_points;
				this.num_of_points ++;
				for(int j =1;j<this.faces[i][0];j++)
				{
					newfaces[cnt][0]=3;
					newfaces[cnt][1] =index;
					newfaces[cnt][2] =this.faces[i][j];
					newfaces[cnt][3] =this.faces[i][j+1];
					cnt++;
				}
				newfaces[cnt][0]=3;
				newfaces[cnt][1] =index;
				newfaces[cnt][2] =this.faces[i][this.faces[i][0]];
				newfaces[cnt][3] =this.faces[i][1];
				cnt++;

			}
			else
			{
				newfaces[cnt] = this.faces[i];
				cnt++;
			}
		}
		this.num_of_faces=cnt;
		this.faces=newfaces;
		System.out.println(cnt);

	}
/**********************************************/
public double conv(double x1,double z1)
{
	double pi = 3.1415926535;
	double  rad  = (float)(pi/(float)4);
	return (double )((double )x1+((double )z1*(double )Math.cos(rad)));

}

void bounding_box()
{
	for(int i=0;i< this.num_of_points;i++)
	{
		double tempx =  conv(((point)this.points.get(i)).ret_x(),((point)this.points.get(i)).ret_z());
		double tempy =  conv(((point)this.points.get(i)).ret_y(),((point)this.points.get(i)).ret_z());
		if(tempx >max_x) max_x =  tempx;
		if(tempy >max_y) max_y =  tempy;
		if(tempx <min_x) min_x =  tempx;
		if(tempy <min_y) min_y =  tempy;
	}
	//System.out.println("\n" + max_x + "\t" + max_y + "\t" + min_x + "\t" + min_y + "\n");

}



/**********************************************/
	private ArrayList split_string(String s)
	{
		ArrayList sub = new ArrayList();
		//System.out.println(s);
		int start_pos =s.indexOf(" ");
		//System.out.println(s);
		//System.out.println(start_pos);
		while(start_pos !=-1)
		{
			String str = s.substring(0,start_pos).trim();
			if(sub.add(str));//System.out.println(str);
			s = (s.substring(start_pos)).trim();
			start_pos =s.indexOf(" ");
			//System.out.println(s);
			//System.out.println(start_pos);
		}
		if(sub.add(s));//System.out.println(s);
		return  sub;
	}

/**********************************************/
	face form_face(int pos)
	{
		face f = new face();
		//System.out.println(pos);
		for(int i=1;i<=this.faces[pos][0];i++)
		{
			f.add_point((point)this.points.get(this.faces[pos][i]));
		}
		f.add_point((point)this.points.get(this.faces[pos][1]));
		return f;
	}
/**********************************************/

	public void edjenumber()
	{
		int num=0;
		int arr[][]= new int[this.num_of_points][this.num_of_points];
		for(int i=0;i<this.num_of_points;i++)
		for(int j=0;j<this.num_of_points;j++)arr[i][j]=0;

		for(int i=0;i<this.num_of_faces;i++)
		{

			for(int j=1;j<this.faces[i][0];j++)
			{

				int pos1=this.faces[i][j];
				int pos2=this.faces[i][j+1];
				//System.out.println(pos1+"  "+pos2);
				arr[pos1][pos2]=1;
				if(arr[pos2][pos1]==0)
				{
					num++;
				}

			}

			int pos2=faces[i][1];
			int pos1=faces[i][this.faces[i][0]];
			//System.out.println(pos1+"  "+pos2);
			arr[pos1][pos2]=1;
			if(arr[pos2][pos1]==0)
			{
				num++;
			}

		}
		this.num_of_edjes =  num;
		System.out.println("edjes: "+num);


	}

	/**********************************************/
	//find face for edje p-q
	int  find_face(int p, int q)
	{
		boolean got = false;
		for(int i=0;i<this.num_of_faces;i++)
		{
			for(int j=1;j<this.faces[i][0];j++)
			{
				if(this.faces[i][j]==p && this.faces[i][j+1]==q)return i;
			}
			if(this.faces[i][this.faces[i][0]]==p && this.faces[i][1]==q)return i;
		}
		return -1;
	}

	//find postions for edje pq...
	//returns an array of faces pos and point post...
	int[] find_point_pos(int p, int q)
	{
		int ret[]=new int[2];
		ret[0]=-1;
		ret[1]=-1;
		for(int i=0;i<this.num_of_faces;i++)
		{
			for(int j=1;j<this.faces[i][0];j++)
			{
				if(this.faces[i][j]==p && this.faces[i][j+1]==q)
				{
					//face number
					ret[0]=i;
					//index of points...
					ret[1]=j;
					break;
				}
			}
			if(this.faces[i][this.faces[i][0]]==p && this.faces[i][1]==q)
			{
				ret[0]=i;
				ret[1]=this.faces[i][0];
				break;
			}
		}
		return ret;

	}


/**********************************************/
public double alpha(int n)
{
	double pi = (double)22/(double)7;
	double a=Math.pow((double)3+(double)2*Math.cos(2*pi/n),(double)2);
	return (((double)5/(double)8 ) - (a/(double)64));
}
/**********************************************/
public int get_point(int p, int q)
{

	for(int i=0;i<this.num_of_faces;i++)
	{
		if(this.faces[i][1]==p && this.faces[i][2]==q) return this.faces[i][3];
		if(this.faces[i][2]==p && this.faces[i][3]==q) return this.faces[i][1];
		if(this.faces[i][3]==p && this.faces[i][1]==q) return this.faces[i][2];

	}
	return -1;

}
/**********************************************/
int  get_valence(int p)
	{
		int ret =0;
		for(int i=0;i<this.num_of_faces;i++)
		{
			for(int j=1;j<=4;j++)
			{
				if(this.faces[i][j]==p )ret++;
			}
		}
		return ret;
	}
/**********************************************/
	public object loop()
	{
		//edje points index...
		int[][] edjes =  new int[this.num_of_faces][4];
		double omega;//loop function....
		int num = 0,cnt=0;

		//stores the new points generated...
		ArrayList newpoints =  new ArrayList();
		//stores the new face indices....
		int[][] newfaces = new int[this.num_of_faces*4][4];

		for(int i=0;i< this.num_of_faces;i++)
			for(int j=0;j<4;j++)
			edjes[i][j]=-1;


		//get edje points...
		edjenumber();
		for(int i=0;i<this.num_of_faces;i++)
		{
			double tx,ty,tz;
			//for edje 1
			if(edjes[i][1]==-1)
			{

				point p = (point)this.points.get(get_point(this.faces[i][2],this.faces[i][1]));
				tx =(((double)1/(double)8)*
				(p.ret_x() + ((point)this.points.get(this.faces[i][3])).ret_x()))
				+((double)3/(double)8)*
				(((point)this.points.get(this.faces[i][1])).ret_x()+
				((point)this.points.get(this.faces[i][2])).ret_x());

				ty =(((double)1/(double)8)*
				(p.ret_y() + ((point)this.points.get(this.faces[i][3])).ret_y()))
				+((double)3/(double)8)*
				(((point)this.points.get(this.faces[i][1])).ret_y()+
				((point)this.points.get(this.faces[i][2])).ret_y());

				tz =(((double)1/(double)8)*
				(p.ret_z() + ((point)this.points.get(this.faces[i][3])).ret_z()))
				+(((double)3/(double)8)*
				(((point)this.points.get(this.faces[i][1])).ret_z()+
				((point)this.points.get(this.faces[i][2])).ret_z()));
				newpoints.add(new point(tx,ty,tz));
				edjes[i][1]=cnt;
				int[] pos = find_point_pos(this.faces[i][2],this.faces[i][1]);
				//System.out.println(edjes[i][1]);
				//System.out.println(pos[0] + "\t" + pos[1]);
				edjes[pos[0]][pos[1]]=cnt;
				cnt++;
			}
			if(edjes[i][2]==-1)
			{
				//for edje 2
				point p= (point)this.points.get(get_point(this.faces[i][3],this.faces[i][2]));

				tx =(((double)1/(double)8)*
				(p.ret_x() + ((point)this.points.get(this.faces[i][1])).ret_x()))
				+(((double)3/(double)8)*
				(((point)this.points.get(this.faces[i][2])).ret_x()+
				((point)this.points.get(this.faces[i][3])).ret_x()));

				ty =(((double)1/(double)8)*
				(p.ret_y() + ((point)this.points.get(this.faces[i][1])).ret_y()))
				+(((double)3/(double)8)*
				(((point)this.points.get(this.faces[i][2])).ret_y()+
				((point)this.points.get(this.faces[i][3])).ret_y()));

				tz =(((double)1/(double)8)*
				(p.ret_z() + ((point)this.points.get(this.faces[i][1])).ret_z()))
				+(((double)3/(double)8)*
				(((point)this.points.get(this.faces[i][2])).ret_z()+
				((point)this.points.get(this.faces[i][3])).ret_z()));
				newpoints.add(new point(tx,ty,tz));
				edjes[i][2]=cnt;
				int[] pos = find_point_pos(this.faces[i][3],this.faces[i][2]);
			//	System.out.println(edjes[i][1]);
			//	System.out.println(pos[0] + "\t" + pos[1]);
				edjes[pos[0]][pos[1]]=cnt;
				cnt++;
			}

			//for edje 3
			if(edjes[i][3]==-1)
			{
				point p= (point)this.points.get(get_point(this.faces[i][1],this.faces[i][3]));

				tx =((double)1/(double)8)*
				(p.ret_x() + ((point)this.points.get(this.faces[i][2])).ret_x())
				+((double)3/(double)8)*
				(((point)this.points.get(this.faces[i][3])).ret_x()+
				((point)this.points.get(this.faces[i][1])).ret_x());

				ty =((double)1/(double)8)*
				(p.ret_y() + ((point)this.points.get(this.faces[i][2])).ret_y())
				+((double)3/(double)8)*
				(((point)this.points.get(this.faces[i][3])).ret_y()+
				((point)this.points.get(this.faces[i][1])).ret_y());

				tz =((double)1/(double)8)*
				(p.ret_z() + ((point)this.points.get(this.faces[i][2])).ret_z())
				+((double)3/(double)8)*
				(((point)this.points.get(this.faces[i][3])).ret_z()+
				((point)this.points.get(this.faces[i][1])).ret_z());
				newpoints.add(new point(tx,ty,tz));
				edjes[i][3]=cnt;

				int[] pos = find_point_pos(this.faces[i][1],this.faces[i][3]);
				edjes[pos[0]][pos[1]]=cnt;
				cnt++;
			}
		}
		//System.out.println("cnt here" +cnt);
		//get vertext points...
		/*for(int i = 0;i<this.num_of_faces;i++)
			System.out.println("\t"+edjes[i][1] +"\t"+edjes[i][2] + "\t"+ edjes[i][3]);*/

		for(int i =0;i<this.num_of_points;i++)
		{
			double tx=0.0;
			double ty=0.0;
			double tz=0.0;

			int valence =0;
			for(int j =0;j<this.num_of_faces;j++)
			{
				if(this.faces[j][1]==i)
				{
					valence++;
					tx = tx + ((point)this.points.get(this.faces[j][2])).ret_x();
					ty = ty + ((point)this.points.get(this.faces[j][2])).ret_y();
					tz = tz + ((point)this.points.get(this.faces[j][2])).ret_z();
				}
				if(this.faces[j][2]==i)
				{
					valence++;
					tx = tx + ((point)this.points.get(this.faces[j][3])).ret_x();
					ty = ty + ((point)this.points.get(this.faces[j][3])).ret_y();
					tz = tz + ((point)this.points.get(this.faces[j][3])).ret_z();
				}
				if(this.faces[j][3]==i)
				{
					valence++;
					tx = tx + ((point)this.points.get(this.faces[j][1])).ret_x();
					ty = ty + ((point)this.points.get(this.faces[j][1])).ret_y();
					tz = tz + ((point)this.points.get(this.faces[j][1])).ret_z();
				}

			}
			//System.out.println("valence \t"+valence);
			omega = alpha(valence);
			/*tx = tx*((double)1 - omega);
			ty = ty*((double)1 - omega);
			tz = tz*((double)1 - omega);*/
			tx = tx*omega/(double)valence;
			ty = ty*omega/(double)valence;
			tz = tz*omega/(double)valence;
			//System.out.println("omega..." + omega);
			tx = tx+(((double)1 - omega)*((point)points.get(i)).ret_x());
			ty = ty+(((double)1 - omega)*((point)points.get(i)).ret_y());
			tz = tz+(((double)1 - omega)*((point)points.get(i)).ret_z());
			//System.out.println(tx+ "   " + ty + "  " + tz);
			newpoints.add(new point(tx,ty,tz));
		}

		newfaces = new int[this.num_of_faces * 4][4];
		System.out.println("cnt here too" +cnt);
	/*	for(int i = 0;i<this.num_of_faces;i++)
			System.out.println("\t"+this.faces[i][1] +"\t"+this.faces[i][2] + "\t"+ this.faces[i][3]);*/


		int faceindex=0;
		for(int i = 0;i<this.num_of_faces;i++)
		{
			newfaces[faceindex][0]=3;
			newfaces[faceindex][1]=edjes[i][1];
			newfaces[faceindex][2]=edjes[i][2];
			newfaces[faceindex][3]=edjes[i][3];
			faceindex++;

			newfaces[faceindex][0]=3;
			newfaces[faceindex][1]=edjes[i][3];
			newfaces[faceindex][2]=this.num_of_edjes +this.faces[i][1];
			newfaces[faceindex][3]=edjes[i][1];
			faceindex++;

			newfaces[faceindex][0]=3;
			newfaces[faceindex][1]=edjes[i][1];
			newfaces[faceindex][2]=this.num_of_edjes +this.faces[i][2];
			newfaces[faceindex][3]=edjes[i][2];
			faceindex++;

			newfaces[faceindex][0]=3;
			newfaces[faceindex][1]=edjes[i][2];
			newfaces[faceindex][2]=this.num_of_edjes +this.faces[i][3];
			newfaces[faceindex][3]=edjes[i][3];
			faceindex++;



		}
		object ret = new object(newpoints.size(),this.num_of_faces * 4,0,newpoints,newfaces);
		return ret;
	}

/**********************************************/

	public void save(String Filename)
	{
		//save into off file....
		if(this.formed)System.out.println("already saved...");
		else
		{
			try{
				FileOutputStream fos = new FileOutputStream (Filename);

				PrintStream pr =new PrintStream((OutputStream)fos);
				edjenumber();
				pr.println("OFF");
				pr.print(this.num_of_points +" " + this.num_of_faces + " " + this.num_of_edjes);
				pr.println();
				for(int i=0;i<this.num_of_points;i++)
				{
					double x = ((point)this.points.get(i)).ret_x();
					double y = ((point)this.points.get(i)).ret_y();
					double z = ((point)this.points.get(i)).ret_z();
					pr.println(x +" " + y + " " + z);
				}
				for(int i=0;i<this.num_of_faces;i++)
				{

					for(int j=0;j<=this.faces[i][0];j++)
					{
						pr.print( this.faces[i][j] +" ");
					}
					pr.println();
				}
				pr.close();
				this.formed =true;
			}

			catch(Exception e){e.printStackTrace();}
		}

	}

/**********************************************/
	public void plot_object(Graphics2D g2)
	{
		//boundingbox();
		double min;
		double trans  = (double)300;
		if((max_x -min_x)>(max_y -min_y)) min =(max_x -min_x);
		else min =(max_y -min_y);
		g2.setPaint(Color.black);
		g2.translate(300,300);
		//g2.rotate(3.1415926535);
		double scale = (double)200/min;
		for(int i=0;i<this.num_of_faces;i++)
		{
			for(int j=1;j<this.faces[i][0];j++)
			{
				//double x1 = trans + get_x(this.faces[i][j])* scale;
				//double y1 = get_y(this.faces[i][j])* scale + trans;
				//double x2 = trans + get_x(this.faces[i][j+1])* scale;
				//double y2 = get_y(this.faces[i][j+1])* scale + trans;
				double x1 = get_x(this.faces[i][j])* scale;
				double y1 = get_y(this.faces[i][j])* scale;
				double x2 = get_x(this.faces[i][j+1])* scale;
				double y2 = get_y(this.faces[i][j+1])* scale;
				Line2D l =  new Line2D.Double(x1,y1,x2,y2);
				g2.draw(l);
			}
			double x1 = get_x(this.faces[i][this.faces[i][0]]) * scale;
			double y1 = get_y(this.faces[i][this.faces[i][0]])* scale ;
			double x2 = get_x(this.faces[i][1])* scale;
			double y2 = get_y(this.faces[i][1])* scale;
			Line2D l =  new Line2D.Double(x1,y1,x2,y2);
			g2.draw(l);

		}

	}
	/**********************************************/




	public static void main(String args[]) throws Exception
	{
		String Filename1 = args[0];
		String s = " 2                  0                1";
		final object o = new object(Filename1);
		ArrayList arr = o.split_string(s);
		//for(int i = 0;i<arr.size();i++)
		//{
		//	System.out.println((String)arr.get(i));
		//}
		object newobject=o.loop();
		newobject.save("tet1.off");


	}
	/**********************************************/
}
