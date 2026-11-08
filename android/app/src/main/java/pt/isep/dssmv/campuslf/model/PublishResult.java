package pt.isep.dssmv.campuslf.model;

/** A successful server publication is distinct from a subsequent local cache write. */
public final class PublishResult {
    public final String recordId;
    public final boolean cached;
    public final boolean photoConfirmed;

    public PublishResult(String recordId, boolean cached) {
        this(recordId, cached, true);
    }

    public PublishResult(String recordId, boolean cached, boolean photoConfirmed) {
        this.recordId = recordId;
        this.cached = cached;
        this.photoConfirmed = photoConfirmed;
    }
}
