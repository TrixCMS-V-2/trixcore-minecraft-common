package eu.trixcms.trixcore.common.fixtures;

import eu.trixcms.trixcore.common.response.Response;

public enum EJsonResponseFixture {

    OK(200, "OK"),
    NOT_FOUND(404, "NOT FOUND"),

    ;

    private int status;
    private Object data;

    EJsonResponseFixture(int status, Object data) {
        this.status = status;
        this.data = data;
    }

    public Response create() {
        return create(status, data);
    }

    public Response create(int status, Object data) {
        return new Response(status, data);
    }
}
