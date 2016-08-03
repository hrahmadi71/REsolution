/*
 * ***************************************************
 * REsolution is an automatic software refactoring tool      
 * ***************************************************
 *  Copyright (c) 2016, Wang Ying, Yin Hongjian
 *  E-mail: wangying8052@163.com
 *  All rights reserved.
 *
 * This file is part of REsolution.
 *
 * REsolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * REsolution is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with REsolution.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.neu.utils.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.Refactor.Inheritance.extend;
import com.neu.utils.data.*;

 /**
 * 
 * @author revo
 *
 */
public class TreeJPanel extends JPanel implements MouseListener{
	
	private int TOP_BORDER = 10;
	private int BOTTOM_BORDER = (int)(this.getHeight()*0.05);
	private Map<DotType, Color> colors = new HashMap<DotType,Color>();
	
	List<ClassNode> classNodes;
	List<Line> lines;
	List<Line> dottedLines;
	List<ArrayList<extend>> tree;
	
	public TreeJPanel() {
		init();
	}

	public TreeJPanel(List<ArrayList<extend>> tree){
		this.tree = tree;
		init();
	}

	/**
	 * ��ʼ����������õ�����ɫ�Լ����ı���ɫ
	 */
	private void init() {
		colors.put(DotType.Intertace, Color.red);
		colors.put(DotType.NewClass, Color.blue);
		colors.put(DotType.OldClass, Color.green);
		colors.put(DotType.Alone, Color.yellow);
		this.setBackground(Color.white);
		
		final JComponent c= this;
		c.addMouseMotionListener(new MouseMotionListener() {
			
			public void mouseMoved(MouseEvent e) {
				Point point = e.getPoint();
                int x = (int)point.getX();
        		int y = (int)point.getY();
        		
        		for (ClassNode node : classNodes) {
        			if (x>=node.getX() && x<=(node.getX()+node.getWidth()) && 
        					y>=node.getY() && y<=(node.getY()+node.getHeight())) {
        				JPopupMenu popup = new JPopupMenu();  
                        popup.setLayout(new BorderLayout());  
                        JPanel infoPanel = createtInfoPanel(node.getName());  
                        popup.add(infoPanel, BorderLayout.CENTER);  

                        popup.show(c, x+5, y+5);
        			}
        		}
			}
			
			public void mouseDragged(MouseEvent e) {}
		});
		
		
	}

	private JPanel createtInfoPanel(String content) {
		JPanel infoPanel = new JPanel();   
        infoPanel.setLayout(new BorderLayout());  

        infoPanel.add(new JLabel(content),BorderLayout.CENTER);  
        return infoPanel;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		drawTree(g,tree);
		drawLegend(g);
	}

	/**
	 * ����
	 * @param g
	 * @param tree
	 */
	private void drawTree(Graphics g,List<ArrayList<extend>> tree) {
		
		drawNode(g, tree);
		drawRelationShip(g, classNodes);
		drawDependency(g, classNodes);
//		drawLeble(g, classNodes);
	}

	
	/**
	 * ����ǩ˵��
	 * @param g
	 * @param classNodes
	 */
	private void drawLeble(Graphics g, List<ClassNode> classNodes) {
		
		for (ClassNode classNode : classNodes) {
			g.drawString(classNode.getName(), 
					(int)(classNode.x+classNode.width*1.5),(int)(classNode.y+classNode.height*0.5));
		}
		
	}

	/**
	 * ��������ϵ�����ߵļ�ͷ
	 * @param g
	 * @param classNodes2
	 */
	private void drawDependency(Graphics g, List<ClassNode> classNodes) {
		
		dottedLines = new ArrayList<Line>();
		
		for (int i = 0; i < classNodes.size(); i++) {
			ClassNode node= classNodes.get(i);
			List<String> OutdependencyNames = node.getOutdependencyList();
			for (String name : OutdependencyNames) {
				for (ClassNode nodeTemp : classNodes) {
					if (nodeTemp.name.equals(name)) {
						Line line = new Line(node, nodeTemp);
						dottedLines.add(line);
					}
				}
			}
		}

		g.setColor(new Color(102,46,180));
		for (Line line : dottedLines) {
			drawAL(line.getsDot().x+10, line.getsDot().y+10, line.gettDot().x+10, line.gettDot().y+10, (Graphics2D)g,1);
		}
		g.setColor(Color.black);
	}
	
