package com.pc.netty_anth_guide.chapter7;

import java.io.Serializable;

/**
 * 应答对象
 *
 * @author pengchao
 * @since 17:45 2019-09-16
 */
public class SubscribeResp implements Serializable {
    private static final long serialVersionUID = 4325013978711914835L;

    private int subReqId;
    private int respCode;
    private String desc;

    public int getSubReqId() {
        return subReqId;
    }

    public void setSubReqId(int subReqId) {
        this.subReqId = subReqId;
    }

    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    @Override
    public String toString() {
        return "SubscribeResp{" +
                "subReqId=" + subReqId +
                ", respCode=" + respCode +
                ", desc='" + desc + '\'' +
                '}';
    }
}
