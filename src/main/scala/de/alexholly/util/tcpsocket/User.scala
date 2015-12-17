package de.alexholly.util.tcpsocket

import java.io._
import java.net.Socket
import java.util.Objects

case class User(serverServices: IServerServices, socket: Socket) extends Thread {

  //    private boolean angemeldet  = false;
  //    private final int MAXLENGTH;

  var max_header_size = 8175
  var id:String = socket.getInetAddress.getHostAddress
  var os: OutputStream = null
  var in: InputStream = null
  
  var error = false

  override def run() {
    try {
      var c: Int = 0
      while (!error) {
        // Verbundener Client empfängt Daten
        in = socket.getInputStream()
        c = in.read()
        if (c != -1) {

          var header = ""
          var header_error = false
          var output = Map[String, String]()
          var header_size = 0

          //read header
          var endString = ""
          while (endString != "\r\n\r\n") {
            header += c.asInstanceOf[Char]
            header_size += 1

            try {
              endString = header.substring(header.length() - 4, header.length())
            } catch {
              case _: Throwable => endString = ""
            }
            //            if (header_size > max_header_size) {
            //              header_error = true
            //            }
            if (endString != "\r\n\r\n") {
              c = in.read()
            }
          }
          //println("User.run: Eingehende Nachricht " + header)

          var body = ""
          if (!header_error) {
            output = parseHeader(header)
            //println("User.run: Header " + output)
            body = parseBody(output)

            //            handle_request(output, client)
          } else {
            //header too big error
          }
          header_size = 0
          //println("User.run: Eingehende Nachricht " + header)
          serverServices.handle_request(output, body, this)
          //          this.senden("HTTP/1.1 200 Ok\r\n" + "Content-Type: application/json; charset=UTF-8\r\n\r\n")
          //                    this.executeCommand(rs);
        } else { //client wure beendet, schließe socket
          println("User.run: Client wurde beendet")
          serverServices.closeClient(this.id);
          error = true
        }
      }
    } catch {
      case e: IOException =>
        println("User.run: Socket wurde geschlossen")
        serverServices.closeClient(this.id);
    }
  }

  def isClosed(): Boolean = {
    socket.isClosed()
  }

  def closeConnection() {
    try {
      this.socket.close();
      println("User.closeConnection: Socket wurde geschlossen");
    } catch {
      case e: IOException =>
        println("User.closeConnection: Socket wurde nicht geschlossen");
    }
  }

  import java.nio.ByteBuffer
  import java.nio.ByteOrder

  def senden(text: String) {
    //    try {
    //      os = socket.getOutputStream()
    //      var s: String = text
    //      var b: Array[Byte] = s.getBytes("UTF-8")
    //      os.write(b)
    //      os.flush()
    //      println("C<--S " + text)
    //    } catch {
    //      case e: IOException =>
    //        println("SClient.senden: Fail")
    //    }
    try {
      var os = socket.getOutputStream()
      var b: Array[Byte] = text.getBytes("UTF-8")

      var buffer: ByteBuffer = ByteBuffer.allocate(4 + 4 + 4 + b.size)
      buffer.order(ByteOrder.LITTLE_ENDIAN)
      buffer.putInt(4 + 4 + b.size)
      buffer.putInt(4)
      buffer.putInt(b.size)
      buffer.put(b)

      var bytes: Array[Byte] = buffer.array()

      os.write(bytes)
      os.flush()
      //println("Send: " + text)
    } catch {
      case e: IOException =>
        println("Sende: Fail")
    }
  }

  def parseHeader(request: String): Map[String, String] = {

    var requestLine: Map[String, String] = Map()
    var requestHeaders: String = ""
    var messagetBody: String = ""

    var no_eof = request.replace("\r\n\r\n", "")
    var lines = no_eof.split("\r\n").toList

    //parse first request line 
    requestLine += ("verb" -> lines(0).split(" ")(0))
    requestLine += ("url" -> lines(0).split(" ")(1))
    requestLine += ("version" -> lines(0).split(" ")(2))

    //chieck if is a request or response
    if (requestLine("url").split("/").size > 0) { //-> request
      requestLine += ("service" -> requestLine("url").split("/")(1))
    } else { //->response
      requestLine += ("service" -> "")
    }

    //Wo ist die Zweiten informations line?
    //DONE: DELETE FIRST LINE ITS DATATRASH !!!!!!

    lines = lines.drop(1)
    //parse header
    for (line <- lines) {
      if (line != "") {
        //DONE: Save an Array if the value is seperated by spaces 
        var pair = line.split(": ")
        var key = pair(0)
        var values = pair(1)
        //        var value
        //Falls nur einer im StringArray, diesen als String ablegen
        //TODO: else Zweig, StringArray vielleicht als Array ablegen?
        //        if (values.size <= 1) {
        //          requestLine += (key -> values(0))
        //        } else {
        requestLine += (key -> values)
        //        }
      }
    }
    requestLine
  }

  def parseBody(header: Map[String, String]): String = {
    var body = ""
    if (header.contains("Content-Length")) {
      var content_length: Int = header("Content-Length").toInt
      if (content_length != 0) {
        var counter = 0
        while (counter < content_length) {
          println(counter)
          counter += 1
          body += in.read().asInstanceOf[Char]
        }
        println("Eingelesener Body " + body)
      }
    }
    body
  }

}