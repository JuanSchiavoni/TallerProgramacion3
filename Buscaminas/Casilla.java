public class Casilla {
    
    private int posFil;
    private int posCol;
    private boolean mina;
    private int nMinAl;
    private boolean abierta;


    //Constructor de Casilla
    public Casilla(int posFil, int posCol) {
        this.posFil = posFil;
        this.posCol = posCol;
    }


    //Getter y Setter de posicion de la fila y la posicion de la columna
    public int getPosFil() {
        return posFil;
    }
    public void setPosFil(int posFil) {
        this.posFil = posFil;
    }
    public int getPosCol() {
        return posCol;
    }
    public void setPosCol(int posCol) {
        this.posCol = posCol;
    }


    //Getter(isMina) y Setter de minas
    public boolean isMina() {
        return mina;
    }
    public void setMina(boolean mina) {
        this.mina = mina;
    }


    //Getter y Setter de minas alrededor 
    public int getnMinAl() {
        return nMinAl;
    }
    public void setnMinAl(int nMinAl) {
        this.nMinAl = nMinAl;
    }
     /*
    *numnero de minas alrededor sirve para verificar cuantas minas hay 
    * alrededor de una casilla presionada 
    */
    

    //Metodo para incrementar el numero de minas alrededor de una casilla
    public void incrementarNMinAl(){
        this.nMinAl++;
    }


    //Getter (isAbierta) y Setter de abierta
    public boolean isAbierta() {
        return abierta;
    }

    public void setAbierta(boolean abierta) {
        this.abierta = abierta;
    }
    /*
     * Abierta me sirve para chequear que una casilla esta abierta,
     * es decir, que fue clickeada y no habia una mina en su posicion, o
     * en su defecto se abrio al no tener minas alrededor
     */
    

}
