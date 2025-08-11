/**
 * File: ResultCode
 * Author: will.liu
 * Date: 2025/8/11 18:22
 * Description:
 */
public enum ResultCode implements IErrorCode{

    SUCCESS(20000, "操作成功"),
    FAILED(50000, "操作失败"),
    VALIDATE_FAILED(40400, "参数检验失败"),
    UNAUTHORIZED(40100, "暂未登录或token已经过期"),
    FORBIDDEN(40300, "没有相关权限");
    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
