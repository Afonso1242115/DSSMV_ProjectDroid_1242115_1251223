package pt.isep.dssmv.campuslf.model;

// indica se o objeto foi publicado e se ficou guardado no telemóvel
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
