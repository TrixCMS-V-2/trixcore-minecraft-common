package eu.trixcms.trixcore.common.response;

public class NotFoundResponse extends ErrorResponse {

    public NotFoundResponse(String uri) {
        super(404, uri + " not found");
    }

}
