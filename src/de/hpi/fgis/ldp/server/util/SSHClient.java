/*-
 * Copyright 2012 by: Hasso Plattner Institute for Software Systems Engineering 
 * Prof.-Dr.-Helmert-Str. 2-3
 * 14482 Potsdam, Germany
 * Potsdam District Court, HRB 12184
 * Authorized Representative Managing Director: Prof. Dr. Christoph Meinel
 * http://www.hpi-web.de/
 * 
 * Information Systems Group, http://www.hpi-web.de/naumann/
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.server.util;

import java.io.File;
import java.io.IOException;

import net.schmizz.sshj.connection.channel.direct.Session.Command;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

public class SSHClient {
  private final Log logger;

  private net.schmizz.sshj.SSHClient con = null;

  private String host = null;
  private int port = -1;
  private String fingerprint = null;
  private String username = null;
  private String password = null;
  private String remoteDirectory = null;

  @Inject
  protected SSHClient(Log logger) {
    this.logger = logger;
  }

  public void init(String host, int port, String fingerprint, String username, String password,
      String remoteDirectory) {
    this.host = host;
    this.port = port;
    this.fingerprint = fingerprint;
    this.username = username;
    this.password = password;
    this.remoteDirectory = remoteDirectory;
  }

  public boolean connect() throws IOException {
    if (host == null || port < 0 || fingerprint == null || username == null || password == null
        || remoteDirectory == null) {
      throw new IllegalStateException("Unable to connect to ssh server w/o connection settings!");
    }

    if (this.con == null) {
      this.con = new net.schmizz.sshj.SSHClient();
    }
    try {
      this.con.loadKnownHosts();
    } catch (IOException e) {
      logger.warn("Unable to load known hosts for SSH server!");
    }
    this.con.addHostKeyVerifier(this.host, this.port, this.fingerprint);

    this.con.connect(this.host, this.port);

    this.con.authPassword(this.username, this.password);

    return this.isConnected();
  }

  public boolean disconnect() throws IOException {
    if (this.con == null) {
      return true;
    }

    this.con.disconnect();
    return !this.isConnected();
  }

  public void sendFile(final String localPath) throws IOException {
    if (this.con == null) {
      throw new IllegalStateException("Unable to send files without connection!");
    }
    final File content = new File(localPath);
    if (content.isDirectory()) {
      throw (new IllegalArgumentException("Unable to send directories"));
    }

    final String fileName = content.getName();

    this.con.newSFTPClient().put(localPath, this.remoteDirectory + fileName);
  }

  public String sendCommand(final String command) throws IOException {
    if (this.con == null) {
      throw new IllegalStateException("Unable to send commands without connection!");
    }

    final Command cmd = this.con.startSession().exec(command);

    return cmd.getOutputAsString();
  }

  public boolean isConnected() {
    return this.con == null || (this.con.isConnected() && this.con.isAuthenticated());
  }
}
