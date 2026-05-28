package com.jyjun.projectbp.common.validation;

public final class ValidationConstants {

    private ValidationConstants() {}

    // DTO에 매번 regax를 설정하기 귀찮아서 만든 상수 모음 클래스


    // 계정명
    // 영문, 숫자, _ . - 만 허용, 3~50자
    public static final String ACCOUNT_NAME_PATTERN = "^[a-zA-Z0-9_.\\-]{3,50}$";
    public static final String ACCOUNT_NAME_MESSAGE = "계정명은 영문, 숫자, 특수문자(_ . -)만 사용할 수 있습니다. (3~50자)";

    // 비밀번호
    // 영문 1자 이상 + 숫자 1자 이상, 8~72자
    public static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9!@#$%^&*()_+=\\-~]{8,72}$";
    public static final String PASSWORD_MESSAGE = "비밀번호는 영문과 숫자를 각각 1자 이상 포함해야 합니다. (8~72자)";

    // 표시용 이름
    // 영문, 숫자, 한글, 공백, _ . - (2~100자)
    public static final String DISPLAY_NAME_PATTERN = "^[a-zA-Z0-9가-힣 _.\\-]{2,100}$";
    public static final String DISPLAY_NAME_MESSAGE = "이름에 사용할 수 없는 문자가 포함되어 있습니다. (2~100자)";

    // 버전
    // 영문, 숫자, . _ - (1~50자)
    public static final String VERSION_PATTERN = "^[a-zA-Z0-9][a-zA-Z0-9._\\-]{0,49}$";
    public static final String VERSION_MESSAGE = "버전 형식이 올바르지 않습니다. (영문, 숫자, . _ - 사용 가능, 최대 50자)";

    public static final int PATCH_NOTE_MAX_LENGTH = 4096;
    public static final int GAME_TITLE_MAX_LENGTH = 200;
    public static final int GAME_DESC_MAX_LENGTH = 2000;
}
