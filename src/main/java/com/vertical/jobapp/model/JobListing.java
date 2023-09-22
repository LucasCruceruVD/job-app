package com.vertical.jobapp.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_listing", schema = "public")
public class JobListing implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(min = 5, max = 50)
    @Column(name = "company_name", length = 50, nullable = false)
    private String companyName;

    @NotNull
    @Size(min = 5, max = 50)
    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @NotNull
    @Size(min = 5, max = 1000)
    @Column(name = "desc", length = 1000, nullable = false)
    private String desc;

    @NotNull
    @Size(min = 5, max = 1000)
    @Column(name = "requirements", length = 1000, nullable = false)
    private String requirements;

    @NotNull
    @Size(min = 5, max = 1000)
    @Column(name = "qualifications", length = 1000, nullable = false)
    private String qualifications;

    @NotNull
    @Column(name = "min_salary", nullable = false)
    private Long minSalary;

    @NotNull
    @Column(name = "max_salary", nullable = false)
    private Long maxSalary;

    @NotNull
    @Min(value = 1)
    @Max(value = 24)
    @Column(name = "hours_per_day", nullable = false)
    private Integer hoursPerDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user = new User();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobListing)) {
            return false;
        }
        return id != null && id.equals(((JobListing) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "JobListing{" +
            "id=" + getId() +
            ", companyName='" + getCompanyName() + "'" +
            ", title='" + getTitle() + "'" +
            ", desc='" + getDesc() + "'" +
            ", requirements='" + getRequirements() + "'" +
            ", qualifications='" + getQualifications() + "'" +
            ", minSalary=" + getMinSalary() +
            ", maxSalary=" + getMaxSalary() +
            ", hoursPerDay=" + getHoursPerDay() +
            "}";
    }
}
