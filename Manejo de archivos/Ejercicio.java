import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Ejercicio {
    

    private static final String BULK_FILE_PATH = Paths.get(System.getProperty("usuario.dir"), "bulk.txt").toString();
    private static final String LOG_FILE_PATH = Paths.get(System.getProperty("usuario.dir"), "logs.txt").toString();

    public static void main(String[] args) {
        //Este metodo es solo para poblar el bulkFile y poder pobrar el codigo basado en el directorio donde se ejecute
        escribeArchBulk();

        Timer timer = new Timer();
        
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Inicio de Rutina.");
                try{
                    List<String> files = leeArchBulk();
                    initThreads(files);
                }catch(FileNotFoundException e){
                    System.out.println("Se produjo una FileNotFoundException");
                    e.printStackTrace();
                }catch(IOException e){
                    System.out.println("Se produjo una IOException");
                    e.printStackTrace();
                }
            }
        };
        
        timer.schedule(tarea, 0, 10000); //60000 representa un minuto, el tiempo esta en milisegundos

    }

    private static List<String> leeArchBulk() throws IOException, FileNotFoundException {
        List<String> filesPath = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(BULK_FILE_PATH))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                filesPath.add(linea);
            }
  
            return filesPath;
        }
    }

    private static void initThreads(List<String> files){
        if(files.isEmpty()) {
            System.out.println("La lista de archivos esta vacía.");
            return;
        };

        int filesCount = files.size();
        Double d = Math.ceil(filesCount/10.0);
        
        int necessaryThreads = d.intValue();
        
        /**
         * Si necesitamos mas de un hilo, sabemos que en la primer op el limite sera 10,
         *  caso contrario sera la cant de archivos
         */

        int init = 0;
        int end = (necessaryThreads > 1) ? 10 : filesCount; 

        for(int i=0; i < necessaryThreads; i++){
            List<String> subFiles = files.subList(init, end);
            init+=10;
            int auxInt = end+10;
            end = ( auxInt < filesCount) ? auxInt : filesCount;

            ProcesoArchivos fpt = new ProcesoArchivos(subFiles, LOG_FILE_PATH, i);
            Thread t = new Thread(fpt);
            t.start();
        }

     }

    private static void escribeArchBulk(){
        File file = new File(BULK_FILE_PATH);
        if (file.exists()) file.delete();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BULK_FILE_PATH, true))) {

            for(int i = 0; i < 8; i++){
                String fileToWrite = Paths.get(System.getProperty("usuario.dir")).resolve("ficheros").resolve("archivo"+i+".txt").toString();
                
                writer.write(fileToWrite);
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Ha ocurrido un error al generar el log.");
            e.printStackTrace();
        }
    }
}