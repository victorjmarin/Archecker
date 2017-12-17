package archecker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {

  public static String read(final String path) throws IOException {
    return new String(Files.readAllBytes(Paths.get(path)));
  }

  public static void saveResult(final String fileName, final String result) {
    PrintWriter writer;
    try {
      writer = new PrintWriter(fileName + ".txt", "UTF-8");
      writer.println(result);
      writer.close();
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    } catch (final UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

}
