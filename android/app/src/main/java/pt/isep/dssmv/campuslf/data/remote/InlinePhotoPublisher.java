package pt.isep.dssmv.campuslf.data.remote;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import io.reactivex.rxjava3.core.Single;
import pt.isep.dssmv.campuslf.model.NewFoundItem;
import pt.isep.dssmv.campuslf.model.PhotoLimits;
import pt.isep.dssmv.campuslf.model.PublishDraft;
import pt.isep.dssmv.campuslf.model.RemoteFoundItem;
import pt.isep.dssmv.campuslf.util.ErrorMessages;

/** Sends the photo and item in one JSON document through the collection API. */
public final class InlinePhotoPublisher {
    private InlinePhotoPublisher() { }

    public static Single<Receipt> publish(RestDbService api, PublishDraft draft, byte[] jpeg) {
        return Single.defer(() -> {
            if (jpeg == null || jpeg.length == 0 || jpeg.length > PhotoLimits.MAX_JPEG_BYTES) {
                return Single.error(new IOException("Photo must be a JPEG of at most 60 kB."));
            }
            String encoded = Base64.getEncoder().encodeToString(jpeg);
            NewFoundItem body = draft.withPhotoBase64(encoded);
            int jsonBytes = RestDbJson.create().toJson(body).getBytes(StandardCharsets.UTF_8).length;
            if (jsonBytes > PhotoLimits.MAX_DOCUMENT_BYTES) {
                return Single.error(new IOException(
                        "Photo and item details are too large. Shorten the description or take another photograph."));
            }
            return api.createItem(body)
                    .onErrorResumeNext(error -> Single.error(new IOException(
                            ErrorMessages.describePhotoPublication(error, jpeg.length, jsonBytes), error)))
                    .flatMap(created -> {
                        if (created.getId() == null || created.getId().isEmpty()) {
                            return Single.error(new IOException(
                                    "Photo publication returned no record ID. Check the list before trying again."));
                        }
                        if (encoded.equals(created.getPhotoBase64())) {
                            return Single.just(new Receipt(created, true));
                        }
                        // Some POST responses omit fields. Read back once before confirming the photo.
                        // A failed verification must not turn an accepted POST into a retry/duplicate.
                        return api.getItem(created.getId()).map(stored -> {
                                    boolean confirmed = created.getId().equals(stored.getId())
                                            && encoded.equals(stored.getPhotoBase64());
                                    return new Receipt(confirmed ? stored : created, confirmed);
                                })
                                .onErrorReturn(error -> new Receipt(created, false));
                    });
        });
    }

    public static final class Receipt {
        public final RemoteFoundItem item;
        public final boolean photoConfirmed;

        private Receipt(RemoteFoundItem item, boolean photoConfirmed) {
            this.item = item;
            this.photoConfirmed = photoConfirmed;
        }
    }
}
