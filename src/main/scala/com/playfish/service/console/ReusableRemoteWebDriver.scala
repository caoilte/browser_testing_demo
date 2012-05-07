package com.playfish.service.console

import org.openqa.selenium.remote.{HttpCommandExecutor, RemoteWebDriver}
import java.net.URL
import org.openqa.selenium.Capabilities

class ReusableRemoteWebDriver(executor: HttpCommandExecutor, desiredCapabilities: Capabilities)
  extends RemoteWebDriver(executor, desiredCapabilities) {

  def this(remoteAddress: URL, desiredCapabilities: Capabilities) = {
    this(new HttpCommandExecutor(remoteAddress), desiredCapabilities)
  }

  SessionIdLookup.lookupSessionId match {
    case Some(sessionId) => setSessionId(sessionId)
    case None => super.startSession(desiredCapabilities)
  }

  protected override def startSession(desiredCapabilities: Capabilities): Unit = {
  }
}