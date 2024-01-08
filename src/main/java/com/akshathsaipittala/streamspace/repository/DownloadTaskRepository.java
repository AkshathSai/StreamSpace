package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.entity.DownloadTask;
import com.akshathsaipittala.streamspace.entity.STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DownloadTaskRepository extends JpaRepository<DownloadTask, Long> {

    List<DownloadTask> findByTaskStatus(STATUS taskStatus);

    DownloadTask findByTorrentHash(String torrentHash);
}
