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

package com.neu.utils.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.dom4j.DocumentException;

import com.Refactor.Inheritance.ConnComp;
import com.Refactor.NonInheritance.MergeMethodNonInheri;
import com.Refactor.NonInheritance.preprocessing;
import com.jeantessier.dependencyfinder.gui.SrcAction;
import com.neu.utils.data.Edge;
import com.neu.utils.data.Node;
import com.neu.utils.io.NetworkToXMLUtil;

/**
 * @author revo
 *
 */
public class CMDNUtil {
	
	static Map<String,Node> map = new HashMap<String, Node>();
	static List<Node> nodelist = new ArrayList<Node>();
	static List<Edge> edgelist = new ArrayList<Edge>();
	
	public static void matrixToXML(){
		
		hibernateMatrixToXML(preprocessing.extendsMatrix, Node.NODE_TYPE_INHERITANCE);
		hibernateMatrixToXML(preprocessing.dependencyMatrix, Node.NODE_TYPE_NON_INHERITANCE);
		
	}
	
	/**
	 * �õ���ͨƬ�������
	 * @param fileName
	 * @return
	 */
	public static List<List<String>> getUnicomFilm(String fileName) throws DocumentException{
		return NetworkToXMLUtil.unicomFilmXmlRead(fileName);
	}
	
	
	public static void hibernateMatrixToXML(int[][] matrix,String type){
		
		nodelist.clear();
		edgelist.clear();
		
		for (int i = 0; i < matrix.length; i++) {
			int[] js = matrix[i];
			Node node = new Node(i+1);
			nodelist.add(node);
			for (int j = 0; j < js.length; j++) {
				int isEdge = js[j];
				if(isEdge>0){
					Edge edge = new Edge(i+1, j+1);
					edgelist.add(edge);
				}
			}
			
		}
		
		//���ýڵ����ݱ�ǩ
		setNodesText(nodelist,SrcAction.classname);
		
		//�õ����е���ͨƬ������ String ������
		ArrayList<ArrayList<String>> cc = ConnComp.getcc(matrix.clone());
		
		// ������ͨƬ
		setConnected(cc,nodelist);
		
		//������ͨƬ�Ĵ�С��������
		cc = sortCCBySize(cc);
		
		nodelist = reSetNodeList(nodelist,cc);
		
		nodelist = getSimpleName(nodelist);
		
		NetworkToXMLUtil.xmlWrite(nodelist, edgelist, type);
		
	}
	
	private static List<Node> getSimpleName(List<Node> nodelist) {
		
		for (Node node : nodelist) {
			node.setText(node.getText().substring(node.getText().lastIndexOf(".")+1));
		}
		return nodelist;
	}
	
	/**
	 * @author revo
	 * @param nodelist
	 * @param cc
	 * @return
	 */
	private static List<Node> reSetNodeList(List<Node> nodelist,
			ArrayList<ArrayList<String>> cc) {
		
		nodelist.clear();
		
		for (ArrayList<String> list : cc) {
			for (String string : list) {
				nodelist.add(map.get(string));
			}
		}
		
		return nodelist;
	}

	/**
	 * @author revo
	 * @param cc
	 * @return
	 */
	private static ArrayList<ArrayList<String>> sortCCBySize(
			ArrayList<ArrayList<String>> cc) {
		
		Map<String,ArrayList<String>> ccmap = new HashMap<String,ArrayList<String>>();
		
		for (ArrayList<String> arrayList : cc) {
			ccmap.put(arrayList.size()+"_"+UUID.randomUUID().toString(), arrayList);
		}
		
		List<Map.Entry<String, ArrayList<String>>> list = sortMapByKey(ccmap);
		
		cc.clear();
		
		for (Entry<String, ArrayList<String>> entry : list) {
			cc.add(entry.getValue());
			
		}
		
		return cc;
	}

	
	/**
	 * ʹ�� Map��key��������
	 * @author revo
	 * @param map
	 * @return
	 */
	/**
	 * ����ֵ��������map��������һ������
	 */
	public static List<Entry<String, ArrayList<String>>> sortMapByKey(Map<String, ArrayList<String>> map){
		//����
		//���ｫmap.entrySet()ת����list
		List<Map.Entry<String, ArrayList<String>>> list = new ArrayList<Map.Entry<String, ArrayList<String>>>(map.entrySet());
		//Ȼ��ͨ���Ƚ�����ʵ������
		Collections.sort(list,new Comparator<Map.Entry<String, ArrayList<String>>>() {
			//��������
			public int compare(Entry<String, ArrayList<String>> o1,
					Entry<String, ArrayList<String>> o2) {
				
				Integer o1value = Integer.parseInt(o1.getKey().split("_")[0]);
				Integer o2value = Integer.parseInt(o2.getKey().split("_")[0]);
				
				return o2value.compareTo(o1value);
			}
			
		});
		return list;
	}
	
	/**
	 * @author revo
	 * @param cc
	 * @param nodelist
	 */
	private static void setConnected(ArrayList<ArrayList<String>> cc,
			List<Node> nodelist) {
		
		int count=0;
		
		for (ArrayList<String> list : cc) {
			for (String string : list) {
				count++;
				for (Node node : nodelist) {
					if (string.equals(node.getText())) {
						map.put(string, node);
					}
				}
			}
			
		}
		
		
	}

	/**
	 * @author revo
	 * @param nodelist
	 * @param classname
	 */
	private static void setNodesText(List<Node> nodelist,
			ArrayList<String> classname) {
		
		//�˴�֮������������ ����Ϊclassname ��nodelist ���Ԫ��˳����һһ��Ӧ��
		for (int i = 0; i < classname.size(); i++) {
			String fillName = classname.get(i);
			//String name = fillName.substring(fillName.lastIndexOf(".")+1);
			
			Node node = nodelist.get(i);
			node.setText(fillName);
		}
	}

	/**
	 * �־û��������б�xml�ļ�
	 * @author revo
	 * @param mergeSetList
	 */

	public static void hibernateMethodListToXmlWrite(List<List<String>> list) {
		
		NetworkToXMLUtil.unicomFilmXmlWrite(list);
	}
	

}
