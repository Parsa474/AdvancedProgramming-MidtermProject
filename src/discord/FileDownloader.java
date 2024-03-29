package discord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDownloader implements Runnable {
    // Fields:
    private final String username;
    private final DownloadableFile downloadingFile;
    private final View printer;

    // Constructors:
    public FileDownloader(String username, DownloadableFile downloadingFile, View printer) {
        this.username = username;
        this.downloadingFile = downloadingFile;
        this.printer = printer;
    }

    // Methods:
    @Override
    public void run() {
        makeDirectory("Downloads");
        makeDirectory("Downloads" + File.separator + username + "'s downloads");
        makeDirectory("Downloads" + File.separator + username + "'s downloads" + File.separator + "Files");
        String directory = "Downloads" + File.separator + username + "'s downloads" + File.separator + "Files";
        File file = new File(directory, downloadingFile.getFileName());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            printer.println("Download of the file started.");
            fileOutputStream.write(downloadingFile.getBytes());
            printer.println("Download finished. the file saved in " + directory + File.separator + downloadingFile.getFileName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                printer.printErrorMessage("Could not create the " + path + " directory!");
                throw new RuntimeException();
            }
        }
    }
}
