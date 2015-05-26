/**
 * 
 */
package Algoritmos;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import Dominio.Actions;
import Dominio.Estado;
import Dominio.ProblemDefinition;

/**
 * @author Ignasi
 *
 */
public class iLAO extends algoritmo {

	/**
	 * Clase iLAO herda de clase algoritmo
	 */
	Stack<Estado> z = new Stack<Estado>();
	public HashMap<String, List<Float>> VLao = new HashMap<String, List<Float>>();
	Estado sInicial;
	
	public iLAO(ProblemDefinition p) {
		problem = p;
		initHash(p);
		initIndex = p.getIndexEstadoInicial();
		timeStart = System.currentTimeMillis();
		
		//Estado Inicial
		sInicial = p._sEstados[this.initIndex];
		if(this.initIndex >= 0){
			do{
				//z.add(sInicial);
				iLAOTrial(sInicial);
			}
			while(!convergenceTest());
			problem.closeOutput();
			//Imprimimos resultados
			for(int i = 0; i<p._sEstados.length;i++){
				if(p._sEstados[i] != null){
					Actions a = greedyAction(p._sEstados[i]);
					System.out.println("Action(" + p._sEstados[i]._sEstado + "): " + a._sActionName + " V(" + p._sEstados[i]._sEstado + "): " + VLao.get(p._sEstados[i]._sEstado));
				}			
			}
		}else{
			System.out.println("Nao existem estados iniciais. Programa concluido.");
		}
	}
	
	public void iLAOTrial(Estado s){
		if(!s.isGoal && !s.isStacked){
			//Being visited
			s.isStacked = true;
			s.isVisited = true;
			//If not in the z list
			if(!z.contains(s)){
				z.add(s);
			}
			//pick best action and update hash
			//Update(s);
						
			//get Next states
			Actions a = greedyAction(s);
			for(int contadorAuxActions = a._sEstadoFinal.length-1; contadorAuxActions >= 0;contadorAuxActions--){
				if(a._sEstadoFinal[contadorAuxActions] != null){
					Estado sAux = problem.getEstado(a._sEstadoFinal[contadorAuxActions]);
					iLAOTrial(sAux);
				}
			}
		}
	}
	
	public void plotValues(){
		long timeFinish = System.currentTimeMillis();
		long timeAux = timeFinish-timeStart;		
		DecimalFormat df = new DecimalFormat("#.###");
		String timeDown=df.format(timeAux/1000.0);
		List<Float> auxList = VLao.get(this.problem._sEstados[this.initIndex]._sEstado);
		this.problem.outputString(timeDown + ";" + auxList.get(auxList.size()-1));
	}
	
	public void vIteration(Estado s){
		do{
			Update(s);
		}while(residual(s)>epsilon);
	}
	
	public boolean convergenceTest(){
		boolean test = true;
		while(!z.isEmpty()){
			Estado sAux = z.pop();
			if(!sAux.isGoal){
				sAux.isStacked = false;
				//Actions a = sAux.greedyAction;
				Update(sAux);
				//Print the updated values of V[initState]
				if(sAux.isInitial){
					plotValues();
				}
				if(residual(sAux) > epsilon){
					//iLAOTrial(sInicial);
					test = false;
				}
				/*if(!sAux.greedyAction.equals(a)){
					//iLAOTrial(sInicial);
					test = false;
				}*/
			}
		}
		return test;
	}

	public void initHash(ProblemDefinition p){
		List<Float> listAux = new ArrayList<Float>();
		listAux.add(0.0f);
		for(int i=0;i<p._sEstados.length;i++){
			if(p._sEstados[i] != null){
				VLao.put(p._sEstados[i]._sEstado, listAux);
			}			
		}	
	}
	
	public void Update(Estado s){
		Actions a = greedyAction(s);
		float val = qValue(a);
		s.greedyAction = a;
		List<Float> listAux = new ArrayList<Float>();
		for(int i=0; i<VLao.get(s._sEstado).size();i++){
			listAux.add(VLao.get(s._sEstado).get(i));
		}
		//List<Float> listAux = VLao.get(s._sEstado);
		listAux.add(val);		
		//System.out.println("Value V(" + s._sEstado + "): " + val);
		VLao.put(s._sEstado, listAux);
	}
	
	public float qValue(Actions a){
		float v = 0;
		float sum = 0;
		//Formula de Bellman
		try {			
			for(int i=0;i<a._sEstadoFinal.length;i++){
				if(a._sEstadoFinal[i] != null){
					if(a._sProbabilidad[i] > 0){
						List<Float> listAux = VLao.get(a._sEstadoFinal[i]);
						sum += a._sProbabilidad[i] * listAux.get(listAux.size()-1);
					}					
				}
			}
		} catch (Exception e) {
			System.out.println("Error durante execuçao durante o calculo do qValue: " + e.toString());
		}
		//
		v = problem.getEstado(a._sEstadoInicial)._sReward - a._sCost + problem.discountFactor * sum;
		//System.out.println("Action: " + a._sActionName);
		return v;
	}

	public Actions greedyAction(Estado s){
		for(int i=0;i<s._sActions.length;i++){
			if(s._sActions[i] != null){
				s._sActions[i].q = qValue(s._sActions[i]);
			}
		}
		//System.out.println("State: " + s._sEstado);
		return argMax(s._sActions);
	}
	
	public float residual(Estado s){
		List<Float> listAux = VLao.get(s._sEstado);
		float val = 0;
		if(listAux.size() > 1){
			val = Math.abs(listAux.get(listAux.size()-2) - listAux.get(listAux.size()-1));
		}
		else{
			val = 1;
		}		
		return val;
	}
	
	public Estado pickNextState(Actions a){
		Estado auxEstado = problem.getEstado(a._sEstadoFinal[0]);
		for(int i = a._sEstadoFinal.length-1; i>=0; i--){
			if(a._sEstadoFinal[i] != null){
				if(!problem.getEstado(a._sEstadoFinal[i]).isStacked){
					auxEstado = problem.getEstado(a._sEstadoFinal[i]);
					break;
				}				
			}
		}
		return auxEstado;
	}
	
	
}
