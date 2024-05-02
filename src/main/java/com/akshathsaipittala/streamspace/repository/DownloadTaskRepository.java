package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.entity.DownloadTask;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DownloadTaskRepository extends ListCrudRepository<DownloadTask, String>, JpaSpecificationExecutor<DownloadTask> {
}
