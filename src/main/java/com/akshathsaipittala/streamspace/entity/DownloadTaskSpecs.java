package com.akshathsaipittala.streamspace.entity;

import org.springframework.data.jpa.domain.Specification;

public class DownloadTaskSpecs {

    public static Specification<DownloadTask> hasTaskStatusIn(STATUS... statuses) {
        return (root, query, criteriaBuilder) -> root.get("taskStatus").in(statuses);
    }

}
