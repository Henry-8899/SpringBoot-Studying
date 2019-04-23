package cn.henry.springbootlearning.model.commons;



/**
 * Created by liqing on 2017/12/10 0010.
 */
public enum CommonSysResponseCode implements ISysResponseCode {
    success(0, "0000", "success"),
    sysErr(1, "0001", "sysErr"),
    paramErr(1, "0002", "paramErr"),
    auditorErr(1, "0003", "auditor is not exist"),
    noLogin(1, "0004", "noLogin"),
    noPermission(1, "0005", "Permission denied"),

    /**
     * 线下还款
     */
    offlineRepayStatueEr(1,"10001","It has been examined and approved"),
    offlineRepayBankSerialEr(1,"10002","Sao kê  ngân hàng đã tồn tại"),


    ;

    private Integer code;
    private String businessCode;
    private String msg;

    CommonSysResponseCode(Integer code, String businessCode, String msg){
        this.code = code;
        this.businessCode = businessCode;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getBusinessCode() {
        return this.businessCode;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
