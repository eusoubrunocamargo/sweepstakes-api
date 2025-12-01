package com.brunothecoder.sweepstakes.api.exceptions;

public class ErrorMessages {



    private ErrorMessages() {}

    // === Pool ===
    public static final String BELOW_POOL_MIN = "Aposta abaixo do valor mínimo deste bolão";
    public static final String ABOVE_POOL_MAX = "Aposta acima do valor máximo deste bolão";
    public static final String POOL_NOT_FOUND = "Pool not found!";
    public static final String POOL_CLOSED = "This pool is closed for new participants!";
    public static final String POOL_DEADLINE_PASSED = "This pool is no longer accepting participants!";
    public static final String POOL_CAPACITY_EXCEEDED = "Pool has reached its maximum number of participants!";
    public static final String POOL_KEYWORD_INVALID = "Invalid keyword for this pool!";
    public static final String POOL_ALREADY_EXISTS = "Já existe um bolão ativo com este nome!";
    public static final String MAX_ACTIVE_POOLS = "Máximo de 5 bolões ativos!";

    // === User / Participant ===
    public static final String USER_NOT_FOUND = "User not found!";
    public static final String USER_ALREADY_REGISTERED = "Usuário já registrado!";
    public static final String USER_INACTIVE = "User is inactive!";
    public static final String USER_NOT_VERIFIED = "User is not verified!";
    public static final String PARTICIPANT_ALREADY_JOINED = "You already joined this pool!";

    // === Valores / Campos ===
    public static final String NICKNAME_SIZE = "Nickname length is out of allowed bounds!";
    public static final String MAX_VALUE_REQUIRED = "The maximum bet value is required!";
    public static final String MAX_VALUE_NEGATIVE = "The maximum bet value cannot be negative!";
    public static final String MAX_VALUE_TOO_HIGH = "The maximum bet value exceeds the allowed limit!";

    // === Mensagens genéricas de validação (se precisar no DTO) ===
    public static final String FIELD_REQUIRED = "This field is required!";
    public static final String MUST_BE_FUTURE = "The date must be in the future!";
    public static final String SIZE_EXCEEDED = "The maximum allowed size was exceeded!";

}
