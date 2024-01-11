import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Tablero {

    //Armo una matriz de casilla para q queden 10x10
    Casilla[][] casillas;

    int nFil;
    int nCol;
    int nMin;

    int nCasillasAbiertas;
    boolean juegoTerminado;

    //avisa a panel que encontre una mina
    private Consumer<List<Casilla>> eventoPartidaPerdida;
    //avisa a panel que gane el juego
    private Consumer<List<Casilla>> eventoPartidaGanada;
    //avisa a panel que abri la casilla y no haiba una mina
    private Consumer<Casilla> eventoCasillaAbierta;

    //contructor de tablero
    public Tablero(int nFil, int nCol, int nMin) {
        this.nFil = nFil;
        this.nCol = nCol;
        this.nMin = nMin;
        this.inicializarCasillas();
    }
    /*
     * Contruye el tablero recibiendo un nuemro de fila y 
     * un numero de columnas, en el caso de nuestro juego por defecto 
     * son 10 columnas, 10 filas y 10 minas repartidas
     */


    public void inicializarCasillas(){
        casillas = new Casilla[this.nFil][this.nCol];
        //recorro todas las posiciones y genero las casillas
        for(int i=0; i < casillas.length; i++){
            for(int j=0; j < casillas[i].length; j++){
                casillas[i][j] = new Casilla(i, j); 
            }
        }
        generarMinas();
    }
    /*
     * Arma la matriz 10x10, recorriendo todas las filas y columnas 
     * e inicializando las posiciones.
     * Luego llama a generarMinas
     */


    private void generarMinas(){
        int minasGeneradas = 0;
        while (minasGeneradas != nMin) {
            //coloco las minas en posiciones random
            int tmpFil = (int) (Math.random() * casillas.length);
            int tmpCol = (int) (Math.random() * casillas[0].length);
            if(!casillas[tmpFil][tmpCol].isMina()){
                casillas[tmpFil][tmpCol].setMina(true);
                minasGeneradas++;
            }
        }
        actualizarNMinAl();
    }
    /*
     * Arranca con un contador de minas en 0, se usan posiciones temporales para
     * la fila y la columna donde se van poniendo minas mientras sea distinto a las minas 
     * necesitadas; luego llama a actualizarMinasAlrededor
     */
    

    public void imprimirTablero(){
        for(int i=0; i < casillas.length; i++){
            for(int j=0; j < casillas[i].length; j++){
                System.out.print(casillas[i][j].isMina()? " * ": " 0 "); 
            }
            System.out.println();
        }
    }
    /*
     * Esta funcion la use para ver como se formaba la matriz y si se iban cambiando las minas
     * cada vez que ejecutaba el programa, a la hora de jugar en serio comentar la linea
     * del systm.out para que no me imprima donde estan las minas
     */


    private void actualizarNMinAl(){
        for(int i=0; i < casillas.length; i++){
            for(int j=0; j < casillas[i].length; j++){
                if(casillas[i][j].isMina()){
                    List<Casilla> casillasAlr = obtenerCasillasAl(i, j);         
                    casillasAlr.forEach((c)->c.incrementarNMinAl());
                }
            }
        }
    }
    /*
     * recorro todas las casillas del tablero, si la casilla contiene una mina, obtengo las casillas
     * adyacentes con obtenerCasillasAl(), se usa un bucle forEach para iterar sobre la lista de casillas 
     * e incremento las minas alrededor para cada casilla
     */

    

    private List<Casilla> obtenerCasillasAl(int posFil, int posCol){
        List<Casilla> listaCasillas = new LinkedList<>();
        for(int i=0; i < 8; i++){
            int tmpFil = posFil;
            int tmpCol = posCol;
            switch (i) {
                case 0: tmpFil--; break; //Arriba
                case 1: tmpFil--; tmpCol++; break; //Arriba Derecha
                case 2: tmpCol++; break; //Derecha
                case 3: tmpCol++; tmpFil++; break; //Derecha Abajo
                case 4: tmpFil++; break; //Abajo
                case 5: tmpFil++; tmpCol--; break; //Abajo Izquierda 
                case 6: tmpCol--; break; //Izquierda
                case 7: tmpFil--; tmpCol--; break; //Izquierda Arriba
            }
            if(tmpFil>=0 && tmpFil<this.casillas.length && tmpCol>= 0 && tmpCol < this.casillas[0].length){
                listaCasillas.add(this.casillas[tmpFil][tmpCol]);
            }
        }
        return listaCasillas;
    }
    /*
     * recibo una posicion y hago un bucle de 8 repeticiones pues alreddeodr de una casilla hay 8, 
     * salvo que este en los bordes, por eso la verificacion del if
     * me devuelve una lista de las casillas adyacentes
     */


    List<Casilla> obtenerCasillasConMinas(){
        List<Casilla> casillasConMinas = new LinkedList<>();
        for(int i=0; i < casillas.length; i++){
            for(int j=0; j < casillas[i].length; j++){
                if(casillas[i][j].isMina()){
                    casillasConMinas.add(casillas[i][j]);
                }
            }
        }
        return casillasConMinas;
    }
    /*
     * recorro todo el tablero y obtengo un listado de casillas que contienen minas
     */


    public void seleccionarCasilla(int posFil, int posCol){
        //se notifica al Consumer que las casilla esta abierta
        eventoCasillaAbierta.accept(this.casillas[posFil][posCol]);
        //si la casilla contiene un mina, se pierde el jego
        if(this.casillas[posFil][posCol].isMina()){
            //notifico al Consumer que perdio 
            eventoPartidaPerdida.accept(obtenerCasillasConMinas());
        }else if(this.casillas[posFil][posCol].getnMinAl()==0){
            //si no tiene minas alrededor se expande hasta las casillas con minas adyacentes
            //se marca como abierta
            marcarCasillaAbierta(posFil, posCol);
            List<Casilla> casillasAlrededor = obtenerCasillasAl(posFil, posCol);
            //se exploran las adyacentes llamando recursivamente a seleccionarCasilla para cada no abierta adyacente.
            for(Casilla casilla: casillasAlrededor){
                if(!casilla.isAbierta()){
                    seleccionarCasilla(casilla.getPosFil(), casilla.getPosCol());
                }
            }
        }else{
            //si tiene minas adyacentes se marca solo como abierta
            marcarCasillaAbierta(posFil, posCol);
        }
        //verifica si ganamos la partida
        if(partidaGanada()){
            //notifico al Consumer que gano
            eventoPartidaGanada.accept(obtenerCasillasConMinas());
        }
    }
    /*
     * se evaluan las distintos casos posibles al clickear, si hay una mina se pierde el juego
     * si no hay minas alrededor se van abriendo las casillas alrededor hasta encontrar una 
     * casilla con minas alrededor, si tiene minas adyacentes simplemente se marca como abierta
     * por ultimo veridfico si gane la partida
     */


    void marcarCasillaAbierta(int posFil, int posCol){
        if(!this.casillas[posFil][posCol].isAbierta()){
            nCasillasAbiertas++;
            this.casillas[posFil][posCol].setAbierta(true);
        }
    }
    /*
     * recibe una casilla; si no esta abierta la abre y aumenta el numero de casillas
     * abiertas
     */

    boolean partidaGanada(){
        return nCasillasAbiertas >= (nFil*nCol)-nMin;
    }
    //si abri todas las casillas que no tienen minas entonces gane


    //Setters de los tres eventos posibles
    public void setEventoPartidaPerdida(Consumer<List<Casilla>> eventoPartidaPerdida) {
        this.eventoPartidaPerdida = eventoPartidaPerdida;
    }

    public void setEventoCasillaAbierta(Consumer<Casilla> eventoCasillaAbierta) {
        this.eventoCasillaAbierta = eventoCasillaAbierta;
    }

    public void setEventoPartidaGanada(Consumer<List<Casilla>> eventoPartidaGanada) {
        this.eventoPartidaGanada = eventoPartidaGanada;
    }

}
