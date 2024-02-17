/*
 * Copyright (c) 2016—2021 Andrei Tomashpolskiy and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.akshathsaipittala.streamspace.services.torrentengine;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Setter
@Getter
public class Options {

    private File metainfoFile;
    private String magnetUri;
    private File targetDirectory;
    private boolean seedAfterDownloaded;
    private boolean sequential;
    private boolean enforceEncryption;
    private boolean disableUi;
    private boolean disableTorrentStateLogs;
    private boolean verboseLogging;
    private boolean traceLogging;
    private String iface;
    private Integer port;
    private Integer dhtPort;
    private boolean downloadAllFiles;

    public Options(File metainfoFile,
                   String magnetUri,
                   File targetDirectory,
                   boolean seedAfterDownloaded,
                   boolean sequential,
                   boolean enforceEncryption,
                   boolean disableUi,
                   boolean disableTorrentStateLogs,
                   boolean verboseLogging,
                   boolean traceLogging,
                   String iface,
                   Integer port,
                   Integer dhtPort,
                   boolean downloadAllFiles) {
        this.metainfoFile = metainfoFile;
        this.magnetUri = magnetUri;
        this.targetDirectory = targetDirectory;
        this.seedAfterDownloaded = seedAfterDownloaded;
        this.sequential = sequential;
        this.enforceEncryption = enforceEncryption;
        this.disableUi = disableUi;
        this.disableTorrentStateLogs = disableTorrentStateLogs;
        this.verboseLogging = verboseLogging;
        this.traceLogging = traceLogging;
        this.iface = iface;
        this.port = port;
        this.dhtPort = dhtPort;
        this.downloadAllFiles = downloadAllFiles;
    }

    public boolean shouldSeedAfterDownloaded() {
        return seedAfterDownloaded;
    }

    public boolean downloadSequentially() {
        return sequential;
    }

    public boolean enforceEncryption() {
        return enforceEncryption;
    }

    public boolean shouldDisableTorrentStateLogs() {
        return disableTorrentStateLogs;
    }

    public Integer getDHTPort() {
        return dhtPort;
    }

    public boolean shouldDownloadAllFiles() {
        return downloadAllFiles;
    }
}
