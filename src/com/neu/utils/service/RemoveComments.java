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
/**
 * ���������ȥ��ԭ�ļ���ע��
 *  author ��˱�
 *  update LEEO date :2013-04-10
 */
import java.io.IOException;
import java.io.Reader;

public class RemoveComments{

	// COMMENCODESΪ��ͨ����ģʽ,PRECOMMENTSΪб��ģʽ,MORELINECOMMENTSΪ����ע��ģʽ,
	// STARMODELΪ����ע�����Ǻ�ģʽ��SINGLELINECOMMENTSΪ����ע��ģʽ��STRINGMODELΪ�ַ���ģʽ��
	// TRANSFERMODELΪ�ַ���ת��ģʽ
	private enum model {
		COMMENCODES, PRECOMMENTS, MORELINECOMMENTS, STARMODEL, SINGLELINECOMMENTS, STRINGMODEL, TRANSFERMODEL
	}
	
	//stats��¼״̬
	private model stats = model.COMMENCODES;	

	public String remove(Reader in) throws IOException {
		StringBuilder s = new StringBuilder();
		int n;
		while ((n = in.read()) != -1) {
			switch ((char) n) {
			case '/':
				if (stats == model.COMMENCODES) {// �����ǰλ��ͨ����ģʽ��ת��б��ģʽ
					stats = model.PRECOMMENTS;
				} else if (stats == model.PRECOMMENTS) {// �����ǰΪб��ģʽ��ת������ע��ģʽ
					stats = model.SINGLELINECOMMENTS;
					s.append("  ");
				} else if (stats == model.MORELINECOMMENTS) {//
					s.append(" ");
				} else if (stats == model.STARMODEL) {// �����ǰΪ�Ǻ�ģʽ��ת����ͨ����ģʽ
					stats = model.COMMENCODES;
					s.append(" ");
				} else if (stats == model.SINGLELINECOMMENTS) {
					s.append(" ");
				} else if (stats == model.STRINGMODEL) {
					s.append("/");
				} else if (stats == model.TRANSFERMODEL) {
					stats = model.STRINGMODEL;
					s.append("/");
				}
				break;
			case '*':
				if (stats == model.COMMENCODES) {
					s.append("*");
				} else if (stats == model.PRECOMMENTS) {// ���Ϊб��ģʽ��ת������ע��ģʽ
					stats = model.MORELINECOMMENTS;
					s.append("  ");
				} else if (stats == model.MORELINECOMMENTS) {// �����ǰΪ����ע��ģʽ��ת���Ǻ�ģʽ
					stats = model.STARMODEL;
					s.append(" ");
				} else if (stats == model.STARMODEL) {
					s.append(" ");
				} else if (stats == model.SINGLELINECOMMENTS) {
					s.append(" ");
				} else if (stats == model.STRINGMODEL) {
					s.append("*");
				} else if (stats == model.TRANSFERMODEL) {
					s.append("*");
				}
				break;
			case '"':
				if (stats == model.COMMENCODES) {// �����ǰΪ��ͨ����ģʽ��ת���ַ���ģʽ
					stats = model.STRINGMODEL;
					s.append("\"");
				} else if (stats == model.PRECOMMENTS) {// �����ǰΪб��ģʽ��ת����ͨ����ģʽ
					stats = model.COMMENCODES;
					s.append("/\"");
				} else if (stats == model.STARMODEL) {// �����ǰΪ�Ǻ�ģʽ��ת������ע��ģʽ
					stats = model.MORELINECOMMENTS;
					s.append(" ");
				} else if (stats == model.SINGLELINECOMMENTS) {
					s.append(" ");
				} else if (stats == model.STRINGMODEL) {// �����ǰΪ�ַ���ģʽ��ת����ͨ����ģʽ
					stats = model.COMMENCODES;
					s.append("\"");
				} else if (stats == model.TRANSFERMODEL) {// �����ǰΪת��ģʽ��ת���ַ�����ʽ
					stats = model.STRINGMODEL;
					s.append("\"");
				}
				break;
			case '\\':
				if (stats == model.COMMENCODES) {
					s.append("\\");
				} else if (stats == model.PRECOMMENTS) {// �����ǰΪб��ģʽ��ת����ͨ�����ʽ
					stats = model.COMMENCODES;
					s.append("/\\");
				} else if (stats == model.MORELINECOMMENTS) {
					s.append(" ");
				} else if (stats == model.STARMODEL) {// �����ǰΪ�Ǻ�ģʽ��ת������ע��ģʽ
					stats = model.MORELINECOMMENTS;
					s.append(" ");
				} else if (stats == model.SINGLELINECOMMENTS) {
					s.append(" ");
				} else if (stats == model.STRINGMODEL) {// �����ǰΪ�ַ���ģʽ��ת���ַ���ת��ģʽ
					stats = model.TRANSFERMODEL;
					s.append("\\");
				} else if (stats == model.TRANSFERMODEL) {// �����ǰΪ�ַ���ת��ģʽ��ת���ַ���ģʽ
					stats = model.STRINGMODEL;
					s.append("\\");
				}
				break;
			case '\n':
				if (stats == model.COMMENCODES) {
					s.append("\n");
				} else if (stats == model.PRECOMMENTS) {// �����ǰΪб��ģʽ��ת����ͨ�����ʽ
					stats = model.COMMENCODES;
					s.append("/\n");
				} else if (stats == model.MORELINECOMMENTS) {
					s.append("\n");
				} else if (stats == model.STARMODEL) {// �����ǰΪ�Ǻ�ģʽ��ת������ע��ģʽ
					stats = model.MORELINECOMMENTS;
					s.append("\n");
				} else if (stats == model.SINGLELINECOMMENTS) {// �����ǰΪ����ע��ģʽ��ת����ͨ�����ʽ
					stats = model.COMMENCODES;
					s.append("\n");
				} else if (stats == model.STRINGMODEL) {
					s.append("\n");
				} else if (stats == model.TRANSFERMODEL) {
					s.append("\\n");
				}
				break;
			default:
				if (stats == model.COMMENCODES) {
					s.append((char) n);
				} else if (stats == model.PRECOMMENTS) {// �����ǰΪб��ģʽ��ת����ͨ�����ʽ
					stats = model.COMMENCODES;
					s.append("/" + (char) n);
				} else if (stats == model.STARMODEL) {// �����ǰΪ�Ǻ�ģʽ��ת������ע��ģʽ
					stats = model.MORELINECOMMENTS;
					s.append(" ");
				} else if (stats == model.SINGLELINECOMMENTS) {
					s.append(" ");
				} else if (stats == model.STRINGMODEL) {
					s.append((char) n);
				} else if (stats == model.TRANSFERMODEL) {// �����ǰΪ�ַ���ת��ģʽ��ת���ַ���ģʽ
					stats = model.STRINGMODEL;
					s.append((char) n);
				}
				break;
			}
		}

		String result = s.toString();
//		System.out.println(result);
		return result;
	}
}
