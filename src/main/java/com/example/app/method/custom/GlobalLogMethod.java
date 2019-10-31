package com.example.app.method.custom;

import com.example.app.model.LogDetail;

import java.util.List;

public interface GlobalLogMethod {
    void setLogDetailList(List<LogDetail> logDetailList);
    List<LogDetail> exec();
}
