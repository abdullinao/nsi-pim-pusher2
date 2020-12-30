package pusher;


import java.io.IOException;
import java.io.InputStream;

import java.util.Base64;
import java.util.Properties;

public class proploader {
    // private static final File PATH_TO_PROPERTIES = new File("src/main/resources/config.properties");
    // public static final String PATH_TO_PROPERTIES = "src/main/resources/config.properties";
    private String Prop_bdurl;
    private String Prop_port;
    private String Prop_servicename;
    private String Prop_pwd;
    private String Prop_user;
    private String Prop_test1;
    private String Prop_debug;
    private String test;
    private String pwd;

    public proploader(String debug) throws Exception {
        //   FileInputStream fileInputStream;
        //инициализируем специальный объект Properties
        //типа Hashtable для удобной работы с данными

        try (InputStream is = proploader.class.getClassLoader().getResourceAsStream("config.properties")) {
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
            this.Prop_pwd = prop.getProperty("user");
            this.Prop_user = prop.getProperty("password");
            //this.Prop_test1 = new String(Base64.getDecoder().decode(prop.getProperty("test1")));
            //this.Prop_debug = new String(Base64.getDecoder().decode(prop.getProperty("debug")));
            test = prop.getProperty("test1") + " ==";
            debug = prop.getProperty("debug") + " =";
            Prop_test1 = new String(Base64.getDecoder().decode(test));
            Prop_debug = new String(Base64.getDecoder().decode(debug));

            String pwdb64 = prop.getProperty("pwd");
            pwd = new String(Base64.getDecoder().decode(pwdb64));

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

    public String getProp_test1() {
        return Prop_test1;
    }

    public String getProp_debug() {
        return Prop_debug;
    }

    public String getPwd() {
        return pwd;
    }


}
