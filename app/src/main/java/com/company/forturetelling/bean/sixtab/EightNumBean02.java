package com.company.forturetelling.bean.sixtab;

/**
 * Created by Lovelin on 2019/12/23
 * <p>
 * Describe:
 */
public class EightNumBean02 {

    /**
     * status : 0
     * msg : ok
     * data : {"oid":"201912231517226050976929"}
     */

    private String status;
    private String msg;
    private DataBean data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * oid : 201912231517226050976929
         */

        private String oid;

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }
    }
}