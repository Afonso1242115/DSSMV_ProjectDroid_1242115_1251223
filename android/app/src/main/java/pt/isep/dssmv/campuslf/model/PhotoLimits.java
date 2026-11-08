package pt.isep.dssmv.campuslf.model;

/** Base64 expands a 60 kB JPEG to 80 kB; reserve room for the document's other fields. */
public final class PhotoLimits {
    public static final int MAX_JPEG_BYTES = 60_000;
    public static final int MAX_DOCUMENT_BYTES = 90_000;

    private PhotoLimits() { }
}
