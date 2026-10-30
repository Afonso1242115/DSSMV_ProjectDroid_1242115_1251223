import java.time.LocalDate;
import pt.isep.dssmv.campuslf.model.FoundItem;

/** Plain Java smoke tests; no Android runtime or third-party dependencies are required. */
public final class ModelSmokeTest {
    private static int passed;

    public static void main(String[] args) {
        acceptsValidItem();
        trimsText();
        rejectsShortTitle();
        rejectsExcessivelyLongTitle();
        rejectsBlankDescription();
        rejectsNullDate();
        rejectsFutureDate();
        rejectsNegativeId();
        rejectsShortLocation();
        System.out.println("Passed model tests: " + passed);
    }

    private static FoundItem item(long id, String title, String description,
                                  String location, LocalDate date) {
        return new FoundItem(id, title, description, "Documents", location, date);
    }

    private static void acceptsValidItem() {
        FoundItem value = item(0, "Student card", "Card found in a classroom.",
                "Building B", LocalDate.now());
        check("Student card".equals(value.getTitle()), "valid title");
    }

    private static void trimsText() {
        FoundItem value = item(3, "  Student card  ", "Card found in a classroom.",
                "  Building B  ", LocalDate.now());
        check("Student card".equals(value.getTitle())
                && "Building B".equals(value.getFoundLocation()), "trimmed text");
    }

    private static void rejectsShortTitle() {
        rejects(() -> item(0, "A", "Valid description", "Library", LocalDate.now()), "short title");
    }

    private static void rejectsExcessivelyLongTitle() {
        rejects(() -> item(0, "A".repeat(FoundItem.MAX_TITLE_LENGTH + 1),
                "Valid description", "Library", LocalDate.now()), "long title");
    }

    private static void rejectsBlankDescription() {
        rejects(() -> item(0, "Valid title", " ", "Library", LocalDate.now()), "blank description");
    }

    private static void rejectsNullDate() {
        rejects(() -> item(0, "Valid title", "Valid description", "Library", null), "null date");
    }

    private static void rejectsFutureDate() {
        rejects(() -> item(0, "Valid title", "Valid description", "Library",
                LocalDate.now().plusDays(1)), "future date");
    }

    private static void rejectsNegativeId() {
        rejects(() -> item(-1, "Valid title", "Valid description", "Library", LocalDate.now()),
                "negative ID");
    }

    private static void rejectsShortLocation() {
        rejects(() -> item(0, "Valid title", "Valid description", "X", LocalDate.now()),
                "short location");
    }

    private static void rejects(Runnable action, String name) {
        boolean rejected = false;
        try { action.run(); }
        catch (IllegalArgumentException expected) { rejected = true; }
        check(rejected, name);
    }

    private static void check(boolean ok, String name) {
        if (!ok) throw new AssertionError("Failed model test: " + name);
        passed++;
    }
}
