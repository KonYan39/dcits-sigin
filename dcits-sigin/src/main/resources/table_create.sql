CREATE TABLE "public"."tbl_userinfo" (
  "open_id" varchar(60) COLLATE "pg_catalog"."default" NOT NULL,
  "user_name" varchar(60) COLLATE "pg_catalog"."default",
  "first_card_hour" int4,
  "first_card_minute" int4,
  "last_card_hour" int4,
  "last_card_minute" int4,
  "next_card_time" varchar(255) COLLATE "pg_catalog"."default",
  "at_phone_num" varchar(15) COLLATE "pg_catalog"."default",
  "current_day_card_counts" int4,
  "after_card_minutes" int4,
  "is_first_force" int4,
  "is_full_card" int4 DEFAULT 0,
  "address" varchar(255) COLLATE "pg_catalog"."default",
  CONSTRAINT "tbl_userInfo_pkey" PRIMARY KEY ("open_id")
);


COMMENT ON COLUMN "public"."tbl_userinfo"."user_name" IS '姓名';

COMMENT ON COLUMN "public"."tbl_userinfo"."first_card_hour" IS '初次打卡小时';

COMMENT ON COLUMN "public"."tbl_userinfo"."first_card_minute" IS '初次打卡分钟(具体时间为初次小时:初次打卡分钟~初次打卡分钟+after_card_minutes)';

COMMENT ON COLUMN "public"."tbl_userinfo"."last_card_hour" IS '末次打卡小时';

COMMENT ON COLUMN "public"."tbl_userinfo"."last_card_minute" IS '末次打卡分钟(具体时间为初次小时:末次打卡分钟~末次打卡分钟+after_card_minutes)';

COMMENT ON COLUMN "public"."tbl_userinfo"."next_card_time" IS '下次打卡时间';

COMMENT ON COLUMN "public"."tbl_userinfo"."at_phone_num" IS '钉钉@手机号码';

COMMENT ON COLUMN "public"."tbl_userinfo"."current_day_card_counts" IS '当天打卡次数';

COMMENT ON COLUMN "public"."tbl_userinfo"."after_card_minutes" IS '打卡浮动时间';

COMMENT ON COLUMN "public"."tbl_userinfo"."is_first_force" IS '第一次是否强制打卡0否1是';

COMMENT ON COLUMN "public"."tbl_userinfo"."is_full_card" IS '是否全部打卡0否1是';

COMMENT ON COLUMN "public"."tbl_userinfo"."address" IS '打卡地址';
-- tbl_cardconf
CREATE TABLE "public"."tbl_cardconf" (
  "get_card_info_by_open_id_url" varchar(255) COLLATE "pg_catalog"."default",
  "card_url" varchar(255) COLLATE "pg_catalog"."default",
  "get_carded_infos_by_user_id_url" varchar(255) COLLATE "pg_catalog"."default",
  "ding_talk_web_hooks" varchar(500) COLLATE "pg_catalog"."default",
  "workdays" varchar(255) COLLATE "pg_catalog"."default",
  "holidays" varchar(255) COLLATE "pg_catalog"."default"
);

COMMENT ON COLUMN "public"."tbl_cardconf"."get_card_info_by_open_id_url" IS '获取打卡人信息 用于构建打卡参数 GET';

COMMENT ON COLUMN "public"."tbl_cardconf"."card_url" IS '打卡接口 POST';

COMMENT ON COLUMN "public"."tbl_cardconf"."get_carded_infos_by_user_id_url" IS '获取当天打卡信息 GET';

COMMENT ON COLUMN "public"."tbl_cardconf"."ding_talk_web_hooks" IS '钉钉机器人回调 可多个 英文逗号隔开';

COMMENT ON COLUMN "public"."tbl_cardconf"."workdays" IS '工作日 可多个 英文逗号隔开';

COMMENT ON COLUMN "public"."tbl_cardconf"."holidays" IS '假日 可多个 英文逗号隔开';