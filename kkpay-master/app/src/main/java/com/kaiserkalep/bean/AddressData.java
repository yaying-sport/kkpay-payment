package com.kaiserkalep.bean;

import com.kaiserkalep.widgets.pickerview.contrarywind.interfaces.IPickerViewData;

import java.util.List;

/**
 * 省市区实体
 *
 * @Auther: Jack
 * @Date: 2021/2/17 14:12
 * @Description:
 */
public class AddressData implements IPickerViewData {


    /**
     * name : 北京市
     * code : 110000
     * sub : [{"name":"北京市","code":"110100","sub":[{"name":"东城区","code":"110101"},{"name":"西城区","code":"110102"},{"name":"朝阳区","code":"110105"},{"name":"丰台区","code":"110106"},{"name":"石景山区","code":"110107"},{"name":"海淀区","code":"110108"},{"name":"门头沟区","code":"110109"},{"name":"房山区","code":"110111"},{"name":"通州区","code":"110112"},{"name":"顺义区","code":"110113"},{"name":"昌平区","code":"110114"},{"name":"大兴区","code":"110115"},{"name":"怀柔区","code":"110116"},{"name":"平谷区","code":"110117"},{"name":"密云县","code":"110228"},{"name":"延庆县","code":"110229"}]}]
     */

    private String name;
    private String code;
    private List<SubBeanX> sub;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<SubBeanX> getSub() {
        return sub;
    }

    public void setSub(List<SubBeanX> sub) {
        this.sub = sub;
    }

    @Override
    public String getPickerViewText() {
        return name;
    }

    public static class SubBeanX implements IPickerViewData {
        /**
         * name : 北京市
         * code : 110100
         * sub : [{"name":"东城区","code":"110101"},{"name":"西城区","code":"110102"},{"name":"朝阳区","code":"110105"},{"name":"丰台区","code":"110106"},{"name":"石景山区","code":"110107"},{"name":"海淀区","code":"110108"},{"name":"门头沟区","code":"110109"},{"name":"房山区","code":"110111"},{"name":"通州区","code":"110112"},{"name":"顺义区","code":"110113"},{"name":"昌平区","code":"110114"},{"name":"大兴区","code":"110115"},{"name":"怀柔区","code":"110116"},{"name":"平谷区","code":"110117"},{"name":"密云县","code":"110228"},{"name":"延庆县","code":"110229"}]
         */

        private String name;
        private String code;
        private List<SubBean> sub;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public List<SubBean> getSub() {
            return sub;
        }

        public void setSub(List<SubBean> sub) {
            this.sub = sub;
        }

        @Override
        public String getPickerViewText() {
            return name;
        }

        public static class SubBean implements IPickerViewData {
            /**
             * name : 东城区
             * code : 110101
             */

            private String name;
            private String code;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            @Override
            public String getPickerViewText() {
                return name;
            }
        }
    }
}