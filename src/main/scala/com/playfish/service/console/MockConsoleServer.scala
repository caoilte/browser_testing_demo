package com.playfish.service.console

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import org.slf4j.LoggerFactory


/**
 * Created by IntelliJ IDEA.
 * User: coconnor
 * Date: 14/03/12
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */

class MockConsoleServer extends HttpServlet {

//  val CONSOLE_URL_PREFIX = "http://localhost:8080/"
//  val CAMPAIGN_ASSETS_PREFIX = CONSOLE_URL_PREFIX + "console-application/Console-Templates/campaign-assets/"
//  val TEMP_SLOT_1 = CAMPAIGN_ASSETS_PREFIX + "temp_slot1.jpg"
//  val TEMP_SLOT_2 = CAMPAIGN_ASSETS_PREFIX + "temp_slot2.jpg"
//  val TEMP_SLOT_3 = CAMPAIGN_ASSETS_PREFIX + "temp_slot3.jpg"
//  val TEMP_SLOT_4 = CAMPAIGN_ASSETS_PREFIX + "temp_slot4.jpg"
//
//  def initData() = {
//    val consoleConfiguration: ConsoleConfiguration = ConsoleConfiguration.apply
//    consoleConfiguration.headerFacetBuilder.aURLSlot().withImgSrc(TEMP_SLOT_4)
//      .withLinkUrl("http://playfish.com").build()
//    consoleConfiguration.headerFacetBuilder.aURLSlot().withImgSrc(TEMP_SLOT_3)
//      .withLinkUrl("http://playfish.com").build()
//    consoleConfiguration.headerFacetBuilder.aURLSlot().withImgSrc(TEMP_SLOT_2)
//      .withLinkUrl("http://playfish.com").build()
//    consoleConfiguration.build
//  }

  var data: Option[String] = None //Some(initData())



  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    data match {
      case Some(dataString) => {
        val writer = resp.getWriter
        writer.write(dataString)
        writer.close()
      }
      case None => {
        resp.sendError(503)
      }
    }

  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    data = Some(req.getParameter("data"))
    MockConsoleServer.log.info("data is ["+data+"]")

  }
}

object MockConsoleServer {
  val log = LoggerFactory.getLogger(classOf[MockConsoleServer])
}
