import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

import Algoritmos.LRTDP;
import Algoritmos.iLAO;
import Dominio.ProblemDefinition;

/**
 * 
 */

/**
 * @author Ignasi Andres Franch
 *
 */
public class Main {

	/**
	 * @param args
	 * Clase Main
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException{
		String strLine;
		String nameAction;
		String[] tokensEstado = null;
		String[] tokensAction = null;
		ProblemDefinition problema;
		//Criamos as instancias dos estados e das açoes:
		//System.out.println("Introduzir a localizaçao do arquivo com as instancias: ");

		File f = new File(".\\DomainProblems\\");
		if (f.isDirectory()) {
			for (File f2 : f.listFiles())
				if (f2.getName().endsWith(".txt")) {
					problema = new ProblemDefinition();
					System.out.println("Loading: " + f2);
					//rddl.addOtherRDDL(parser.parse(f2));

					//rddl.addOtherRDDL(parser.parse(f));

					//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					try {
						//Lemos o endereço:
						//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
						//String loc = br.readLine();
						//lemos as primeira linha
						FileReader input = new FileReader(f2);
						//Criamos o buffer de leitura, que vai ler uma linha de cada vez
						BufferedReader bufRead = new BufferedReader(input);
						
						while((strLine = bufRead.readLine())!= null){
							if(strLine.length()>=8){
								//Caso 5: Factor
								if(strLine.substring(0, 8).equalsIgnoreCase("discount")) {
									tokensAction = strLine.trim().split(" ");
									problema.discountFactor = Float.parseFloat(tokensAction[2]);
								}
								//Caso: Action
								else if(strLine.substring(0, 6).equalsIgnoreCase("action")) {
									nameAction = strLine.substring(6, strLine.length());
									while(!(strLine = bufRead.readLine()).trim().equalsIgnoreCase("endaction")){
										tokensAction = strLine.trim().split(" ");
										problema.setActionEstado(nameAction.trim(), tokensAction[0], tokensAction[1], Float.parseFloat(tokensAction[2]));
									}
								}
								//Goal state
								else if(strLine.substring(0, 9).equalsIgnoreCase("goalstate")){
									while(!(strLine = bufRead.readLine()).trim().equalsIgnoreCase("endgoalstate")){
										tokensAction = strLine.trim().split(" ");
										problema.setFinalState(tokensAction[0]);
									}
								}
								//Initial state
								else if(strLine.substring(0, 12).equalsIgnoreCase("initialstate")){
									while(!(strLine = bufRead.readLine()).trim().equalsIgnoreCase("endinitialstate")){
										tokensAction = strLine.trim().split(" ");
										problema.setIndexEstadoInicial(tokensAction[0]);
									}
								}
							}else if(strLine.length()>=6){
								//Caso: Estados
								String auxString = strLine.substring(0, strLine.length()).trim();
								if(auxString.equalsIgnoreCase("states")){
									while(!(strLine = bufRead.readLine()).trim().equalsIgnoreCase("endstates")){
										tokensEstado = strLine.trim().split(", ");
									}
									for(int aux = 0; aux < tokensEstado.length; aux++){
										problema.addEstado(tokensEstado[aux]);
									}	
								}
								//Caso 3: Reward
								else if(strLine.substring(0, 6).equalsIgnoreCase("reward")) {
									while(!(strLine = bufRead.readLine().trim()).equalsIgnoreCase("endreward")){
										tokensAction = strLine.trim().split(" ");
										problema.setRewardEstado(tokensAction[0], Integer.parseInt(tokensAction[1]));
									}
								}
							}//Caso 4: Cost	
							else if(strLine.length()>=4){
								if(strLine.substring(0, 4).equalsIgnoreCase("cost")) {
									while(!(strLine = bufRead.readLine().trim()).equalsIgnoreCase("endcost")){
										tokensAction = strLine.trim().split(" ");
										problema.setCostAction(tokensAction[0], Integer.parseInt(tokensAction[1]));
									}
								}
							}
						}
					}catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("Error lendo arquivo.");
						e.printStackTrace();
					}
					//printResults(problema);
					problema.initActions();
					String problem_fileName = ".\\Results\\" + f2.getName();
					problema.prepareOutput(problem_fileName.replace(".\\DomainProblems\\", ".\\Results\\").replace(".txt", "-result.txt"));
					long timeStart = System.currentTimeMillis();
					//LRTDP lrtdp = new LRTDP(problema);
					iLAO ilao = new iLAO(problema);
					long timeFinish = System.currentTimeMillis();
					int estadostotales = 0;
					int estadosvisitados = 0;
					for(int x=0;x<problema._sEstados.length;x++){
						if(problema._sEstados[x] != null)
						{
							estadostotales++;
							if(problema._sEstados[x].isVisited){
								estadosvisitados++;
							}
						}
					}
					problema.Clear();
					long timeAux = timeFinish-timeStart;		
					DecimalFormat df = new DecimalFormat("#.###");
					String timeDown=df.format(timeAux/1000.0);
					//Contador de tempo
					System.out.println(f2.getName() + ";" + timeDown + ";Estados totales: " + estadostotales + ";Estados visitados: " + estadosvisitados);
				}
		}
	}

	public static void printResults(ProblemDefinition problema){
		System.out.println("Problema: ");
		for(int i= 0; i<problema._sEstados.length; i++){
			System.out.println("Estado: " + problema._sEstados[i]._sEstado);
			System.out.println("Reward Estado: " + problema._sEstados[i]._sReward);
			System.out.println("Açoes: ");
			for(int j = 0; j<problema._sEstados[i]._sActions.length; j++){
				if(problema._sEstados[i]._sActions[j] != null){
					System.out.println(problema._sEstados[i]._sActions[j]._sActionName
							+ " " + problema._sEstados[i]._sActions[j]._sEstadoInicial
							+ " " + problema._sEstados[i]._sActions[j]._sEstadoFinal
							+ " " + problema._sEstados[i]._sActions[j]._sCost
							+ " " + problema._sEstados[i]._sActions[j]._sProbabilidad);
				}
			}
			System.out.println("-------------------------------------------------------");
		}
		System.out.println("Factor de disconto: " + problema.discountFactor);
		System.out.println("-------------------------------------------------------");
		System.out.println("-----------------------LRTDP---------------------------");
	}

}
