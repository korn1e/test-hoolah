package org.ajigile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Configuration {

    private Properties props;

    public void loadConfiguration() throws IOException {
        String workingDir = System.getProperty("user.dir");

        String applicationDir = workingDir;
        Path applicationPath = Paths.get(System.getProperty("java.class.path"));
        if(applicationPath.toString().contains(File.separator)){
            applicationDir = applicationPath.getParent().toString();
        }

        System.out.println("WORK_DIR: " + workingDir);
        System.out.println("APP_DIR...: " + applicationDir);

        props = new Properties();
        InputStream is = null;
        if(Files.exists(Paths.get(Constants.APPLICATION_PROPERTIES))){
            System.out.println(String.format("Use %s%sapplication.properties", workingDir, File.separator));
            is = Files.newInputStream(Paths.get(Constants.APPLICATION_PROPERTIES));
        } else if(Files.exists(Paths.get(applicationDir + "/" + Constants.APPLICATION_PROPERTIES))){
            System.out.println(String.format("Use %s%sapplication.properties", applicationDir, File.separator));
            is = Files.newInputStream(Paths.get(applicationDir + "/" + Constants.APPLICATION_PROPERTIES));
        } else {
            System.out.println("Use internal application.properties");
            is =  Main.class.getResourceAsStream("/"+Constants.APPLICATION_PROPERTIES);
        }

        props.load(is);
    }

    public String getProperty(String key){
        return props == null ? null : props.getProperty(key);
    }

    public String getProperty(String key, String defaultVal){
        return props == null ? null : props.getProperty(key, defaultVal);
    }

    public String toString(){
        return props == null ? null : props.toString();
    }
}
