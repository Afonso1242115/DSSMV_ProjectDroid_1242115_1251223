package pt.isep.dssmv.campuslf.model;

/** A successful server publication is distinct from a subsequent local cache write. */
public final class PublishResult {
    public final String recordId;
    public final boolean cached;

    public PublishResult(String recordId, boolean cached) {
        this.recordId = recordId;
        this.cached = cached;
    }
}