	/**
	 * ���̳й�ϵ�ļ�ͷ
	 * @param g
	 * @param classNodes
	 */
	private void drawRelationShip(Graphics g, List<ClassNode> classNodes) {
		lines = new ArrayList<Line>();
		for (int i = 0; i < classNodes.size(); i++) {
			ClassNode node= classNodes.get(i);
			List<String> names = node.getParentList();
			for (String name : names) {
				for (ClassNode nodeTemp : classNodes) {
					if (nodeTemp.name.equals(name)) {
						Line line = new Line(node, nodeTemp);
						lines.add(line);
					}
				}
			}
		}

		g.setColor(new Color(102,76,80));
		for (Line line : lines) {
			drawAL(line.getsDot().x+10, line.getsDot().y, line.gettDot().x+10, line.gettDot().y+20, (Graphics2D)g,0);
		}
		g.setColor(Color.black);
	}


	/**
	 * ���ڵ�
	 * @param g
	 * @param tree
	 */
	private void drawNode(Graphics g, List<ArrayList<extend>> tree) {
		classNodes = new ArrayList<ClassNode>();
		for (int i = 0; i < tree.size(); i++) {
			List<extend> levelNode = tree.get(i);
			classNodes.addAll(calculateNodeLocation(levelNode,i));
		}
		g.setColor(new Color(10,111,40));
		for (ClassNode node : classNodes) {
			
			g.setColor(colors.get(node.getType()));
			g.fillOval(node.x, node.y, node.width, node.height);
		}
		g.setColor(Color.black);
	}

	/**
	 * ����ڵ��λ��
	 * @param levelNode
	 * @param level
	 * @return
	 */
	private List<ClassNode> calculateNodeLocation(List<extend> levelNode, int level) {
		List<ClassNode> classNodes = new ArrayList<ClassNode>();
		int columnPX = getColumnPx(levelNode);
		int levelPx = getLevelPx(tree);
		
//		int step = (int)((levelPx+0.0f)/levelNode.size()+0.5);
		
		for (int i = 0; i < levelNode.size(); i++) {
//			ClassNode node = new ClassNode(columnPX*(i+1), TOP_BORDER+level*levelPx+i*step, 20, 20);
			ClassNode node = new ClassNode(columnPX*(i+1), TOP_BORDER+level*levelPx, 20, 20);
			node.setName(levelNode.get(i).TreenodeName);
			node.setParentList(levelNode.get(i).SuperClassName);
			
			node.setType(getType(levelNode.get(i)));
			node.setOutdependencyList(levelNode.get(i).OutdependencyClassName);
			node.setCns(levelNode.get(i).cns);
			
			classNodes.add(node);
		}
		return classNodes;
	}

	/**
	 * �жϽڵ������
	 * @param ext
	 * @return
	 */
	private DotType getType(extend ext) {
		
		if (ext.interfaceornot == true) {
			return DotType.Intertace;
		} else if (ext.split == true) {
			return DotType.NewClass;
		}else if (ext.SubClassName.size() == 0 && ext.SuperClassName.size() == 0) {
			return DotType.Alone;
		}
		
		return DotType.OldClass;
	}

	/**
	 * ����һ��ڵ�Ŀ�Ȳ���
	 * @param levelNode
	 * @return
	 */
	private int getColumnPx(List<extend> levelNode) {
		 return this.getWidth() / (levelNode.size()+1);
	}

	/**
	 * ����ÿһ��ڵ�ĸ߶Ȳ���
	 * @param tree
	 * @return
	 */
	private int getLevelPx(List<ArrayList<extend>> tree) {
		 return (this.getHeight()-TOP_BORDER-BOTTOM_BORDER) / tree.size();
	}

