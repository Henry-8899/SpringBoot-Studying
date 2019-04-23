package cn.henry.springbootlearning.model.commons;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by liqing on 2017/12/8 0008.
 */
@Setter
@Getter
@NoArgsConstructor
public class JsonResult implements Serializable {

    private static final long serialVersionUID = -1L;

    // 0成功，1失败
    private Integer code;
    // 业务错误码
    private String businessCode = "0000";
    private String msg;
    private Object data = null;

    /**
     * @param sysResponseCode
     */
    public JsonResult(ISysResponseCode sysResponseCode) {
        this.msg = sysResponseCode.getMsg();
        this.code = sysResponseCode.getCode();
        this.businessCode = sysResponseCode.getBusinessCode();
    }

    public JsonResult(ISysResponseCode sysResponseCode, Object data) {
        this(sysResponseCode);
        this.data = data;
    }

    public JsonResult(Integer code, String businessCode, String msg, Object data) {
        this.code = code;
        this.businessCode = businessCode;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造成功的JsonResult，code为0000，busniessId为0000
     * @return JsonResult
     */
    public static JsonResult buildSuccess() {
        return new JsonResult(CommonSysResponseCode.success);
    }

    public static JsonResult buildSuccess(Object data){
        return new JsonResult(CommonSysResponseCode.success, data);
    }

    /**
     * 构造状态不正确的JsonResult，code为0001，busniessId为0001
     * @return JsonResult
     */
    public static JsonResult buildError() {
        return new JsonResult(CommonSysResponseCode.sysErr);
    }

    /**
     * 构造状态不正确的JsonResult，code为0001，busniessId为0001, msg为errorMsg
     * @param errorMsg
     * @return
     */
    public static JsonResult buildErrorWithMsg(String errorMsg) {
        return new JsonResult(CommonSysResponseCode.sysErr.getCode(), CommonSysResponseCode.sysErr.getBusinessCode(), errorMsg, null);
    }

    /**
     * 构造自定义JsonResult
     * @return JsonResult
     */
    public static JsonResult build(ISysResponseCode sysResponseCode) {
        return new JsonResult(sysResponseCode);
    }

    @Override
    public String toString() {
        return "JsonResult{" +
                "code=" + code +
                ", businessCode=" + businessCode +
                ", msg=" + msg +
                ", data=" + data +
                '}';
    }

    public boolean checkSuccess() {
        return 0 == code && "0000".equals(businessCode);
    }

}
