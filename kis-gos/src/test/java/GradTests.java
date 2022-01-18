import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;

public class GradTests {
    @ParameterizedTest
    @ValueSource(strings = {"img_1.png", "img_2.png", "img.png"})
    public void test(String source) {
        try {
            String file = Paths.get(Objects.requireNonNull(this.getClass().getResource(source)).toURI())
                    .toFile().getAbsolutePath();
            String toFile = Files.createTempFile("imgs", "out.ong").toFile().getAbsolutePath();

            new ApplicationBootstrap().saveGradPicture(file, toFile);

        } catch (IOException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                IllegalAccessException e) {
            fail();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
