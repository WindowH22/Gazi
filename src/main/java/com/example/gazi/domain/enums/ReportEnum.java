package com.example.gazi.domain.enums;

import lombok.Getter;

@Getter
public enum ReportEnum {
    //부적절한 글
    IN_APPOSITE("IN APPOSITE"),
    FALSE_INFORMATION("FALSE INFORMATION"),
    ADVERTISEMENT("ADVERTISEMENT"),
    HATE("HATE"),
    ETC("ETC");

    ReportEnum(String notificationEnum) {
    }
}
