package com.playfish.service.console

import net.liftweb.json._
import scalaj.http.Http

/**
 *
 */

object SessionIdLookup {
  implicit val formats = DefaultFormats

  case class Capabilities(platform: String, javascriptEnabled: Boolean, cssSelectorsEnabled: Boolean,
                          handlesAlerts: Boolean, browserName: String, nativeEvents: Boolean,
                          takesScreenshot: Boolean, version: String)
  case class Session(id: String, `class`: String, capabilities: Capabilities, hCode: String)
  case class Sessions(status: Integer, sessionId: String, value: Array[Session],
                      `class`: String, hCode: String)

  def lookupSessionId: Option[String] = {

    val response = Http("http://localhost:8080/wd/sessions/").asString.trim
    val json = parse(response)
    val capabilities = (json \ "value")
    val res: List[Session] = capabilities.extract[List[Session]]
    
    if (res.size == 0) {
      None
    } else {
      Some(res(0).id)
    }
  }
}
