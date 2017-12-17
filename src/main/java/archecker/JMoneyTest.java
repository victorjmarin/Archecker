package archecker;

import java.io.IOException;
import archecker.compliance.JarChecker;

public class JMoneyTest {

  public static void main(final String[] args) throws IOException {
    final String[] a = {"projects/jmoney.jar", "spec/arch.json", "spec/mappings.json"};
    JarChecker.main(a);
  }

}
