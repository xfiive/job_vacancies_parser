package org.parser.parsermail.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

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
    private String reserveSalary;

    @Field
    private String contractType;

    @Field
    private boolean isLiked;


    @Override
    public String toString() {
        return "JobVacancy{" +
                "id='" + id + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobLink='" + jobLink + '\'' +
                ", companyName='" + companyName + '\'' +
                ", jobLocation='" + jobLocation + '\'' +
                ", salary=" + salary +
                ", reserveSalary='" + reserveSalary + '\'' +
                ", contractType='" + contractType + '\'' +
                ", isLiked='" + isLiked + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobVacancy that = (JobVacancy) o;
        return salary == that.salary &&
                Objects.equals(jobName, that.jobName) &&
                Objects.equals(jobLink, that.jobLink) &&
                Objects.equals(companyName, that.companyName) &&
                Objects.equals(jobLocation, that.jobLocation) &&
                Objects.equals(isLiked, that.isLiked) &&
                Objects.equals(reserveSalary, that.reserveSalary) &&
                Objects.equals(contractType, that.contractType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobName, jobLink, companyName, jobLocation, salary, reserveSalary, contractType, isLiked);
    }

}
