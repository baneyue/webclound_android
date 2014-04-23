package com.funlib.log;


/**
 * 崩溃模块异常日志发送。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-30]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class CrashLogCtrl {
    /**记录应用崩溃日志文件名*/
    public static final String CRASH_LOG = "CrashLog";
    
    public static void sendCrashLog() {
        //System.out.println(1/0);
        /*try {
            SharedPreferences sp =
                CTCloudApplication.getInstance().getSharedPreferences(CRASH_LOG,
                    Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
            Map<String, ?> logMap = sp.getAll();
            if (logMap != null && !logMap.isEmpty()) {
                Set<String> set = logMap.keySet();
                Iterator<String> it = set.iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("key", key);
                    StringBuffer sb = new StringBuffer((String)logMap.get(key));
                    if (sb.length() > 4000) {
                        params.put("crashwords", sb.substring(0, 3999));
                    } else {
                        params.put("crashwords", sb.toString());
                    }
                    String MobileId = Utily.getDeviceIMSI();
                    if (TextUtils.isEmpty(MobileId)) {
                        MobileId = CTCloudApplication.getInstance().getImsi();
                    }
                    params.put("MobileId", MobileId);
                    Log.d(CRASH_LOG, MobileId);
                    params.put("version", Utily.getVersionCode(CTCloudApplication.getInstance()));
                    //有键值对，就发送日志到服务端,发送成功后返回，清空缓存文件
                    new Requester(CTCloudApplication.getInstance()).request(new NewRequestListener() {
                        
                        @Override
                        public void requestStatusChanged(int statusCode, HttpUrlImpl requestId,
                            String responseString, Map<String, String> requestParams) {
                            if (statusCode != RequestStatus.SUCCESS) {
                                return;
                            }
                            try {
                                JSONObject json = JsonFriend.parseJSONObject(responseString);
                                String retcode = json.getString(JsonResponse.RET_CODE);
                                String retmsg = json.getString(JsonResponse.RET_MSG);
                                HttpUrlImpl.V1 v1 = (HttpUrlImpl.V1)requestId;
                                switch (v1) {
                                    case CRASH_LOG: {
                                        if (JsonResponse.CODE_SUCC.equals(retcode)) {
                                            SharedPreferences sp =
                                                CTCloudApplication.getInstance().getSharedPreferences(CRASH_LOG,
                                                    Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
                                            //sp.edit().remove(requestParams.get("key")).commit();
                                            sp.edit().clear().commit();
                                        }
                                    }
                                        break;
                                    default:
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                        HttpUrlImpl.V1.CRASH_LOG,
                        HttpUrlImpl.V1.CRASH_LOG.getUrl(),
                        params,
                        HttpRequestImpl.POST,
                        false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
