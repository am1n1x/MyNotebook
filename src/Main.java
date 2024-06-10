import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Collections;


public class Main {
    public static final String FILE_NAME = "notes.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Список действий:\n1. Добавить запись\n2. Список записей\n3. Статистика\n4. Поиск записей\n0. Выход\nВведите номер действия:");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    addNote(scanner);
                    break;
                case 2:
                    readNotes();
                    break;
                case 3:
                    showStatistics();
                    break;
                case 4:
                    findNote(scanner);
                    break;
                case 0:
                    System.out.println("Завершение работы...");
                    return;
                default:
                    System.out.println("Пожалуйста, введите 1, 2, 3, 4 или 0.");
            }
        }
    }

    private static void addNote(Scanner scanner) {
        System.out.println("Введите запись:");
        String note = scanner.nextLine();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        String record = timestamp + ", " + note;

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_NAME), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(record);
            writer.newLine();
            System.out.println("Запись добавлена.");
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    private static void readNotes() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            System.out.println("Список записей:");
                for (String line : lines) {
                    System.out.println(line);
                }

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    public static void showStatistics() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            int notesCount = lines.size();
            int symbolsCount = 0;
            Map<LocalDate, Integer> notesPerDay = new HashMap<>();

            for (String line : lines) {
                String[] parts = line.split(",", 2);
                symbolsCount += parts[1].trim().length();               // Считает символы  только в тексте заметки (символы даты и времени не учитываются)

                LocalDateTime dateTime = LocalDateTime.parse(parts[0], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                LocalDate date = dateTime.toLocalDate();
                notesPerDay.put(date, notesPerDay.getOrDefault(date, 0) + 1);
            }

            System.out.println("Статистика:");
            System.out.println("Количество записей: " + notesCount);
            System.out.println("Общее количество символов в записях: " + symbolsCount);
            System.out.println("Самый активный день: " + Collections.max(notesPerDay.entrySet(), Map.Entry.comparingByValue()).getKey().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    public static void findNote(Scanner scanner) {
        while (true) {
            System.out.println("Список действий:\n1. Поиск по дате\n2. Поиск по заметкам\n0. Назад\nВведите номер действия:");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    findByDate(scanner);
                    break;
                case 2:
                    findByWord(scanner);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Пожалуйста, введите 1, 2 или 0.");
            }
        }
    }

    private static void findByDate(Scanner scanner) {                                     // Ищет только по дате, а не наличию даты в тексте заметки
        System.out.println("Введите дату в формате dd.MM.yyyy:");
        String date = scanner.nextLine();
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            System.out.println("Записи за " + date + ":");
            for (String line : lines) {
                String[] parts = line.split(",", 2);
                if (parts[0].contains(date)) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    private static void findByWord(Scanner scanner) {                                   // Ищет по наличию символов/слов в тексте заметок
        System.out.println("Введите слово для поиска:");
        String word = scanner.nextLine();
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            System.out.println("Записи, содержащие слово " + word + ":");
            for (String line : lines) {
                String[] parts = line.split(",", 2);
                if (parts[1].contains(word)) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}
