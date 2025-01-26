package starlock.obf.manager;

import lombok.Getter;
import lombok.SneakyThrows;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConfigManager {
    @Getter
    private static final YamlConfiguration config = new YamlConfiguration();

    @SneakyThrows
    public static void loadCFG(String fileName){
        config.load(new File(fileName));
    }
    public static void saveCFG(){
        try {
            InputStream cfg = ConfigManager.class.getResourceAsStream("../../config.yml");
            FileOutputStream outputStream = new FileOutputStream("config.yml");
            outputStream.write(cfg.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
