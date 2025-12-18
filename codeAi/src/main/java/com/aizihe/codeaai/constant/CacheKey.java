package com.aizihe.codeaai.constant;

/**
 * 缓存key统一定义常量类
 *
 * @author gavyn
 */
public class CacheKey {


    public static final String CACHE_PREFIX = "vision_zh:";

    /**
     * 验证码缓存
     */
    public static final String CAPTCHA = CACHE_PREFIX +"captcha:%s";

    /**
     * 用户登录token
     */
    public static final String USER_TOKEN = CACHE_PREFIX + "user-token:%s";

    /**
     *
     */
    public static final String DEFAULT_REGION = "default";

    /**
     * 请求id缓存region
     */
    public static final String REQUEST_ID_REGION = "request-id-region";

    /**
     * 请求id缓存
     */
    public static final String REQUEST_ID_CACHE_KEY = CACHE_PREFIX + "request:id:%s";

    /**
     * 验证码缓存key
     */
    public static final String CAPTCHA_CACHE_KEY = CACHE_PREFIX + "login:captcha:%s";

    public static final String SITE = CACHE_PREFIX + "site:%s";

    public static final String COLUMN_TOP = CACHE_PREFIX + "column:top:%s";

    public static final String COLUMN_BOTTOM = CACHE_PREFIX + "column:bottom:%s";

    public static final String USER_RESOURCE_REGION = "user-resource";

    public static final String SYSTEM_CONFIG_REGION = "system-config-region";

    public static final String SYSTEM_PARAM_REGION = "system-param-region";

    public static final String CMS_CONTENT_REGION = "cms-content-region";

    public static final String PARAM                          = "%s:%s";
    public static final String FIRST_LEVEL_COLUMN             = "first:level:column:%s";
    public static final String COLUMN                         = CACHE_PREFIX + "column:%s:%s";
    public static final String COLUMN_UNIQUE_CONTENT          = CACHE_PREFIX + "column:unique:%s:%s";
    public static final String COLUMN_LIST_CONTENT            = CACHE_PREFIX + "column:list:%s:%s";
    public static final String COLUMNAll                      = "columnALL";
    public static final String COLUMN_TREE_TOP                = "column:tree:top:%s";
    public static final String COLUMN_SUB_TOP                 = "column:sub:top:%s";
    public static final String COLUMN_SUB                     = "column:sub:%s";
    public static final String COLUMN_TREE_BOTTOM             = "column:tree:bottom:%s";
    public static final String COLUMN_WEBSITE_MAP_TREE_BOTTOM = "column:website:map:tree:bottom:%s";
    public static final String CONTENT_LIST                   = "content:%s";
    public static final String CONTENT_LIST_MATCH             = "content:%s:%s:%s";
    public static final String CONTENT_DETAIL                 = "content:detail:%s:%s";

    public static final String BANNER_CID_LIST   = "banner:cid:list";
    public static final String SHARE_CERTIFICATE = "share:certificate";

    public static final String PREVIEW_TOKEN = "preview-token";

    public static final String ARTICLE_DETAIL = "article:detail:%s";
    /**
     * 百度请求token
     */
    public static final String BAI_DU_TOKEN   = "baidu_token";
    /**
     * 缓存全局唯一前缀
     */
    public static final String PREFIX = "";
    /**
     * 验证码缓存key 第一个%s为来源 第二个%s为唯一key
     */
    public static final String CAPTCHA_KEY = PREFIX + "captcha:%s";

    public static  final String SMS_VERIFY_CODE="tencent:sms:%s";
    public static  final String SMS_EMAIL_CODE="tencent:email:%s";
}
