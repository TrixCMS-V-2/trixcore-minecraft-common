package eu.trixcms.trixcore.common.response;

public class SuccessResponse extends JsonResponse {

    public SuccessResponse(Object data) {
        super(200, data);
    }

}
