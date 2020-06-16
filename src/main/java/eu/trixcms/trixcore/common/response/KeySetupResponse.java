package eu.trixcms.trixcore.common.response;

import eu.trixcms.trixcore.api.util.ServerTypeEnum;
import lombok.RequiredArgsConstructor;

public class KeySetupResponse extends JsonResponse {

    public KeySetupResponse(String message, ServerTypeEnum serverType) {
        super(200, new KeyResponse(message, serverType));
    }

    @RequiredArgsConstructor
    private static final class KeyResponse {
        private final String message;
        private final ServerTypeEnum serverType;
    }

}
