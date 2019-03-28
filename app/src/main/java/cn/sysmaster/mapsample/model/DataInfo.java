package cn.sysmaster.mapsample.model;

import java.util.List;

/**
 * @author dabo
 * @date 2019/3/22
 * @describe
 */
public class DataInfo {


    public Header header;
    public BusinessBean body;
    public String extra;


    public static class BusinessBean {

        public String telephone;
        public String uuid;
        public String lng;
        public String avg_consumption;
        public String is_full_count;
        public String city;
        public String status;
        public String brand_logo;
        public String sub_type;
        public String province;
        public String logo;
        public String sign;
        public String opening_hours;
        public String lat;
        public String type;
        public String name;
        public String address;
        public String longitude;
        public String latitude;
        public String title;
        public String desc;
        public String ios_can_borrow_num;
        public String android_can_borrow_num;
        public String typec_can_borrow_num;
        public String can_return_num;
        public ReturnTextBean return_text;
        public IosTextBean ios_text;
        public AndroidTextBean android_text;
        public TypecTextBean typec_text;
        public AdParamBean ad_param;
        public List<String> banner;
        public List<?> count_tip;

        public static class ReturnTextBean {
            /**
             * text : 可归还
             * color :
             */

            public String text;
            public String color;
        }

        public static class IosTextBean {
            /**
             * text : 有
             * color :
             */

            public String text;
            public String color;
        }

        public static class AndroidTextBean {
            /**
             * text : 有
             * color :
             */

            public String text;
            public String color;
        }

        public static class TypecTextBean {
            /**
             * text : 有
             * color :
             */

            public String text;
            public String color;
        }

        public static class AdParamBean {
            /**
             * positions : ["businessDetail"]
             * business_id : 75088868-64c4-40e1-a0da-3787e8885d24
             * business_type : 丽人
             * business_sub_type : 美容中心
             * city : 北京
             * province : 北京市
             */

            public String business_id;
            public String business_type;
            public String business_sub_type;
            public String city;
            public String province;
            public List<String> positions;
        }
    }
}
