/**
 * 
 */
package Algoritmos;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import Dominio.Actions;
import Dominio.Estado;
import Dominio.ProblemDefinition;

/**
 * @author Ignasi
 *
 */
public abstract class algoritmo {
	ProblemDefinition problem;
	//public HashMap<String, Float> V = new HashMap<String, Float>();
	//public HashMap<String, List<Float>> VLao = new HashMap<String, List<Float>>();
	protected float epsilon = 0.0000001f;
	protected int nMax = 10;
	protected int initIndex = 0;
	long timeStart;

	public abstract float qValue(Actions a);
	
	public abstract float residual(Estado s);
	
	public abstract void Update(Estado s);

	public abstract Actions greedyAction(Estado s);
	
	protected Actions argMin(Actions[] a){
		int min = 0;
		for(int i=1;i<a.length;i++){
			if(a[i] != null){
				if(a[i].q < a[min].q){
					min = i;
				}
			}			
		}
		return a[min];
	}
	
	protected Actions argMax(Actions[] a){
		int max = 0;				
		for(int i=1;i<a.length;i++){
			if(a[i] != null){
				if(a[i].q > a[max].q){
					max = i;
				}
			}			
		}
		return a[max];
	}
	
	protected float maxValue(float[] arrayFloat){
		int Max = 0;
		for(int i = 1; i< arrayFloat.length; i++){
				if(arrayFloat[i] > arrayFloat[Max]){
					Max = i;
				}
		}
		return arrayFloat[Max];
	}
	
	public abstract Estado pickNextState(Actions a);
	
	public abstract void initHash(ProblemDefinition p);
	
}
