/**
 * 
 */
package Algoritmos;

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import Dominio.Actions;
import Dominio.Estado;
import Dominio.ProblemDefinition;

/**
 * @author Ignasi
 *
 */
public class LRTDP extends algoritmo{
	
	public HashMap<String, Float> V = new HashMap<String, Float>();
	public LRTDP(ProblemDefinition p){
		this.problem = p;
		//Em caso de nao ter estado inicial nem final definido, definir e descomentar as seguintes linhas
		//p._sEstados[2].isSolved = true;
		
		initHash(p);
		this.initIndex = p.getIndexEstadoInicial();
		this.timeStart = System.currentTimeMillis();
		if(this.initIndex >= 0){
			while(!p._sEstados[this.initIndex].isSolved){
				lrtdpTrial(p._sEstados[this.initIndex]);	
			}
			for(int i = 0; i<p._sEstados.length;i++){
				if(p._sEstados[i] != null){
					Actions a = greedyAction(p._sEstados[i]);
					System.out.println("Action(" + p._sEstados[i]._sEstado + "): " + a._sActionName + " V(" + p._sEstados[i]._sEstado + "): " + V.get(p._sEstados[i]._sEstado));
				}			
			}
		}else{
			System.out.println("Nao existem estados iniciais. Programa concluido.");
		}		
	}
	
	public void lrtdpTrial(Estado s){
		int iter = 1000;
		//float residual = 0;
		//String auxString;
		//Init visited stack 
		Stack<Estado> visited = new Stack<Estado>();
		while((!s.isSolved) && (visited.size()<50)){
			// 
			//Insert into visited
			s.isVisited=true;
			visited.push(s);			
			//Check termination and goal states
			if(s.isGoal){
				break;
			}
			//pick best action and update hash
			//shuffleActions(s._sActions);
			Actions a = greedyAction(s);
			Update(s);
			//Stochastically simulate next state
			s.greedyAction = a;
			//auxString = s._sEstado;
			//Print the updated values of V[initState]
			if(s.isInitial){
				plotValues();
			}
			s = pickNextState(a);
			//System.out.println("Estado: " + auxString + " Action taken: " + a._sActionName + " Estado novo: " + s._sEstado);	
			//System.out.println("Value V(" + s._sEstado + "): " + V.get(s._sEstado));
			iter--;
		}
		//try labelling visited states in reverse order
		while(!visited.isEmpty()){
			s = visited.pop();
			if(!checkSolved(s)){
				break;
			}
		}
	}
	
	public float qValue(Actions a){
		float v = 0;
		float sum = 0;
		//Formula de Bellman
		try {			
			for(int i=0;i<a._sEstadoFinal.length;i++){
				if(a._sEstadoFinal[i] != null){
					if(a._sProbabilidad[i] > 0){
						sum += a._sProbabilidad[i] * V.get(a._sEstadoFinal[i]);
					}					
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error durante execuçao durante o calculo do qValue: " + e.toString());
		}
		//
		v = problem.getEstado(a._sEstadoInicial)._sReward - a._sCost + problem.discountFactor * sum;
		//System.out.println("Action: " + a._sActionName);
		return v;
	}

	public float residual(Estado s){
		Actions a = greedyAction(s);
		return V.get(s._sEstado)-qValue(a);
	}

	public void Update(Estado s){
		Actions a = greedyAction(s);
		float val = qValue(a);
		s.greedyAction = a;
		//System.out.println("Value V(" + s._sEstado + "): " + val);
		V.put(s._sEstado, val);
	}

	public Actions greedyAction(Estado s){
		//Actions[] listAction = new Actions[problem.nMax];
		for(int i=0;i<s._sActions.length;i++){
			if(s._sActions[i] != null){
				s._sActions[i].q = qValue(s._sActions[i]);
			}
		}
		//System.out.println("State: " + s._sEstado);
		return argMax(s._sActions);
	}

	public Estado pickNextState(Actions a){
		Random r = new Random();
		float numeroR = r.nextFloat();
		int j=0;
		for(int i =0;i<a._sEstadoFinal.length;i++){
			j = i;
			if(a._sEstadoFinal[i] != null){
				if(numeroR<=a._sProbabilidad[i]){
					return problem.getEstado(a._sEstadoFinal[i]);
				}
			}
		}
		return problem.getEstado(a._sEstadoFinal[j-1]);
	}

	public void initHash(ProblemDefinition p){
		//float r;
		//float[] arrayFloat = new float[p.getMaxActions()];
		//float[] maxFloatArray = new float[p._sEstados.length];
		//int indexFloat=0;
		for(int i=0;i<p._sEstados.length;i++){
			if(p._sEstados[i] != null){
				/*for(int indexAction = 0; indexAction < p._sEstados[i]._sActions.length; indexAction++){
					if(p._sEstados[i]._sActions[indexAction] != null){
						arrayFloat[indexAction] = p._sEstados[i]._sReward - p._sEstados[i]._sActions[indexAction]._sCost;
					}
				}
				//shuffleActions(p._sEstados[i]._sActions);
				r = maxValue(arrayFloat) * (1.0f/(1.0f-p.discountFactor));*/
				V.put(p._sEstados[i]._sEstado, 0.0f);
			}			
		}	
	}
	
	public boolean checkSolved(Estado s){
		Estado s_linha;
		boolean rv = true;
		Stack<Estado> open = new Stack<Estado>();
		Stack<Estado> closed = new Stack<Estado>();
		float auxFloat = 0;
		if(!s.isSolved){
			open.push(s);
		}
		while(!open.empty()){
			s = open.pop();
			closed.push(s);	
			//Check residual
			auxFloat = residual(s);
			if(auxFloat > epsilon){
				rv = false;
				continue;
			}
			//expand state
			Actions a = greedyAction(s);
			for(int index = 0;index<s._sActions.length;index++){
				if(s._sActions[index] != null){
					for(int j=0;j<s._sActions[index]._sEstadoFinal.length;j++){
						if(s._sActions[index]._sEstadoFinal[j] != null){
							s_linha = problem.getEstado(s._sActions[index]._sEstadoFinal[j]);
							if((!s_linha.isSolved) && (open.search(s_linha)<0) && (closed.search(s_linha)<0)){
								open.push(s_linha);
							}
						}
					}
				}
			}
		}
		if(rv){
			//label relevant states
			Enumeration enumera = closed.elements();
			while(enumera.hasMoreElements()){
				s_linha = (Estado)enumera.nextElement();
				s_linha.isSolved = true;
			}
		}
		else{
			//Update states with residuals and ancestors
			while(!closed.isEmpty()){
				s = closed.pop();
				Update(s);
			}
		}
		return rv;
	}
	
	private void plotValues(){
		//int init = this.problem.getIndexEstadoInicial();
		//System.out.println("Value V(" + this.problem._sEstados[init]._sEstado + "): " + V.get(this.problem._sEstados[init]._sEstado));		
		long timeFinish = System.currentTimeMillis();
		long timeAux = timeFinish-timeStart;		
		DecimalFormat df = new DecimalFormat("#.###");
		String timeDown=df.format(timeAux/1000.0);
		this.problem.outputString(timeDown + ";" + V.get(this.problem._sEstados[this.initIndex]._sEstado));
	}
}
