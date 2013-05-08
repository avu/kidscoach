/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kidscoach;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author avu
 */
public class Utils {
    
    private static final Logger log = Logger.getLogger(Project.class.getName());

    public static void delete(File file) throws IOException {
        if(file.isDirectory()){
 
            //directory is empty, then delete it
            if(file.list().length==0){
                file.delete();
                log.log(Level.FINE, "Directory is deleted : {0}", 
                        file.getAbsolutePath());
            } else {
                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    
                    //recursive delete
                    delete(fileDelete);
                }
 
                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    log.log(Level.FINE, "Directory is deleted : {0}", 
                    file.getAbsolutePath());
                }
            }
 
    	} else {
            //if file, then delete it
            file.delete();
            log.log(Level.FINE, "File is deleted : {0}", 
                file.getAbsolutePath());
    	}
    } 
}