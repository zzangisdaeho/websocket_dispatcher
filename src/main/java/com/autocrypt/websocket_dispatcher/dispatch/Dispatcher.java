package com.autocrypt.websocket_dispatcher.dispatch;

import com.autocrypt.websocket_dispatcher.dispatch.dto.DispatchMessage;
import com.autocrypt.websocket_dispatcher.dispatch.handler.impl.DefaultHandler;
import com.autocrypt.websocket_dispatcher.dispatch.handler.DispatchHandler;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.autocrypt.websocket_dispatcher.dispatch.dto.DispatchMessage.DispatchType;

/**
 * 김대호
 */
@Slf4j
public class Dispatcher {

    private final Map<DispatchType, DispatchHandler> handlers;
    private final DispatchHandler defaultHandler = new DefaultHandler();
    private final Validator validator;

    public Dispatcher(Map<DispatchType, DispatchHandler> handlers) {
        this.handlers = handlers;
        try(var validationFactory = Validation.buildDefaultValidatorFactory()){
            this.validator = validationFactory.getValidator();
        }
    }

    public void routeMessage(DispatchMessage dispatchMessage) {
        checkMessageValidation(dispatchMessage);

        DispatchHandler handler = handlers.getOrDefault(dispatchMessage.getType(), defaultHandler);
        handler.handle(dispatchMessage);
    }

    /**
     * 김대호
     * spring validation 검증
     * @param dispatchMessage
     */
    private void checkMessageValidation(DispatchMessage dispatchMessage) {
        Set<ConstraintViolation<DispatchMessage>> violations = validator.validate(dispatchMessage);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<DispatchMessage> constraintViolation : violations) {
                sb.append(constraintViolation.getPropertyPath()).append(": ").append(constraintViolation.getMessage()).append("\n");
            }
            throw new ConstraintViolationException(sb.toString(), violations);
        }
    }


}