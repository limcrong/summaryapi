package com.summary.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
    private String code;
    private String msg;
    private String credits;
    private String remaining_credits;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getRemaining_credits() {
        return remaining_credits;
    }

    public void setRemaining_credits(String remaining_credits) {
        this.remaining_credits = remaining_credits;
    }
}
