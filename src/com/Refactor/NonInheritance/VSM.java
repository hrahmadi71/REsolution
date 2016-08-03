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

package com.Refactor.NonInheritance;


import Jama.Matrix;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
public class VSM {
	DoubleMatrix2D  newAmatrix;

	double[][] s;
	double[][] u;
	double[][] v;
	
	//getLength:������ģ
	private double getLength( double[][] Vector )
	{
		double length = 0.0;
		for( int count=0; count<Vector.length; count++ )
		{
			length += Vector[count][0] * Vector[count][0];
		}
		return Math.sqrt( length );
	}

	//�������������ĽǶ�
	private double getAngle( double[][] tmpVector1, double[][] tmpVector2 )
	{
		double angle;
		if (getLength(tmpVector1) * getLength(tmpVector2) == 0)
			angle = 0;
		else
			angle = scalarProduct(tmpVector1, tmpVector2) / (getLength(tmpVector1) * getLength(tmpVector2));
		if(angle<0)
			angle=0;
		return angle;
	}

	//���
	private double scalarProduct( double[][] tmpVector1, double[][] tmpVector2 )
	{
		double scalar = 0.0;		
		for(int count =0;count<tmpVector1.length;count++)
			scalar += tmpVector1[count][0]*tmpVector2[count][0];
		return scalar;

	}
	
	public double[] gettwomax(double[][] s,int lie )
	{
		double[][] S=new double[lie][2];
		double[] twomax=new double[2];
		for(int i=0;i<lie;i++)
		{
			S[i][1]=s[i][i];
			S[i][0]=i;

		}
		 //ֱ�Ӳ������򣨴���λ���Ŵ�С)
        for (int i = 1; i < lie; i++)
        {
            //������Ԫ��
            double [] temp=new double[2];
            temp[1] = S[i][1];
            temp[0] = S[i][0];
            int j;
            for (j = i-1; j>=0; j--) 
            {
                //������temp�������ƶ�һλ
                if(S[j][1]<temp[1])
                {
                    S[j+1][1] = S[j][1];
                    S[j+1][0] = S[j][0];
                }
                else
                {
                    break;
                }
            }
            S[j+1][1] = temp[1];
            S[j+1][0] = temp[0];
        }
			twomax[0]=S[0][0];
			twomax[1]=S[1][0];
		return twomax;
		
		
		
	}

	
	//���� Uk:N*2
	public DoubleMatrix2D getUk(DoubleMatrix2D u, double[] twomax )
	{
		DoubleMatrix2D ukMatrix = new SparseDoubleMatrix2D(u.rows(),twomax.length);
		for(int i=0;i<u.rows();i++)
				ukMatrix.set(i,0, u.get(i, (int)twomax[0]));
		for(int i=0;i<u.rows();i++)
			ukMatrix.set(i,1, u.get(i, (int)twomax[1]));
		return ukMatrix;
		
	}

	//����Sk��2*2
		public DoubleMatrix2D getSk(DoubleMatrix2D s, double[] twomax )
		{
			DoubleMatrix2D skMatrix =new SparseDoubleMatrix2D(twomax.length,twomax.length);
				skMatrix.set(0,0, s.get((int) twomax[0], (int)twomax[0]));
				skMatrix.set(1,1, s.get((int) twomax[1], (int)twomax[1]));
			return skMatrix;
		}
		
	
	//���� Vk��2*N
	public DoubleMatrix2D getVk( DoubleMatrix2D v,double[] twomax )
	{
		DoubleMatrix2D vkMatrix = new SparseDoubleMatrix2D(twomax.length,v.rows());
		for(int i=0;i<v.rows();i++)
			vkMatrix.set(0,i, v.get((int) twomax[0],i));
		for(int i=0;i<v.rows();i++)
		vkMatrix.set(1,i, v.get((int) twomax[1],i));
		return vkMatrix;

		
	}


	/*
	 * newAmatrix:N*P
	 * ���У�u2��N*N   s2:N*P   vT2:vת��  P*P
	 */
	public DoubleMatrix2D getnewA(DoubleMatrix2D u, DoubleMatrix2D s, DoubleMatrix2D v )
	{
		Algebra algebra = new Algebra();
		double[][] ss=new double[s.rows()][s.columns()];
		for(int i=0;i<s.rows();i++)
		{
			for(int j=0;j<s.columns();j++)
			{
				ss[i][j]=s.get(i, j);
			}
		}
		double[] twomax=gettwomax(ss,ss[0].length);
		DoubleMatrix2D tmpMatrix = algebra.mult( getUk(u,twomax), getSk(s,twomax) );
		newAmatrix=algebra.mult( tmpMatrix, getVk(v,twomax) );

		return newAmatrix;
	}	
	
	/**��Matrix�;���תΪDoubleMatrix2D��ʽ
	 * @param a
	 * @return
	 */
	private static DoubleMatrix2D getSparseDoubleMatrix(Matrix a) {
		// TODO Auto-generated method stub
		DoubleMatrix2D newA =new SparseDoubleMatrix2D(a.getRowDimension(), a.getColumnDimension());
		for(int i=0;i<a.getRowDimension();i++)
		{
			for(int j=0;j<a.getColumnDimension();j++)
			{
				newA.set(i, j, a.get(i, j));
			}
		}
		return newA;
	}

	//������ά�������ĵ�����
	public double[][] getDk( int t)
	{
		double[][] x=new double[newAmatrix.rows()][1];
		for(int i=0;i<newAmatrix.rows();i++)
			x[i][0]=newAmatrix.get(i, t);
		return x;
			
	}
	
	//������ʾ����ֵ:number �ĵ�����
	public double[] cosineMatrix( DoubleMatrix2D newA, int number) //aQueryΪTerm���ַ�����k  1
	{

		double[][] tmpVector1 = new double[newAmatrix.rows()][1];
		double[][] tmpVector2 = new double[newAmatrix.rows()][1];
		double[] tmpDocAngle = new double[newAmatrix.columns()];
	
			tmpVector1=getDk(number);        
		for( int count=0; count<newAmatrix.columns(); count++ )//lie
		{						
						
				tmpVector2= getDk(count);

			
			tmpDocAngle[count] = getAngle(tmpVector1, tmpVector2);

		}

		return tmpDocAngle;
	}
		


}
