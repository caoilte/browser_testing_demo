package com.playfish.service.console

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import org.openqa.selenium.remote.server.{DriverSessions, DriverServlet}


/**
 * Created by IntelliJ IDEA.
 * User: coconnor
 * Date: 14/03/12
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */

class DriverSessionsServlet extends HttpServlet {

  var data: Option[String] = None

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val driverSessionsObject = getServletContext.getAttribute(DriverServlet.SESSIONS_KEY);
    val driverSessions = driverSessionsObject.asInstanceOf[DriverSessions]

    val sessionId = driverSessions.getSessions.iterator().next()

    val writer = resp.getWriter
    writer.print(sessionId)
    writer.close()

  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    data = Some(req.getParameter("data"))

  }
}
