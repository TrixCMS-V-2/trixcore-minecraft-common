package eu.trixcms.trixcore.common.request;

import eu.trixcms.trixcore.api.config.IConfig;
import eu.trixcms.trixcore.api.i18n.ITranslator;
import eu.trixcms.trixcore.api.method.IMethodsManager;
import eu.trixcms.trixcore.api.method.exception.ArgsPreconditionFailedException;
import eu.trixcms.trixcore.api.method.exception.MethodNotFoundException;
import eu.trixcms.trixcore.api.request.IRequestHandler;
import eu.trixcms.trixcore.api.request.exception.JsonMalformedException;
import eu.trixcms.trixcore.api.response.IResponse;
import eu.trixcms.trixcore.api.util.GsonParser;
import eu.trixcms.trixcore.api.util.ServerTypeEnum;
import eu.trixcms.trixcore.common.response.ErrorResponse;
import eu.trixcms.trixcore.common.response.JsonResponse;
import eu.trixcms.trixcore.common.response.KeySetupResponse;
import eu.trixcms.trixcore.common.response.NotFoundResponse;
import lombok.AllArgsConstructor;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@AllArgsConstructor
public class RequestHandler extends AbstractHandler implements IRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final IMethodsManager methodManager;
    private final IConfig config;
    private final ITranslator translator;
    private final ServerTypeEnum serverType;

    @Override
    public IResponse dispatch(String methodName, String[] args) throws MethodNotFoundException, ArgsPreconditionFailedException {
        return methodManager.dispatch(methodName, args);
    }

    private JsonRequest build(HttpServletRequest httpServletRequest) throws Exception {
        if (config.getSecretKey().isEmpty()) {
            if (httpServletRequest.getParameter("method") != null && !httpServletRequest.getParameter("method").isEmpty() &&
                    httpServletRequest.getParameter("args") != null && !httpServletRequest.getParameter("args").isEmpty()) {
                return new JsonRequest(httpServletRequest.getParameter("method"), httpServletRequest.getParameterValues("args"));
            }
        } else {
            if (httpServletRequest.getParameter("iv") != null && !httpServletRequest.getParameter("iv").isEmpty() &&
                    httpServletRequest.getParameter("data") != null && !httpServletRequest.getParameter("data").isEmpty()) {

                JsonEncryptedRequest encryptedRequest = new JsonEncryptedRequest(httpServletRequest.getParameter("data"), httpServletRequest.getParameter("iv"));
                DecryptRequest decryptRequest = new DecryptRequest(encryptedRequest, config.getSecretKey());

                try {
                    return new GsonParser<JsonRequest>().deserialize(JsonRequest.class, decryptRequest.data());
                } catch (Exception e) {
                    throw new JsonMalformedException(decryptRequest.data());
                }
            }
        }

        return null;
    }

    private IResponse handle(String target, Request request, HttpServletRequest httpServletRequest) {
        if (target.equals("/req") && request.getMethod().equals("POST")) {
            JsonRequest jsonRequest;

            try {
                jsonRequest = build(httpServletRequest);
            } catch (JsonMalformedException e) {
                logger.error("Malformed json ", e);
                return new ErrorResponse(500, "malformed json");
            } catch (BadPaddingException e) {
                logger.error("Unable to decrypt request ", e);
                return new ErrorResponse(500, "unable to decrypt request");
            } catch (Exception e) {
                logger.error("Unable to process request ", e);
                return new ErrorResponse(500, "unable to process request");
            }

            if (config.getSecretKey().isEmpty()) {
                if (httpServletRequest.getContentType() != null && !httpServletRequest.getContentType().equals("application/x-www-form-urlencoded")) {
                    return new ErrorResponse(500, "bad request");
                }

                if (jsonRequest == null)
                    return new ErrorResponse(500, translator.of("HTTP_INVALID_ARGS_AMOUNT"));

                if (jsonRequest.method().equals("PutSKConnection")) {
                    if (jsonRequest.args().length != 1) {
                        return new ErrorResponse(500, translator.of("HTTP_INVALID_ARGS_AMOUNT"));
                    }

                    if (jsonRequest.args()[0].isEmpty()) {
                        return new ErrorResponse(500, translator.of("HTTP_INVALID_ARG"));
                    }

                    String secretKey = jsonRequest.args()[0];
                    logger.info(translator.of("SAVER_SAVE_SECRET_KEY", secretKey));

                    try {
                        config.saveSecretKey(secretKey);
                        logger.info(translator.of("SAVER_SECRET_KEY_SAVED"));
                        return new KeySetupResponse(translator.of("SAVER_SECRET_KEY_SAVED"), serverType);
                    } catch (Exception e) {
                        logger.error(translator.of("SAVER_SAVE_SECRET_KEY_ERROR"), e);
                        return new ErrorResponse(500, translator.of("SAVER_SAVE_SECRET_KEY_ERROR"));
                    }
                } else {
                    logger.error(translator.of("HTTP_TRIXCORE_NOT_LINKED"));
                    return new ErrorResponse(500, translator.of("HTTP_TRIXCORE_NOT_LINKED"));
                }
            } else {
                if (jsonRequest == null) {
                    return new ErrorResponse(502, "bad request");
                }
                logger.info(translator.of("HTTP_RUNNING_METHOD", jsonRequest.method(), Arrays.toString(jsonRequest.args())));
                try {
                    return dispatch(jsonRequest.method(), jsonRequest.args());
                } catch (MethodNotFoundException e) {
                    logger.warn(translator.of("HTTP_METHOD_NOT_FOUND", jsonRequest.method()));
                    return new ErrorResponse(404, translator.of("HTTP_METHOD_NOT_FOUND", jsonRequest.method()));
                } catch (ArgsPreconditionFailedException e) {
                    logger.warn(translator.of("HTTP_INVALID_ARG"), e);
                    return new ErrorResponse(403, translator.of("HTTP_INVALID_ARG"));
                }
            }
        }
        return new NotFoundResponse(target);
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        IResponse response;
        try {
            response = handle(target, request, httpServletRequest);
        } catch (Exception e) {
            logger.error("Internal Server Error ", e);
            response = new ErrorResponse(500, "Internal Server Error");
        }

        if (response instanceof JsonResponse)
            httpServletResponse.setContentType("application/json");

        httpServletResponse.setStatus(response.statusCode());
        httpServletResponse.getOutputStream().write(new GsonParser<>().serialize(response.data()).getBytes());
        request.setHandled(true);
    }
}
