package pusher;


import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.concurrent.TimeUnit;

public class filereader {

    private static final File pushguids = new File("guids.txt");

    public static void ReadFile(Connection connection) throws IOException, InterruptedException {
        if (countLines() > 10) {//количество гуидов в файле
            System.out.println("\nВ файле слишком много гуидов! "+ countLines() + " шт. Максимум 10.");

        } else {

            System.out.println("\n*****************НАЧАЛО РАБОТЫ c файлом *****************");
            BufferedReader reader = new BufferedReader(new FileReader(pushguids));//каждый раз открываю ридер, чтоб сбросить его к началу
            String guid = reader.readLine();
            int count = 1;
            try {
                while (guid != null) {
                    System.out.println("\n\nгуид номер " + count+": ");


                    if (main.check(guid).equals("invalid")) {
                        System.out.println("В файле кривой гуид на строке " +count );
                    } else {
                        //вызов скл
                        main.SQLexecute(connection, guid);
                    }
                    System.out.println("Пауза 5 сек...");
                    TimeUnit.SECONDS.sleep(5);
                    count++;
                    //eh_requestGenerator.callSoapWebService(endpoint, soapAction, guid); //создаем соап запрос для этого гуида
                    // считываем остальные строки в цикле
                    guid = reader.readLine();
                }

            } catch (FileNotFoundException e) {

                System.out.println("Не могу найти файл \"guids.txt\", пытаюсь создать...");
                System.out.println("Перезапуск через 5 сек...");
                createFile();
                TimeUnit.SECONDS.sleep(5);


                //main.run();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            reader.close();
            truncFile();//очистка файла
        }

    }

    public static void truncFile() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(pushguids);
        writer.print("");
        writer.close();
    }

    public static int countLines() throws IOException, InterruptedException {
        int linescount = 0;
        BufferedReader reader = new BufferedReader(new FileReader(pushguids));//каждый раз открываю ридер, чтоб сбросить его к началу
        String line = reader.readLine();
        try {
            while (line != null) {
                linescount++;
                line = reader.readLine();
            }

        } catch (FileNotFoundException e) {

            System.out.println("Не могу найти файл \"guids.txt\", пытаюсь создать...");
            System.out.println("Перезапуск через 5 сек...");
            createFile();
            TimeUnit.SECONDS.sleep(5);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return linescount;

    }

    public static void createFile() throws IOException {
        pushguids.createNewFile();
    }
}
