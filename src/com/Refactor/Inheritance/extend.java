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

package com.Refactor.Inheritance;
import java.util.ArrayList;


public class extend {
 public int matrixInd = 10000;//��classnamelist�е����
 public String TreenodeName;
 public ArrayList<Integer> SubClassInd = new  ArrayList<Integer>();//���������
 public ArrayList<Integer> SuperClassInd = new  ArrayList<Integer>();  //���������
 public ArrayList<String> SubClassName = new  ArrayList<String>();//���������
 public ArrayList<String> SuperClassName = new  ArrayList<String>();  //���������
 
 public ArrayList<String> OutdependencyClassName = new  ArrayList<String>();//������������������������
 
 public ArrayList<String> IndependencyClassName = new  ArrayList<String>();//������������������
 
 public int DrawlevelNo = 10000;//�ڿ��ӻ�ʱ������Ӧ���ڵĲ㼶
 
 public double DeltaQ = 0;
 public int move = 0;
 
 public boolean split = false; //�Ƿ񱻷ֽ���
 
 public boolean interfaceornot = false;   //�ǲ��ǽӿ�
 public ArrayList< ArrayList<String> > cns = new ArrayList< ArrayList<String> >();//�����������б�
 
 public boolean NOfuleibeifenjie = false;  //����ڵ��Ƿ񱻷ֽ�
 public int mainidx = 0;  //���̳������
 
 public NodeColor color;
}
