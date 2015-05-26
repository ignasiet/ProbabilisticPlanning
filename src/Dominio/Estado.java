/**
 * 
 */
package Dominio;
/**
 * @author Ignasi
 *	Clase Estado.
 */
public class Estado {
	public String _sEstado;
	public float _sReward;
	private int nMaxActions = 10;
	public Actions[] _sActions = new Actions[nMaxActions];
	private int i = 0;
	public Actions greedyAction;
	public boolean isSolved = false;
	public boolean isGoal = false;
	public boolean isInitial = false;
	public boolean isStacked = false;
	public boolean isVisited = false;
	
	public Estado(String nome){
		this._sEstado = nome;
	}
	
	public void addReward(int reward){
		this._sReward = reward;
	}
	
	public void addAction(Actions action){
		this._sActions[i] = action;
		i++;
	}	
}
