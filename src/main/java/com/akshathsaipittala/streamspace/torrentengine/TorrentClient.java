/*
 * Copyright (c) 2016—2024 Akshath Sai Pittala, Andrei Tomashpolskiy and individual contributors.
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
package com.akshathsaipittala.streamspace.torrentengine;

import bt.Bt;
import bt.BtClientBuilder;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.metainfo.TorrentId;
import bt.protocol.crypto.EncryptionPolicy;
import bt.runtime.BtClient;
import bt.runtime.BtRuntime;
import bt.runtime.Config;
import bt.torrent.selector.PieceSelector;
import bt.torrent.selector.RarestFirstSelector;
import bt.torrent.selector.SequentialSelector;
import com.akshathsaipittala.streamspace.indexer.MediaIndexer;
import com.akshathsaipittala.streamspace.repository.DownloadTaskRepository;
import com.akshathsaipittala.streamspace.utils.TorrentProgressHandler;
import com.google.inject.Module;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public class TorrentClient {

    //private static TorrentClient instance;

    public static void startEngine(Options options, MediaIndexer mediaIndexer, TorrentProgressHandler torrentProgressHandler, DownloadTaskRepository downloadTaskRepository) {
        /*   if (instance!= null) {
            throw new IllegalStateException("TorrentClient is already initialized");
        }
        instance = new TorrentClient(options, mediaIndexer, torrentProgressHandler, downloadTaskRepository);
        instance.resume();*/
        new TorrentClient(options, mediaIndexer, torrentProgressHandler, downloadTaskRepository).resume();
    }

    private Options options;

    private BtRuntime runtime;
    private BtClient client;
    private Optional<SessionStateLogger> torrentStateLogger;
    private boolean running;
    private MediaIndexer mediaIndexer;
    private TorrentProgressHandler torrentProgressHandler;
    private DownloadTaskRepository downloadTaskRepository;

    private TorrentClient(Options options, MediaIndexer mediaIndexer, TorrentProgressHandler torrentProgressHandler, DownloadTaskRepository downloadTaskRepository) {
        this.options = options;
        this.mediaIndexer = mediaIndexer;
        this.torrentProgressHandler = torrentProgressHandler;
        this.downloadTaskRepository = downloadTaskRepository;

        configureSecurity();

        Optional<InetAddress> acceptorAddressOverride = getAcceptorAddressOverride();
        Optional<Integer> portOverride = getPortOverride();
        Optional<Integer> dhtPortOverride = getDHTPortOverride();

        Config config = new Config() {
            @Override
            public InetAddress getAcceptorAddress() {
                return acceptorAddressOverride.orElseGet(super::getAcceptorAddress);
            }

            @Override
            public int getAcceptorPort() {
                return portOverride.orElseGet(super::getAcceptorPort);
            }

            @Override
            public int getNumOfHashingThreads() {
                return Runtime.getRuntime().availableProcessors();
            }

            @Override
            public EncryptionPolicy getEncryptionPolicy() {
                return options.isEnforceEncryption()? EncryptionPolicy.REQUIRE_ENCRYPTED : EncryptionPolicy.PREFER_PLAINTEXT;
            }
        };

        Module dhtModule = new DHTModule(new DHTConfig() {
            @Override
            public int getListeningPort() {
                return dhtPortOverride.orElseGet(super::getListeningPort);
            }

            @Override
            public boolean shouldUseRouterBootstrap() {
                return true;
            }
        });

        this.runtime = BtRuntime.builder(config)
                .module(dhtModule)
                .autoLoadModules()
                .disableAutomaticShutdown()
                .build();

        Storage storage = new FileSystemStorage(options.getTargetDirectory().toPath());
        PieceSelector selector = options.isSequential() ?
                SequentialSelector.sequential() : RarestFirstSelector.randomizedRarest();

        BtClientBuilder clientBuilder = Bt.client(runtime)
                .storage(storage)
                .selector(selector);

        SessionStateLogger torrentStateLogger = options.isDisableTorrentStateLogs() ?
                null : new SessionStateLogger(torrentProgressHandler, downloadTaskRepository);

        if (torrentStateLogger != null) {

            clientBuilder.afterTorrentFetched(torrent -> {
                String torrentName = torrent.getName();
                TorrentId torrentId = torrent.getTorrentId();
                // Set the custom filename for each file in the torrent
                torrent.getFiles().forEach(file -> {
                    // Set the custom filename for the file
                    file.getPathElements().forEach(fileName -> {
                                if (fileName.endsWith(".mp4") || fileName.endsWith(".mkv") || fileName.endsWith(".avi")) {
                                    mediaIndexer.indexMovie(file, torrentName, fileName, torrentId);
                                    log.info("Video {}", fileName);
                                } else if (fileName.endsWith(".mp3") || fileName.endsWith(".flac")) {
                                    // mediaIndexer.indexMusic(file, torrentName, fileName, torrentId);
                                    log.info("Audio {}", fileName);
                                } else {
                                    // noop for OTHERS
                                    log.info("Others {}", fileName);
                                }
                            }
                    );
                });

                torrentStateLogger.setTorrent(torrent);
            });

        }

        if (options.getMetainfoFile() != null) {
            clientBuilder = clientBuilder.torrent(toUrl(options.getMetainfoFile()));
        } else if (options.getMagnetUri() != null) {
            clientBuilder = clientBuilder.magnet(options.getMagnetUri());
        } else {
            throw new IllegalStateException("Torrent file or magnet URI is required");
        }

        this.client = clientBuilder.build();
        this.torrentStateLogger = Optional.ofNullable(torrentStateLogger);
    }

    // Static method to get the instance
    /*public static TorrentClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TorrentClient is not initialized");
        }
        return instance;
    }*/

    private Optional<Integer> getPortOverride() {
        return getOptionalPort(options::getPort);
    }

    private Optional<Integer> getDHTPortOverride() {
        return getOptionalPort(options::getDhtPort);
    }

    private static Optional<Integer> getOptionalPort(Supplier<Integer> portSupplier) {
        Integer port = portSupplier.get();
        if (port == null) {
            return Optional.empty();
        } else if (port < 1024 || port >= 65535) {
            throw new IllegalArgumentException("Invalid port: " + port + "; expected 1024..65534");
        }
        return Optional.of(port);
    }

    private Optional<InetAddress> getAcceptorAddressOverride() {
        String iface = options.getIface();
        if (iface == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(InetAddress.getByName(iface));
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Failed to parse the acceptor's internet address", e);
        }
    }

    private void configureSecurity() {
        // Starting with JDK 8u152 this is a way to programmatically allow unlimited encryption
        // See http://www.oracle.com/technetwork/java/javase/8u152-relnotes-3850503.html
        String key = "crypto.policy";
        String value = "unlimited";
        try {
            Security.setProperty(key, value);
        } catch (Exception e) {
            log.error(String.format("Failed to set security property '%s' to '%s'", key, value), e);
        }
    }

    private static URL toUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unexpected error", e);
        }
    }

    void resume() {
        if (running) {
            return;
        }

        running = true;
        try {
            client
                    .startAsync(state -> {

                        torrentStateLogger.ifPresent(p -> p.printTorrentState(state));

                        if (!options.isSeedAfterDownloaded() && state.getPiecesRemaining() == 0) {
                            torrentStateLogger = Optional.empty(); // mark for garbage collection
                            runtime.shutdown();
                        }
                    }, 5000)
                    .whenComplete((r, t) -> {
                        if (t != null) {
                            throw new RuntimeException(t);
                        }
                    });
        } catch (Throwable e) {
            printAndShutdown(e);
            //addShutdownHook();
        }
    }

    void pause() {
        if (!running) {
            return;
        }

        try {
            client.stop();
        } catch (Throwable e) {
            log.warn("Unexpected error when stopping client", e);
        } finally {
            running = false;
        }
    }

    private void togglePause() {
        if (running) {
            pause();
        } else {
            resume();
        }
    }

    private static void printAndShutdown(Throwable e) {
        // ignore interruptions on shutdown
        if (!(e instanceof InterruptedException)) {
            log.error("Unexpected error, exiting...", e);
        }
        //System.exit(1);
    }

    // Modify the shutdown hook to call cleanup
    public void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down TorrentClient...");
            cleanup();
            System.gc(); // Suggest garbage collection
        }));
    }

    private void cleanup() {
        pause(); // Ensure the client is stopped
        client = null; // Dereference the client
        runtime = null; // Dereference the runtime
        torrentStateLogger = Optional.empty(); // Clear the optional reference
    }

    /*public static void toggleStartStop() {
        TorrentClient client = getInstance();
        client.togglePause();
    }*/

}
