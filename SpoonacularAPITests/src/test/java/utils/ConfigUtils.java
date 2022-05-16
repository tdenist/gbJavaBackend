package utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

@UtilityClass
public class ConfigUtils {

    Properties prop = new Properties();
    private static InputStream configFile;

    static {
        try {
            configFile = new FileInputStream("src/main/resources/config.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public String getBaseUrl()  {
        prop.load(configFile);
        return prop.getProperty("baseUrl");
    }

    @SneakyThrows
    public String getApiKey() {
        prop.load(configFile);
        return prop.getProperty("apiKey");
    }

}