	/**
	 * ��ͼ��
	 * @param g
	 */
	private void drawLegend(Graphics g) {
		
		Graphics2D g2 = (Graphics2D)g;
		g2.drawRect((int)(this.getWidth()*0.85), 10, (int)(this.getWidth()*0.145), 150);
		g2.setFont(new Font("Times New Roman", 1, 14));
		
		this.drawAL((int)(this.getWidth()*0.86), 35, (int)(this.getWidth()*0.91), 35, g2,0);
		g2.drawString("Inheritance", (int)(this.getWidth()*0.912), 40);
		
		g2.setColor(colors.get(DotType.Intertace));
		g2.fillOval((int)(this.getWidth()*0.86), 60, 15, 15);
		g2.setColor(Color.black);
		g2.drawString("Interface", (int)(this.getWidth()*0.88), 72);

		g2.setColor(colors.get(DotType.NewClass));
		g2.fillOval((int)(this.getWidth()*0.86), 90, 15, 15);
		g2.setColor(Color.black);
		g2.drawString("Classes should be split", (int)(this.getWidth()*0.88), 102);
		
		g2.setColor(colors.get(DotType.OldClass));
		g2.fillOval((int)(this.getWidth()*0.86), 120, 15, 15);
		g2.setColor(Color.black);
		g2.drawString("Well-designed classes", (int)(this.getWidth()*0.88), 132);
				
	}
	
	/**
	 * @author revo
	 * @param sx ��ʼ���x����
	 * @param sy ��ʼ���y����
	 * @param ex �������x����
	 * @param ey �������y����
	 * @param g2 ����
	 * @param type 0 ����Ĭ�Ϸ��1���� ���߷��ĵ���ͷ����
	 */
	private void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2,int type) {

		double H = 10; // ��ͷ�߶�
		double L = 4; // �ױߵ�һ��
		int x3 = 0;
		int y3 = 0;
		int x4 = 0;
		int y4 = 0;
		double awrad = Math.atan(L / H); // ��ͷ�Ƕ�
		double arraow_len = Math.sqrt(L * L + H * H); // ��ͷ�ĳ���
		double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
		double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
		double x_3 = ex - arrXY_1[0]; // (x3,y3)�ǵ�һ�˵�
		double y_3 = ey - arrXY_1[1];
		double x_4 = ex - arrXY_2[0]; // (x4,y4)�ǵڶ��˵�
		double y_4 = ey - arrXY_2[1];

		Double X3 = new Double(x_3);
		x3 = X3.intValue();
		Double Y3 = new Double(y_3);
		y3 = Y3.intValue();
		Double X4 = new Double(x_4);
		x4 = X4.intValue();
		Double Y4 = new Double(y_4);
		y4 = Y4.intValue();
		// ����
		if (type == 1) {
			Stroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{12, 12}, 0);
			g2.setStroke(stroke);
		}
		g2.drawLine(sx, sy, ex, ey);
		//
		GeneralPath triangle = new GeneralPath();
		triangle.moveTo(ex, ey);
		triangle.lineTo(x3, y3);
		triangle.lineTo(x4, y4);
		triangle.closePath();
		// ʵ�ļ�ͷ
		g2.fill(triangle);
		// ��ʵ�ļ�ͷ
		// g2.draw(triangle);

	}

	/**
	 *  �����ͷ
	 * @param px
	 * @param py
	 * @param ang
	 * @param isChLen
	 * @param newLen
	 * @return
	 */
	public static double[] rotateVec(int px, int py, double ang,
			boolean isChLen, double newLen) {
		double mathstr[] = new double[2];
		// ʸ����ת��������������ֱ���x������y��������ת�ǡ��Ƿ�ı䳤�ȡ��³���
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		if (isChLen) {
			double d = Math.sqrt(vx * vx + vy * vy);
			vx = vx / d * newLen;
			vy = vy / d * newLen;
			mathstr[0] = vx;
			mathstr[1] = vy;
		}
		return mathstr;
	}

	public void mouseClicked(MouseEvent e) {
		
		if (e.getClickCount() == 2) {			
			int x = e.getX();
			int y = e.getY();
			
			for (final ClassNode node : classNodes) {
				if (x>=node.getX() && x<=(node.getX()+node.getWidth()) && 
						y>=node.getY() && y<=(node.getY()+node.getHeight())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							
							System.out.println("run....");
							
							new ClassContent(node.getCns(),node.getName());
						}
					});
				}
			}
		}
		
	}
	
	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO �Զ����ɵķ������
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO �Զ����ɵķ������
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO �Զ����ɵķ������
		
	}
	
}



