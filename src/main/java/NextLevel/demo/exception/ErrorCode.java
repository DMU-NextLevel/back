package NextLevel.demo.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // no authentication : 공통
    NEED_ADDITIONAL_DATA(HttpStatus.FORBIDDEN, "00403","Additional data required"),
    NO_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "00401","No authentication please login"),
    INPUT_REQUIRED_PARAMETER(HttpStatus.BAD_REQUEST, "00404", "필수 파라미터를 입력해 주세요") ,
    NOT_AUTHOR(HttpStatus.BAD_REQUEST, "00001","작성자가 아닙니다"),
    ACCESS_TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "00002", "access, refresh token을 다시 발급해 주세요"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "00003", "not found %s"),

    //register
    ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "01001","email already exists"),
    ALREADY_EXISTS_NICKNAME(HttpStatus.BAD_REQUEST, "01002","nickname already exists"),
    SEND_EMAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "01003", "send email error"),
    NOT_CORRECT_EMAIL_KEY(HttpStatus.BAD_REQUEST, "01004", "wrong email key"),

    // user / my page
    CAN_NOT_CHANGE_EMAIL(HttpStatus.BAD_REQUEST, "05001", "can not change email"),
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "05002","Login failed"),
    CAN_NOT_INVOKE(HttpStatus.BAD_REQUEST, "05003", "not found column name %s"),
    INVALID_NUMBER_FORMAT(HttpStatus.BAD_REQUEST, "05004", "invalid number format %s"),

    // img 관련 error
    ERROR_ON_SAVE_IMG(HttpStatus.CONFLICT, "03001","error on save img"),
    ERROR_ON_DELETE_IMG(HttpStatus.CONFLICT, "03002","error on delete img"),
    WRONG_IMG_PATH(HttpStatus.INTERNAL_SERVER_ERROR, "03004","wrong image path %s"),

    // project
    NOT_CORRECT_TAG_SIZE(HttpStatus.BAD_REQUEST, "04001","invalidated tag input") ,
    ERROR_EXPIRED_DATE_CONVERSION(HttpStatus.BAD_REQUEST, "04003","can not convert date : %s"),
    START_MUST_BEFORE_EXPIRED(HttpStatus.BAD_REQUEST, "04004","start must be before expired"),

    // funding
    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "05001","not enough point left:%s, need:%s"),
    ALREADY_USED_COUPON(HttpStatus.BAD_REQUEST, "05002","already used coupon"),
    CAN_USE_COUPONS_AT_ONE_OPTION(HttpStatus.BAD_REQUEST, "05003", "can not user coupons at one option"),

    // option

    // follow
    CAN_NOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "07001","can not follow self"),

    // 시발 이게 뭐지? error
    SIBAL_WHAT_IS_IT(HttpStatus.INTERNAL_SERVER_ERROR, "05001","알지 모르는 error 발생 : %s");

    public HttpStatus statusCode;
    public String CustomErrorCode;
    public String errorMessage;

    ErrorCode(HttpStatus statusCode, String CustomErrorCode, String errorMessage) {
        this.statusCode = statusCode; this.errorMessage = errorMessage; this.CustomErrorCode = CustomErrorCode;
    }
}
