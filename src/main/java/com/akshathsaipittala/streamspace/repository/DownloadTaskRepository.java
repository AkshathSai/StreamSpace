package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.entity.DownloadTask;
import com.akshathsaipittala.streamspace.entity.STATUS;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DownloadTaskRepository extends ListCrudRepository<DownloadTask, Long>, JpaSpecificationExecutor<DownloadTask> {

    List<DownloadTask> findAllByTaskStatus(STATUS taskStatus);

    DownloadTask findByTorrentHash(String torrentHash);
}
