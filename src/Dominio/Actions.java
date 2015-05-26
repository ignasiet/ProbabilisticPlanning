/**
 * 
 */
package Dominio;

/**
 * @author Ignasi
 *	Clase Action
 */
public class Actions {
	public String _sActionName;
	public String _sEstadoInicial;
	public String[] _sEstadoFinal = new String[3];
	public float[] _sProbabilidad = new float[3];
	public int _sCost;
	private int i = 0;
	private int nMaxAction = 2;
	public float q = 0;
	public boolean isNonDeterministic = false;
	
	public Actions(String name, String estado1, String estado2, float probabilidad){
		this._sActionName = name;
		this._sEstadoInicial = estado1;
		this._sEstadoFinal[i] = estado2;
		this._sProbabilidad[i] = probabilidad;
		i++;		
	}
	
	public void addDestination(String estado2, float probabilidad){
		this._sEstadoFinal[i] = estado2;
		this._sProbabilidad[i] = probabilidad;
		i++;
		this.isNonDeterministic = true;
	}
	
	public void setCost(int cost){
		this._sCost = cost;
	}
	
	public void orderAction(){
		//Ordena as açoes de menor a maior
		float auxFloat = 0.0f;
		String auxString;
		for(int i = 0; i< this._sProbabilidad.length;i++){
			int auxMin = i;
			if(this._sEstadoFinal[i] != null){
				for (int j = i + 1; j < this._sProbabilidad.length; j++) {
					if(this._sEstadoFinal[j] != null){
			          if (this._sProbabilidad[j] < this._sProbabilidad[auxMin]) {
			        	  auxMin = j;
			          }
					}
			    }
			    if (auxMin != i) {
			         auxString = this._sEstadoFinal[auxMin];
			         this._sEstadoFinal[auxMin] = this._sEstadoFinal[i];
			         this._sEstadoFinal[i] = auxString;
			         auxFloat = this._sProbabilidad[auxMin];
			         this._sProbabilidad[auxMin] = this._sProbabilidad[i];
			         this._sProbabilidad[i] = auxFloat;
			         
			    }
			}
		}
	}
}
