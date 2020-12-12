package pusher;

import java.io.*;
import java.util.Properties;

public class proploader {
   // private static final File PATH_TO_PROPERTIES = new File("src/main/resources/config.properties");
   // public static final String PATH_TO_PROPERTIES = "src/main/resources/config.properties";
    private String Prop_bdurl;
    private String Prop_port;
    private String Prop_servicename;
    private String Prop_user;
    private String Prop_pwd;

    public  proploader() {
     //   FileInputStream fileInputStream;
        //инициализируем специальный объект Properties
        //типа Hashtable для удобной работы с данными

        try (  InputStream is = proploader.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(is);
            //обращаемся к файлу и получаем данные
           // fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            //BufferedReader reader = new BufferedReader(new FileReader(PATH_TO_PROPERTIES));
          //  prop.load(fileInputStream);
           // prop.load(new FileInputStream("main/resources/config.properties"));

            this.Prop_bdurl = prop.getProperty("bdurl");
            this.Prop_port = prop.getProperty("port");
            this.Prop_servicename = prop.getProperty("servicename");
            this.Prop_user = prop.getProperty("user");
            this.Prop_pwd = prop.getProperty("pwd");

        } catch (IOException e) {
            System.out.println("Ошибка в программе: файл  не обнаружено");
            e.printStackTrace();
        }


    }

    public String getProp_bdurl() {
        return Prop_bdurl;
    }

    public String getProp_port() {
        return Prop_port;
    }

    public String getProp_servicename() {
        return Prop_servicename;
    }

    public String getProp_user() {
        return Prop_user;
    }

    public String getProp_pwd() {
        return Prop_pwd;
    }
}
