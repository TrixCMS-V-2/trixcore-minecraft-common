package eu.trixcms.trixcore.common.response;

import lombok.RequiredArgsConstructor;

public class ErrorResponse extends JsonResponse {

    public ErrorResponse(int statusCode, String data) {
        super(statusCode, new Error(data));
    }

    @RequiredArgsConstructor
    private static class Error {
        private boolean error = true;
        private final String message;
    }
}
