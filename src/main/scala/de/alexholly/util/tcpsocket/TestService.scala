//package de.alexholly.util.tcpsocket
//
//case class TestService() extends IService {
//
//  def request(verb: String, url: String, params: String, body: String, user: User) {
//    println("verb " + verb)
//    println("url " + url)
//    println("params " + params)
//    println("body " + body)
//    //user.senden("POST /player/turn HTTP/1.1\r\n\r\n")
//
////    server.addID(name, id)
//
//    //Erfolgreich angemeldet
//    user.senden("HTTP/1.1 200 Ok\r\n" + "Content-Type: application/json; charset=UTF-8\r\n\r\n")
//  }
//
//  def response(verb: String, url: String, params: String, body: String, user: User) {
//    //Auf anfrage antworten
//    //user.senden("HTTP/1.1 200 Ok\r\n" + "Content-Type: application/json; charset=UTF-8\r\n\r\n")
//  }
//
//  def onDisconnect(user: User, server: IServerServices) {
//
//  }
//}