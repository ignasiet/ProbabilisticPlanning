/**
 * Class Problem Definition
 * Laboratorio IA.
 */
package Dominio;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Ignasi Andres Franch
 *
 */
public class ProblemDefinition {
	public int nMax = 500;
	public Estado[] _sEstados;
	private int i = 0;
	private int aMax;
	//private String outputFile;
	public float discountFactor;
	static BufferedWriter out;
	
	public ProblemDefinition(){
		this._sEstados = new Estado[nMax];
	}
	
	public void addEstado(String estadoName){
		this._sEstados[i] = new Estado(estadoName);
		i++;
	}
	
	public void setActionEstado(String nameAction, String estadoInicial, String estadoFinal, float probabilidade){
		Actions act = new Actions(nameAction, estadoInicial, estadoFinal, probabilidade);
		//Get State:
		int index = getIndexEstado(estadoInicial);
		for(int j=0;j<this._sEstados[index]._sActions.length;j++){
			//Get Action if exists
			if(this._sEstados[index]._sActions[j] != null){
				if(this._sEstados[index]._sActions[j]._sActionName.equalsIgnoreCase(nameAction)){
					//If exists, add new destination for the non-deterministic transition
					this._sEstados[index]._sActions[j].addDestination(estadoFinal, probabilidade);
					return;
				}
			}					
		}
		//If action doesnt exists, create and exit
		this._sEstados[index].addAction(act);
	}
	
	public void setRewardEstado(String estadoName, int reward){
		for(int index = 0; index<i; index++){
			if(this._sEstados[index]._sEstado.equalsIgnoreCase(estadoName)){
				this._sEstados[index].addReward(reward);
				break;
			} 			
		}
	}
	
	public void setCostAction(String actionName, int cost){
		for(int index = 0; index<this._sEstados.length; index++){
			if(this._sEstados[index] != null){
				for(int j = 0; j<this._sEstados[index]._sActions.length;j++){
					if(this._sEstados[index]._sActions[j] != null){
						if(this._sEstados[index]._sActions[j]._sActionName.equalsIgnoreCase(actionName)){
							this._sEstados[index]._sActions[j]._sCost = cost;
						}
					}
				}				
			} 			
		}
	}
	
	public int getIndexEstado(String nameEstado){
		int index;
		for(index = 0; index<this._sEstados.length; index++){
			if(this._sEstados[index] != null){
				if(this._sEstados[index]._sEstado.equalsIgnoreCase(nameEstado)){
					break;
				} 
			}
		}
		return index;
	}
	
	public Estado getEstado(String nameEstado){
		int index;
		for(index = 0; index<this._sEstados.length; index++){
			if(this._sEstados[index] != null){
				if(this._sEstados[index]._sEstado.equalsIgnoreCase(nameEstado)){
					break;
				} 
			}
		}
		return this._sEstados[index];
	}
	
	public void initMaxActions(int maxActions){
		this.aMax = maxActions;
	}
	
	public int getMaxActions(){
		return this.aMax;
	}
	
	public void initActions(){
		int indexActions =0;
		for(int i=0;i<this._sEstados.length;i++){
			if(this._sEstados[i] != null){
				indexActions = 0;
				for(int j=0;j<this._sEstados[i]._sActions.length;j++){
					if(this._sEstados[i]._sActions[j] != null){
						indexActions++;
						this._sEstados[i]._sActions[j].orderAction();
					}
				}
			}
		}
		this.initMaxActions(indexActions);
	}
	
	public void setFinalState(String nomeEstado){
		getEstado(nomeEstado).isGoal = true;
	}
	
	public void setIndexEstadoInicial(String nomeEstado){
		getEstado(nomeEstado).isInitial = true;
	}
	
	public int getIndexEstadoInicial(){
		for(int i=0;i<this._sEstados.length;i++){
			if(this._sEstados[i] != null){
				if(this._sEstados[i].isInitial){
					return i;
				}
			}
		}
		return -1;
	}
	
	public void Clear(){
		this._sEstados = null;
	}
	
	public void prepareOutput(String output){
		//this.outputFile = output;
		//Preparamos o arquivo onde vamos a guardar as informaçoes 
		//e as estatisticas das execuçoes:
		FileWriter fstream;
		try {
			fstream = new FileWriter(output, false);
			out = new BufferedWriter(fstream);
		    out.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	}
	
	public void outputString(String output){
		try {
			out.write(output + System.getProperty("line.separator"));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void closeOutput(){
		try{
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
