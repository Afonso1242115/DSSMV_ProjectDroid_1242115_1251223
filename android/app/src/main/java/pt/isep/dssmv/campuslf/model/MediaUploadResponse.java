package pt.isep.dssmv.campuslf.model;

import java.util.Collections;
import java.util.List;

/** RESTDB POST /media result: {"msg":"OK","uploadid":"...","ids":["..."]}. */
public final class MediaUploadResponse {
    private String msg;
    private List<String> ids;

    public List<String> getIds() { return ids == null ? Collections.emptyList() : ids; }
    public String getFirstId() { return getIds().isEmpty() ? null : getIds().get(0); }
}
