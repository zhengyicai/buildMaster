package com.qzi.cms.common.vo;

import java.util.Map;

/**
 * 微信模板
 * Created by Administrator on 2019/10/12.
 */
public class WxTemplate {
    private String template_id;//模板ID
    private String touser;//目标客户

    private Miniprogram miniprogram;
    private String topcolor;//字体颜色
    private Map<String,TemplateData> data;//模板里的数据

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public Miniprogram getMiniprogram() {
        return miniprogram;
    }

    public void setMiniprogram(Miniprogram miniprogram) {
        this.miniprogram = miniprogram;
    }

    public String getTopcolor() {
        return topcolor;
    }

    public void setTopcolor(String topcolor) {
        this.topcolor = topcolor;
    }

    public Map<String, TemplateData> getData() {
        return data;
    }

    public void setData(Map<String, TemplateData> data) {
        this.data = data;
    }
}
