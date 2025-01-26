package starlock.obf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import starlock.obf.manager.FileManager;
import starlock.obf.manager.StarLockManager;
import starlock.obf.obfuscator.Obfuscator;
import org.apache.commons.cli.*;
import java.io.File;
import java.util.*;

public class Main extends FileManager implements StarLockManager {
    protected static final Logger LOGGER = LogManager.getLogger("StarLock");

    public static void main(String[] args) {
        preStart();

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(getOptions(), args);
            if(cmd.hasOption("libs")) {
                LOGGER.info("    Loading libs....");
                setPath(new File(cmd.getOptionValue("libs")));
                loadLibs();
                LOGGER.info("    Loaded!\n");
            }

            if(cmd.hasOption("input")) {
                setInputFile(new File(cmd.getOptionValue("input")));
            } else throw new RuntimeException("Input has null!");

            if(cmd.hasOption("output")) {
                setOutputFile(new File(cmd.getOptionValue("output")));
            } else throw new RuntimeException("Output has null!");

            if(cmd.hasOption("config")) {
                loadCFG(cmd.getOptionValue("config"));
            } else {
                if(!new File("config.yml").exists()) saveCFG();
                loadCFG("config.yml");
            }

        } catch (ParseException e) {
            System.err.println("Cannot parse command line.");
            throw new RuntimeException(e);
        }

        parseFile();
        new Obfuscator().run();
        saveOutput();
    }
    private static int getLen(String str){
        int ban = ("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =".length());
        return (ban - str.length());
    }
    private static void preStart(){
        String version = StarLockManager.VERSION;
        int forRepeat = (getLen(version) - "                               ".length());
        LOGGER.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        LOGGER.info("=                     StarLock LTD                        =");
        LOGGER.info("=            StarLock - New Modern Protection             =");
        LOGGER.info("=                   Version: "+version+" ".repeat(forRepeat)+" =");
        LOGGER.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        LOGGER.info("=                       Authors                           =");
        Arrays.stream(StarLockManager.Authors).forEach(str -> LOGGER.info("=   "+str));
        LOGGER.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =\n");
    }
    private static Options getOptions() {
        final Options options = new Options();
        options.addOption(new Option("i","input", true, "Input file."));
        options.addOption(new Option("o","output", true, "Output file."));
        options.addOption(new Option( "l","libs", true, "Lib dir."));
        options.addOption(new Option( "cfg","config", true, "Config file."));
        return options;
    }
}
