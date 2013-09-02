package org.almuallim.service.helpers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author Naveed Quadri
 */
public class JavaFX {

    /**
     * Just a matching equivalent for SwingUtilities for JavaFX
     * @param r 
     */
    public static void invokeAndWait(Runnable r) {
        FutureTask<Boolean> task = new FutureTask<>(r, true);
        Platform.runLater(task);
        try {
            task.get(); // wait for completition, blocking the thread if needed
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(JavaFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
