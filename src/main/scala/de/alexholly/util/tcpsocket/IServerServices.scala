package de.alexholly.util.tcpsocket

import java.net.Socket
//import util.common.ObservList;

/**
 *
 * @author Alex
 */

trait IService {
  def request(verb: String, url: String, params: String, body: String, user: User)
  def response(verb: String, url: String, params: String, body: String, user: User)
}

trait IOnDisconnect {
  def onDisconnect(user: User)
}

trait IOnConnect {
  def onConnect(user: User)
}

trait IServerServices {

  def addOnDisconnect(function: IOnDisconnect)
  
  def addOnConnect(function: IOnConnect)
  
  def handle_request(header: Map[String, String], body: String, user: User)

  def addService(serviceNameVerb: String, service: IService)

  def setMaxClients(max: Int)

  def setMaxChars(max: Int)

  def getMaxChars(): Int

  def addElement(s: Socket)

  def getTeilnehmer(): Map[String, User]

  def isRunning(): Boolean

  def closeClient(id: String)

  def starten(port: Int)

  def schliessen()

  def senden(id: String, text: String)

  def killAll()

  def sendenAnAlle(text: String)
}
