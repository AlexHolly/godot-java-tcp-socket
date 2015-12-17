package de.alexholly.util.tcpsocket

object Main extends App {

  ServerKomponenteFacade.starten(3560)
  ServerKomponenteFacade.setMaxClients(2)
  
//  ServerKomponenteFacade.addService("name", TestService())

}
