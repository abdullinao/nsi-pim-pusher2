package pusher;

import java.sql.*;
import java.util.Scanner;

public class main {

    public static void main(String[] args) throws SQLException {
        proploader proploader = new proploader();
        System.out.println("-------- Oracle JDBC Connection starting ------");

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.out.println("NO JDBC DRIVER FOUND");
            e.printStackTrace();
            return;
        }

        System.out.println("Oracle JDBC Driver FOUND!");

        Connection connection = null;

        try {

//делаем урл jdbc:oracle:thin:@localhost:1521/xe", "user", "password"
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@" + proploader.getProp_bdurl()
                            + ":" + proploader.getProp_port() + "/" + proploader.getProp_servicename() + " "
                    , proploader.getProp_user(), proploader.getProp_pwd());

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;

        }

        String InputGuid = "";

        System.out.println("**********************");
        System.out.println("Для остановки введите quit");
        System.out.println("**********************");

        while (!InputGuid.equalsIgnoreCase("quit")) {
            if (connection != null) {
                System.out.println("\nВведите гуид отправляемой записи: ");
                Scanner sc = new Scanner(System.in);
                InputGuid = sc.nextLine();

                if (check(InputGuid).equals("invalid")) {
                    System.out.println("Некорректно введен гуид! Повторите ввод ");

                } else {

                    Statement statement = connection.createStatement();



                    try {//простенький селект чтоб посмотреть есть ли записб в бд
                        ResultSet rs = statement.executeQuery("select attribute2,\n" +
                                "                      (select segment2\n" +
                                "                            from inv.mtl_item_catalog_groups_b b\n" +
                                "                           where a.item_catalog_group_id =\n" +
                                "                                 b.item_catalog_group_id\n" +
                                "                             and rownum < 2) as dict_name\n" +
                                "             from inv.MTL_SYSTEM_ITEMS_B a\n" +
                                "                 where UPPER(attribute2) in upper('" + InputGuid + "')\n" +
                                "                        ");

                        rs.next();  // записываем результат селекта в переменные и выводим
                        String InfoGuid = rs.getString("attribute2");
                        String InfoDictType = rs.getString("dict_name");

                        if (InfoDictType != null) {
                            System.out.println("\n\n\nОтправляем запись справочника " + InfoDictType +
                                    "\nс guid " + InfoGuid + "\n");
                            statement.execute("begin\n" +
                                    "  for l_lines in (select attribute2,\n" +
                                    "                      (select segment2\n" +
                                    "                            from inv.mtl_item_catalog_groups_b b\n" +
                                    "                           where a.item_catalog_group_id =\n" +
                                    "                                 b.item_catalog_group_id\n" +
                                    "                             and rownum < 2) as dict_name\n" +
                                    "             from inv.MTL_SYSTEM_ITEMS_B a\n" +
                                    "                 where UPPER(attribute2) in UPPER('" + InputGuid + "')\n" +
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

            } else {
                System.out.println("Нет соединения с БД!");
            }

        }
    }

    public static String check(String InputGuid) {

        if (InputGuid.contains("'") || InputGuid.contains(" ") || InputGuid.contains(",") || InputGuid.contains(";")
                || InputGuid.length() != 36) {
            return "invalid";
        } else return "valid";
    }


}


