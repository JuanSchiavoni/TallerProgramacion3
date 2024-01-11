import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class Panel extends javax.swing.JFrame {
    
    //Matriz 10x10
    int nFil = 10;
    int nCol = 10;
    //20 minas
    int nMin = 20;
    //Matriz de botones
    JButton [][] botonesTablero;
    Tablero tableroBuscaminas;
    //Temporizador
    private Timer timer;
    private JLabel etiquetaTiempo;
    private int segundosTranscurridos;
    //Files
    private static final String ARCHIVO_ESTADISTICAS = "estadisticas.txt";
    private FileWriter escritorEstadisticas;


    //Constructor de la ventana
    public Panel (){
        //initComponents coloca la barra de menu arriba y pone el reloj en su lugar
        initComponents();
        //Inicia un juego nuevo
        juegoNuevo();
        //Nombre de la Ventana
        setTitle("Buscaminas 10x10 - 20 minas");
        //Timer
        segundosTranscurridos = 0;
        etiquetaTiempo = new JLabel("Tiempo: 0 segundos");
        etiquetaTiempo.setBounds(10, 10, 150, 15);
        getContentPane().add(etiquetaTiempo);

        //crea o abre el archiuvo estadisticas
        try {
            escritorEstadisticas = new FileWriter(ARCHIVO_ESTADISTICAS, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //quita los botones del tablero en la interfaz
    void descargarControles(){
        if(botonesTablero!=null){
            for(int i = 0; i < botonesTablero.length; i++){
                for(int j = 0; j < botonesTablero[i].length; i++){
                     if(botonesTablero[i][j]!=null){
                        getContentPane().remove(botonesTablero[i][j]);
                    }
                }    
            }
        }
    }

    private void juegoNuevo(){
        //se llama primero para borrar si habia algo anteriorimente
        descargarControles();
        //carga todaa la matriz de controles
        cargarControles();
        //crea el tablero con los posibles eventos
        crearTablero();
        repaint();
    }


    //creo el tablero y los eventos posibles
    private void crearTablero(){
        tableroBuscaminas = new Tablero(nFil, nCol, nMin);
        //partida perdida
        /*
         * si toco una mina detiene el temporizador, muestra la ventana con el mensaje "Has perdido!"
         * escribo en el archivo de estadisticas que la partida se perdio en x segundos,
         * deshabilito los botones
         */
        tableroBuscaminas.setEventoPartidaPerdida(new Consumer<List<Casilla>>() {
            public void accept(List<Casilla> t){
                for(Casilla casillasConMina: t){
                    botonesTablero[casillasConMina.getPosFil()][casillasConMina.getPosCol()].setText("*");
                }
                detenerTemporizador();
                JOptionPane.showMessageDialog(rootPane, "¡Has perdido!");
                escribirEstadisticas("Partida Perdida", segundosTranscurridos);
                deshabilitarBotones();
            }
        });


        //Partida ganada
        /*
         * si no toque ninguna mina y no quedan casillas por abrir, detengo el temporiador, luego
         * muestro el mensaje "¡Has Ganado!", y escribo en el archivo estadisticas.txt partida ganada
         * y el tiempo en segundos, por ultimo deshabilito botones
         */
        tableroBuscaminas.setEventoPartidaGanada(new Consumer<List<Casilla>>() {
            public void accept(List<Casilla> t){
                for(Casilla casillasConMina: t){
                    botonesTablero[casillasConMina.getPosFil()][casillasConMina.getPosCol()].setText(":)");
                }
                detenerTemporizador();
                JOptionPane.showMessageDialog(rootPane, "¡Has ganado!");
                escribirEstadisticas("Partida Ganada", segundosTranscurridos);
                deshabilitarBotones();
            }
        });


        //Casilla abierta
        tableroBuscaminas.setEventoCasillaAbierta(new Consumer<Casilla>() {
            public void accept(Casilla t){
                botonesTablero[t.getPosFil()][t.getPosCol()].setEnabled(false);
                botonesTablero[t.getPosFil()][t.getPosCol()].setText(t.getnMinAl() == 0? "" : t.getnMinAl() + "");
            }
        });
        //trampita
        tableroBuscaminas.imprimirTablero();
    }


    //esctibe las estadisticas en el txt
    private void escribirEstadisticas(String resultado, int tiempo) {
        try {
            escritorEstadisticas.write(resultado + " - Tiempo: " + tiempo + " segundos\n");
            //Flush garantiza que se esctiban de inmediato
            escritorEstadisticas.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Detiene el timer
    private void detenerTemporizador(){
        if(timer.isRunning()){
            timer.stop();        
        }
    }


    //Inicia el timer
    private void iniciarTemporizador() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    //Actualiza el contador de segundos
    private void actualizarTiempo() {
        etiquetaTiempo.setText("Tiempo: " + segundosTranscurridos + " segundos");
    }

    //Desabilita los botones del juego en caso de partida ganada o perdida
    private void deshabilitarBotones() {
        for (int i = 0; i < botonesTablero.length; i++) {
            for (int j = 0; j < botonesTablero[i].length; j++) {
                botonesTablero[i][j].setEnabled(false);
            }
        }
    }



    private void cargarControles() {

        int posXRef = 25;
        int posYRef = 25;
        int anchoControl = 30;
        int altoControl = 30;

        botonesTablero = new JButton[nFil][nCol];
        for(int i = 0; i < botonesTablero.length; i++){
            for(int j = 0; j < botonesTablero[i].length; j++){
                botonesTablero[i][j] = new JButton();
                botonesTablero[i][j].setName(i + ", " + j);
                botonesTablero[i][j].setBorder(null);
                if(i==0 && j==0){
                    // pone el primer boton a tocar
                    botonesTablero[i][j].setBounds(posXRef, posYRef, anchoControl, altoControl);
                }else if(i==0 && j!= 0){
                    //pone la primera fila de botones
                    botonesTablero[i][j].setBounds(botonesTablero[i][j-1].getX()+botonesTablero[i][j-1].getWidth(), posYRef, anchoControl, altoControl);
                }else {
                    //pone la primer columna de botones y completa la matriz de botones
                    botonesTablero[i][j].setBounds(botonesTablero[i-1][j].getX(), botonesTablero[i-1][j].getY() + botonesTablero[i-1][j].getHeight(), anchoControl, altoControl);
                }

                //Se aggrega el ActionListener a cada boton para manejar los posibles eventos con btnClick()
                botonesTablero[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e){
                        btnClick(e);
                    }

                });
                getContentPane().add(botonesTablero[i][j]);
            }
        }
        //Ajusto el tamaño de la ventana para que todos los botones sean visibles
        this.setSize(botonesTablero[nFil-1][nCol-1].getX() + botonesTablero[nFil-1][nCol-1].getWidth()+30, 
                botonesTablero[nFil-1][nCol-1].getY() + botonesTablero[nFil-1][nCol-1].getHeight()+70);
    }


    //Maneja los clicks
    private void btnClick(ActionEvent e){
        JButton  btn = (JButton) e.getSource(); 
        String[] coordenada = btn.getName().split(", ");
        int posFil = Integer.parseInt(coordenada[0]);
        int posCol = Integer.parseInt(coordenada[1]);
        
        //Inicializa el contador al clickear por primera vez y comenzar un juego
        if(!timer.isRunning()){
            iniciarTemporizador();
        }
        tableroBuscaminas.seleccionarCasilla(posFil, posCol);
    }


    //Lee el archivo estadisticas y calcula el % de partidas ganadas
    private double cargarEstadisticas() {
        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO_ESTADISTICAS))) {
            int totalPartidas = 0;
            int totalPartidasGanadas = 0;

            String linea;
            while ((linea = lector.readLine()) != null) {
                totalPartidas++;
                if (linea.contains("Partida Ganada")) {
                    totalPartidasGanadas++;
                }
            }

            double porcentajeVictorias = (double) totalPartidasGanadas / totalPartidas * 100;
            porcentajeVictorias = Math.round(porcentajeVictorias * 100.0) / 100.0;
            return porcentajeVictorias;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }


    //Lee las estadisticas y saca los tiempos solo de las partidas ganadas
    private int obtenerMenorTiempoPartidaGanada() {
        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO_ESTADISTICAS))) {
            int menorTiempo = Integer.MAX_VALUE;

            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.contains("Partida Ganada")) {
                    int tiempoPartida = extraerTiempo(linea);
                    if (tiempoPartida < menorTiempo) {
                        menorTiempo = tiempoPartida;
                    }
                }
            }

            return menorTiempo;
        } catch (IOException e) {
            e.printStackTrace();
            return -1; 
        }
    }


    /*
     * divide la linea a partir del ":", creando un array donde en partes[0] queda lo que
     * esta antes de ":" y en partes[1] queda lo de despues de ":", partes[1].split 
     * vuelve a separar la linea en " "; con Integer.parseInt convierto la parte numerica
     * del array a un entero 
     */
    private int extraerTiempo(String linea) {
        String[] partes = linea.split(": ");
        return Integer.parseInt(partes[1].split(" ")[0]);
    }

    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        //Barra del menu 
        jMenuBar1 = new javax.swing.JMenuBar();
        //Menu estadisticas
        jMenu1 = new javax.swing.JMenu();
        //muestra mejor tiempo en una partida ganada
        menuEstadisticas = new javax.swing.JMenuItem();
        //muestra porcentaje de victorias
        menuVictorias = new javax.swing.JMenuItem();
        

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenu1.setText("Estadísticas");


        //establezco los manejadores de eventos para menuEstadisticas y menuVictorias
        menuEstadisticas.setText("Mejor tiempo");
        menuEstadisticas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMejorTiempoActionPerformed(evt);
            }
        });
        jMenu1.add(menuEstadisticas);

        menuVictorias.setText("% de victorias");
        menuVictorias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuVictoriasActionPerformed(evt);
            }
        });
        jMenu1.add(menuVictorias);


        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 518, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 377, Short.MAX_VALUE)
        );

        pack();

        //configuro el timer
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                segundosTranscurridos++;
                actualizarTiempo();
            }
        });
    }


    //Este metodo se llama cuando se clickea mejor tiempo en el menu y muestra el cartel JOptionPane
    private void menuMejorTiempoActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(rootPane, "Mejor tiempo en una partida: " + obtenerMenorTiempoPartidaGanada() + " segundos.");
    }


    //Este metodo se llama cuando se clickea porcentaje de victorias en el menu y muestra el cartel JOptionPane
    private void menuVictoriasActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(rootPane, "Porcentaje de victorias: " + cargarEstadisticas() + "%");
        
    }


    public static void main(String args[]) {

        //aspecto visual usando estilo nimbus
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Panel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Panel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Panel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Panel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Panel().setVisible(true);
            }
        });
    }

    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem menuEstadisticas;
    private javax.swing.JMenuItem menuVictorias;

}
