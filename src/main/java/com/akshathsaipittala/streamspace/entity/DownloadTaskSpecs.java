package com.akshathsaipittala.streamspace.entity;

import org.springframework.data.jpa.domain.Specification;

public class DownloadTaskSpecs {

    public static Specification<DownloadTask> hasTaskStatusIn(STATUS... statuses) {
        // Convert varargs to an array explicitly
        return (root, query, criteriaBuilder) -> root.get("taskStatus").in((Object[]) statuses);
    }

}
