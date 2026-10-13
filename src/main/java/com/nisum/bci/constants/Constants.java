package com.nisum.bci.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String EMAIL_CLAIM = "email";
    public static final String LOG_EXCEPTION = "action=controllerException";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String REQUEST_ID_MDC_KEY = "requestId";

    public static final String MSG_EMAIL_ALREADY_EXISTS = "El correo ya registrado";
    public static final String MSG_INVALID_EMAIL = "El formato del correo es inválido";
    public static final String MSG_INVALID_PASSWORD = "La contraseña debe tener al menos 8 caracteres, "
            + "una mayúscula, un número y un carácter especial (@#$%^&+=)";
    public static final String MSG_NAME_REQUIRED = "El nombre es obligatorio";
    public static final String MSG_EMAIL_REQUIRED = "El correo es obligatorio";
    public static final String MSG_PASSWORD_REQUIRED = "La contraseña es obligatoria";
    public static final String MSG_PHONE_REQUIRED = "El teléfono no puede ser nulo";
    public static final String MSG_PHONE_NUMBER_REQUIRED = "El número de teléfono es obligatorio";
    public static final String MSG_MALFORMED_REQUEST = "El cuerpo de la solicitud es inválido o está mal formado";
    public static final String MSG_UNSUPPORTED_MEDIA_TYPE = "El Content-Type debe ser application/json";
    public static final String MSG_METHOD_NOT_ALLOWED = "Método HTTP no soportado para este recurso";
    public static final String MSG_INTERNAL_ERROR = "Error interno del servidor";
}
