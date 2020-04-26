package com.academy.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarksReportDTO {

    String subject;
    int maxMark;
    int passMark;
    int semester1;
    int semester2;
    int finalGrade;
    String subjectGrad;

}
