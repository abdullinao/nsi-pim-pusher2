package pusher;

import java.io.Console;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class main {

    private static proploader proploader;

    static {
        try {
            proploader = new proploader(setdbg());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Console console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.exit(0);
        }
        char[] passwordArray = console.readPassword("Введите пароль(ввод не отображается): ");

        String key = new String(passwordArray);


        //otr2000
        // System.out.println("Пароль:");
        // String key = sc.nextLine();
        if (!key.equals(proploader.getPwd())) {

            System.out.println("hello world!");
            System.exit(1);

        }
        mainJob();
    }

    public static void mainJob() throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("-------- Попытка подключения к бд... ------");
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.out.println("Не нашел драйвер! Проверьте папку /lib!");
            e.printStackTrace();
            return;
        }

        System.out.println("Драйвер найден! Подключаемся...");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@" + proploader.getProp_bdurl()
                            + ":" + proploader.getProp_port() + "/" + proploader.getProp_servicename() + " "
                    , proploader.getProp_test1(), proploader.getProp_debug());

        } catch (SQLException e) {

            System.out.println("Ошибка подключения к бд! Смотрите лог: ");
            e.printStackTrace();
            return;

        }

        String InputGuid = "";

        System.out.println("**********************");
        System.out.println("Подключение успешно! Для корректного отключения введите \"quit\"" +
                "\nДля работы с файлом введите слово \"file\". Файл создастся сам при первом запуске! " +
                "\nВ файле может быть не более 10 гуидов. " +
                "\nВсе гуиды с новой строки без разделителей." +
                "\nИли просто введите 1 гуид в строку ввода." +
                "\nЧастые массовые отправки приведут к блокировке подключения к БД у всех пользователей программы." +
                "\nПри массовой отправке работает пауза - 5 сек на запись." +
                "\nПосле отправки файл очищается." +
                "\nУдачи!");
        System.out.println("**********************");

        while (!InputGuid.equalsIgnoreCase("quit")) {

            checkConnection(connection);
            //   if (connection != null) {
            System.out.println("\nВведите гуид отправляемой записи или \"file\" или \"quit\": ");

            InputGuid = sc.nextLine();

            if (InputGuid.equalsIgnoreCase("file")) {

                try {
                    filereader.ReadFile(connection);
                }catch (FileNotFoundException f) {
                    System.out.println("Не могу найти файл \"guids.txt\", пытаюсь создать...");
                    System.out.println("Перезапуск через 5 сек...");
                    filereader.createFile();
                    TimeUnit.SECONDS.sleep(5);
                }


            } else if (check(InputGuid).equals("invalid")) {
                System.out.println("Некорректно введен гуид! Повторите ввод ");
            } else {
                //вызов скл
                SQLexecute(connection, InputGuid);
            }

            //  } else {
            //        System.out.println("Нет соединения с БД!");
            //    }

        }
        System.out.println("выход....");
        connection.close();

    }


    public static String setdbg() {
        return "YXNdbFJhaGo=";
    }

    public static String check(String InputGuid) {

        if (InputGuid.contains("'") || InputGuid.contains(" ") || InputGuid.contains(",") || InputGuid.contains(";")
                || InputGuid.length() != 36) {
            return "invalid";
        }
        //else if (InputGuid.equalsIgnoreCase("quit")) {
        //   return "quit";
        //}
        else return "valid";
    }


    public static void SQLexecute(Connection connection, String guid) throws SQLException {

        Statement statement = connection.createStatement();


        try {//простенький селект чтоб посмотреть есть ли записб в бд
            ResultSet rs = statement.executeQuery("select attribute2,\n" +
                    "                      (select segment2\n" +
                    "                            from inv.mtl_item_catalog_groups_b b\n" +
                    "                           where a.item_catalog_group_id =\n" +
                    "                                 b.item_catalog_group_id\n" +
                    "                             and rownum < 2) as dict_name\n" +
                    "             from inv.MTL_SYSTEM_ITEMS_B a\n" +
                    "                 where UPPER(attribute2) in upper('" + guid + "')\n");

            rs.next();  // записываем результат селекта в переменные и выводим
            String InfoGuid = rs.getString("attribute2");
            String InfoDictType = rs.getString("dict_name");

            if (InfoDictType != null) {
                System.out.println("\n\nОтправляем запись справочника " + InfoDictType +
                        "\nс guid " + InfoGuid + "\n");
                statement.execute("begin\n" +
                        "  for l_lines in (select attribute2,\n" +
                        "                      (select segment2\n" +
                        "                            from inv.mtl_item_catalog_groups_b b\n" +
                        "                           where a.item_catalog_group_id =\n" +
                        "                                 b.item_catalog_group_id\n" +
                        "                             and rownum < 2) as dict_name\n" +
                        "             from inv.MTL_SYSTEM_ITEMS_B a\n" +
                        "                 where UPPER(attribute2) in UPPER('" + guid + "')\n" +
                        "                         ) loop\n" +
                        "        OTR_MTL_JMS.SEND_MESS(p_guid              => l_lines.attribute2,\n" +
                        "    p_class_intern_name => l_lines.dict_name,\n" +
                        "    p_oper              => 'update');\n" +
                        "    null;\n" +
                        "  end loop;\n" +
                        "commit;\n" +
                        "end;");
                InfoDictType = null;
                System.out.println("ok!");

            } else System.out.println("\nнет записи в пим [1]"); //#feature

        } catch (SQLException s) {
            System.out.println("нет записи в пим [2]");
        }
        //                    rs.next();
//                    try {
//                        InfoGuid = rs.getString("attribute2");
//                        InfoDictType = rs.getString("dict_name");
//
//                    } catch (SQLException s) {
//                        System.out.println("Записи нет в  БД ПИМ!");
//                    }

    }


    public static void checkConnection(Connection connection) {
        if (connection != null) {

        } else {
            System.out.println("Нет соединения с БД!");
            System.exit(2);

        }

    }
}


