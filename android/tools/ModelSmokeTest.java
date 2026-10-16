import com.campuslf.app.model.FoundItem;
import java.time.LocalDate;

/** Testes simples do modelo que podem ser executados sem iniciar o Android. */
public final class ModelSmokeTest {
    private static int passed = 0;

    public static void main(String[] args) {
        shouldCreateValidItem();
        shouldRejectShortTitle();
        shouldRejectFutureDate();
        System.out.println("Passed tests: " + passed);
    }

    private static void shouldCreateValidItem() {
        FoundItem item = new FoundItem(
                0,
                "Student card",
                "Student card found in a classroom.",
                "Documents",
                "Building B",
                LocalDate.now());

        check("Student card".equals(item.getTitle()), "valid item title");
    }

    private static void shouldRejectShortTitle() {
        expectIllegalArgument(() -> new FoundItem(
                0,
                "A",
                "Valid description",
                "Other",
                "Library",
                LocalDate.now()), "short title");
    }

    private static void shouldRejectFutureDate() {
        expectIllegalArgument(() -> new FoundItem(
                0,
                "Valid title",
                "Valid description",
                "Other",
                "Library",
                LocalDate.now().plusDays(1)), "future date");
    }

    private static void expectIllegalArgument(Runnable action, String name) {
        try {
            action.run();
            throw new AssertionError("Expected IllegalArgumentException: " + name);
        } catch (IllegalArgumentException expected) {
            passed++;
        }
    }

    private static void check(boolean condition, String name) {
        if (!condition) {
            throw new AssertionError("Failed: " + name);
        }
        passed++;
    }
}
