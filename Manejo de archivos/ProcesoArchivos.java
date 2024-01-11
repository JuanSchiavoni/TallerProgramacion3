import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class ProcesoArchivos implements Runnable {
    private static Logger logger = Logger.getLogger(ProcesoArchivos.class.getName());

    private List<String> files;
    
    private String logPath;

    private String threadName;

    public ProcesoArchivos(List<String> files, String logPath, int threadId) {
        this.files = files;
        this.logPath = logPath;
        this.threadName = "thread "+threadId;
    }

    @Override
    public void run() {
        //se agrega como validacion en caso que el hilo se haya instanciado con una referencia nula o bien una lista sin datos
        if(files == null || files.isEmpty()) return;

        for (String f : files) {
            File file = new File(f);
            String accion = "";
            
            try {
                if (file.exists()) {
                    logger.info(threadName+ ". Eliminando archivo: "+file);
                    accion = "B";
                    
                    if (!file.delete()) {
                        throw new IOException("Ha ocurrido un error al intentar borrar el archivo :"+file);
                    }
                } else {
                    logger.info(threadName+". Creando archivo: "+file);
                    accion = "C";

                    if (!file.createNewFile()) {
                        throw new IOException("Ha ocurrido un error al intentar crear el archivo :"+file);
                    }
                }
            } catch (IOException | SecurityException e) {
                logger.severe(threadName+" - "+e.getMessage());    
                escribeLog(f, accion);
            }
        }
    }

    /*
     Este metodo sera quien se encargue de escribir en el log
     */
    private synchronized void escribeLog (String file, String accion) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logPath, true))) {
            
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

            writer.write(date + " - " + accion + " - " + file);
            writer.newLine();
        } catch (IOException e) {
            logger.severe(threadName+" - "+"Ha ocurrido un error al generar el log.");
            e.printStackTrace();
        }
    }
    
}