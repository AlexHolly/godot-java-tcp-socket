package de.alexholly.util.tcpsocket

import java.io.IOException
import java.net._

case class Server(serverServices: IServerServices) extends Runnable {

  var t: Thread = null
  var serversocket: ServerSocket = null

  def killThread() {
    if ((t != null) && t.isAlive()) {
      t.interrupt()
    }
  }

  def isRunning(): Boolean = {
    !serversocket.isClosed()
  }

  // Server beenden durch null setzen
  def starten(port: Int) {
    try {
      if (serversocket == null || serversocket.isClosed()) {
        serversocket = new ServerSocket(port);
        t = new Thread(this);
        t.start();
        println("(Server.starten):Server erfolgreich gestartet auf Port: " + port);
      } else {
        println("(Server.starten):Server bereits gestartet auf Port: " + port);
      }
    } catch {
      case e: IOException =>
        try {
          if (serversocket != null) {
            serversocket.close();
            serversocket = null;
          }
          println("Server wurde Geschlossen");
          println("Server Fehler, port besetzt?");
          killThread();
        } catch {
          case e: IOException =>
            println("Server konnte nicht Geschlossen werden");
            killThread();
        }
    }
  }

  def schliessen() {
    try {
      killThread();
      if (serversocket != null) {
        serversocket.close();
        serversocket = null;
        serverServices.killAll();
        println("(Server.schliessen):Server wurde geschlossen...");
      } else {
        println("(Server.schliessen):Server ist bereits geschlossen...");
      }
    } catch {
      case e: IOException =>
        println("(Server.schliessen):Fehler beim trennen von ...");
    }
  }

  override def run() {
    try {
      while (true) { // Wartet auf eingehende Verbindungen
        var s: Socket = serversocket.accept();
        println("(Server.run):Neuer Client verbunden");
        serverServices.addElement(s);
      }
    } catch {
      case e: IOException =>
        try {
          if (serversocket != null) {
            serversocket.close();
            serversocket = null;
          }
          println("(Server.run):Fehler beim warten/verbinden eines Clients." + e);
        } catch {
          case e: IOException =>
            println("(Server.run):Fehler beim warten/verbinden eines Clients." + e);
        }
    }
  }
}
