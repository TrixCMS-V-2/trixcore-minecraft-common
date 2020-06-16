package eu.trixcms.trixcore.common.response;

import com.google.gson.annotations.SerializedName;
import lombok.RequiredArgsConstructor;

public class JsonResponse extends Response {

    public JsonResponse(int statusCode, Object data) {
        super(statusCode, new Structure(data));
    }

    @RequiredArgsConstructor
    private static final class Structure {

        @SerializedName("return")
        private final Object data;
    }

}
