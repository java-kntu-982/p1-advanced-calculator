package ir.ac.kntu.style;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckStyleTest {

    private static final List<File> FILES = new ArrayList<>();

    @BeforeAll
    public static void prepare() throws MalformedURLException {
        File root = new File(new File("src").toURI()
                .toURL().getPath());
        System.out.println("Selecting root as " + root);
        listFiles(FILES, root, "java");
        System.out.println("Found " + FILES.size() + " Java source files.");
    }

    @Test
    public void testCheckStyleCoding() {
        testCheckStyle("config.xml");
    }

    @Test
    public void testCheckStyleNaming() {
        testCheckStyle("naming.xml");
    }

    public void testCheckStyle(String config) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AuditListener listener = new DefaultLogger(baos, AutomaticBean
                .OutputStreamOptions.NONE);

        File conf = new File("src/test/java/ir/ac/kntu/style/" +
                config);
        InputSource inputSource = null;
        try {
            inputSource = new InputSource(new FileInputStream(conf));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CheckStyleTest.class.getName()).log(Level.SEVERE
                    , "Error"
                    , ex);
        }

        Configuration configuration = null;
        try {
            configuration = ConfigurationLoader.loadConfiguration(inputSource,
                    new PropertiesExpander(System.getProperties()),
                    ConfigurationLoader.IgnoredModulesOptions.OMIT);
        } catch (CheckstyleException ex) {
            Logger.getLogger(CheckStyleTest.class.getName()).log(Level.WARNING,
                    null, ex);
        }
        Checker checker = new Checker();
        checker.setModuleClassLoader(Checker.class.getClassLoader());
        try {
            assert configuration != null;
            checker.configure(configuration);
        } catch (CheckstyleException ex) {
            Logger.getLogger(CheckStyleTest.class.getName()).log(Level.WARNING,
                    "Error", ex);
        }
        checker.addListener(listener);

        int errors = 0;
        try {
            errors = checker.process(FILES);
        } catch (CheckstyleException ex) {
            Logger.getLogger(CheckStyleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Found " + errors + " check style errors.");
        System.out.println(baos.toString());
        assertEquals(0, errors, errors + " check style errors " +
                "found. " + baos.toString());
        checker.destroy();
    }

    private static void listFiles(final List<File> files, final File folder,
                                  final String extension) {
        if (folder.canRead()) {
            if (folder.isDirectory()) {
                for (File file : Objects.requireNonNull(folder.listFiles())) {
                    listFiles(files, file, extension);
                }
            } else if (folder.toString().endsWith("." + extension)) {
                if (!folder.toString().contains("module-info.java")) {
                    files.add(folder);
                }
            }
        }
    }
}
