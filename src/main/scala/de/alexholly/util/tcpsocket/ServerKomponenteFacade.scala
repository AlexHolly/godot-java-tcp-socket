package de.alexholly.util.tcpsocket

import java.net.Socket;

/**
 *
 * @author Alex
 */
object ServerKomponenteFacade extends IServerServices {

  var teilnehmer: Map[String, User] = Map()
  val server: Server = new Server(this)
  var maxClients: Int = 10
  var currentClients: Int = 0
  var shutDown: Boolean = false
  var maxLength = -1

  //Default 10 Clients
  //    public ServerKomponenteFacade() {
  //        this.teilnehmer = new ObservList<SClient>();
  //        this.server = new Server(this);
  //        this.maxLength = 101;
  //    }

  def setMaxClients(max: Int) {
    maxClients = max;
  }

  def killAll() {
    for (user: User <- teilnehmer.values) {
      this.closeClient(user.id);
    }
  }

  var onDisconnects = List[IOnDisconnect]()
  def addOnDisconnect(function: IOnDisconnect) {
    onDisconnects +:= function
  }

  var onConnects = List[IOnConnect]()
  def addOnConnect(function: IOnConnect) {
    onConnects +:= function
  }

  def closeClient(id: String) {
    teilnehmer.get(id) match {
      case Some(user) =>
        teilnehmer -= id
        user.closeConnection()
        for (listener <- onDisconnects) {
          listener.onDisconnect(user)
        }
        currentClients -= 1
      case None => println("Close: Den user " + id + " gibt es nicht ")
    }
  }

  def sendenAnAlle(text: String) {
    for (user: User <- teilnehmer.values) {
      user.senden(text);
    }
  }

  def starten(port: Int) {
    shutDown = false;
    currentClients = 0;
    server.starten(port);
  }

  def schliessen() {
    shutDown = false;
    currentClients = 0;
    server.schliessen();
  }

  def addElement(s: Socket) {
    var user: User = new User(this, s);
    teilnehmer += (user.id -> user)
    for (listener <- onConnects) {
      listener.onConnect(user)
    }
    user.start();
  }

  def isRunning(): Boolean = {
    server.isRunning();
  }

  def senden(id: String, text: String) {
    teilnehmer.get(id) match {
      case Some(user) => user.senden(text)
      case None => println("Senden: den user " + id + " gibt es nicht ")
    }
  }

  def getTeilnehmer(): Map[String, User] = {
    teilnehmer
  }

  def isShutDown(): Boolean = {
    shutDown
  }

  def setMaxChars(max: Int) {
    maxLength = max
  }

  def getMaxChars(): Int = {
    maxLength
  }

  var services: Map[String, IService] = Map()

  def addService(serviceNameVerb: String, service: IService) {
    services += (serviceNameVerb -> service)
  }

  def handle_request(header: Map[String, String], body: String, user: User) {
//    println("handle_request")
    //Alex optimierungstest
    //    var header = request("header")
    var service = header("service")
    var url = header("url")
    var verb = header("verb")
    //TODO: parsen der Parameter mit :param?
//    println("service: " + service)

    if (services.contains(service)) {
//      println("service: JA")
      if (url.contains("/")) {
        services(service).request(verb, url, "", body, user)
      } else {
        services(service).response(verb, url, "", body, user)
      }
      //	  ok(user)
    }
  }

}
