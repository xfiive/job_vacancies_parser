package org.parser.parsermail.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "vacancies")
public class JobVacancy {

    @Id
    private String id;

    @Field
    private String jobName;

    @Field
    private String jobLink;

    @Field
    private String companyName;

    @Field
    private String jobLocation;

    @Field
    private int salary;

    @Field
    private String contractType;

    @Override
    public String toString() {
        return "JobVacancy{" +
                "id='" + id + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobLink='" + jobLink + '\'' +
                ", companyName='" + companyName + '\'' +
                ", jobLocation='" + jobLocation + '\'' +
                ", salary=" + salary +
                ", contractType='" + contractType + '\'' +
                '}';
    }
}
