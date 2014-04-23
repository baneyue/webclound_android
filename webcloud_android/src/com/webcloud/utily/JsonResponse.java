package com.webcloud.utily;

public class JsonResponse {
    public static final String RET_DATA = "retdata";
    public static final String RET_CODE = "retcode";
    public static final String RET_MSG = "retmsg";
    /**成功*/
    public static final String CODE_SUCC = "0";
    /**未知异常*/
    public static final String CODE_ERROR = "1";
    /**会话失效*/
    public static final String CODE_SESSION_VALID = "2";
    /**请求参数有误*/
    public static final String CODE_REQPARAM_VALID = "100";
    /**业务验证失败*/
    public static final String CODE_LOGIC_VALID = "101";

    public static final String PAGE_COUNT = "pageCount";
    public static final String PAGE_INDEX = "pageIndex";
    public static final String PAGE_SIZE = "pageSize";
    public static final String PAGE_RECORD_COUNT = "recordeCount";
    
    
}
