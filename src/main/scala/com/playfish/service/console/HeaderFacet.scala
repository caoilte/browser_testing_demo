package com.playfish.service.console

import reflect.BeanInfo
import com.playfish.service.console.HeaderFacet.URLSlotBuilder

/**
 *
 */

object HeaderFacet {

  object JSonConfiguration {
    case class Content(`@attributes`: Content.Attributes, slots: Content.Slots, contentPool: Content.ContentPool)

    object Content {
      case class Attributes(src: String, contentClass: String)
      case class Slots(slot: List[Slots.Slot])
      case class ContentPool(content: List[ContentPool.Content])

      def apply(contentList: List[ContentPool.Content]): Content = {
        val atts = JSonConfiguration.Content.Attributes("RotationSelector", "RotationSelector")
        val jsonSlotList = contentList map {
          content => Slots.Slot(content.`@attributes`.src)
        }
        Content(atts, Slots(jsonSlotList), ContentPool(contentList))
      }

      object Slots {
        case class Slot(weight: Slot.Weight)
        object Slot {
          case class Weight(`@attributes`: Attributes)
          case class Attributes(src: String, country: String, weight: String)
          
          def apply(src: String): Slot = {
            Slot(Weight(Attributes(src, "", "100")))
          }
        }
      }

      object ContentPool {
        case class Content(`@attributes`: Content.Attributes, url: Content.Url)
        object Content {
          case class Attributes(contentClass: String, src: String, campaignId: String)
          case class Url(`@attributes`: Url.Attributes)
          object Url {
            case class Attributes(src: String, url: String)
          }

          def apply(slotName: String, imgSrc: String, linkUrl: String): Content = {
            val url = Content.Url(Content.Url.Attributes(imgSrc, linkUrl))
            val atts = Content.Attributes("URLLink", slotName, "1")
            Content(atts, url)
          }
        }
      }
    }
  }


  class URLSlotBuilder(headerFacetBuilder: HeaderFacetBuilder, imgSrc: Option[String], linkUrl: Option[String]) {
    def withLinkUrl(linkUrl: String) = new URLSlotBuilder(headerFacetBuilder, imgSrc, Some(linkUrl))
    def withImgSrc(imgSrc: String) = new URLSlotBuilder(headerFacetBuilder, Some(imgSrc), linkUrl)
    
    private def buildImgSrc = imgSrc match {
      case Some(imgSrc) => imgSrc
      case None => "http://localhost:8080/console-application/Console-Templates/campaign-assets/temp_slot2.jpg"
    }
    
    private def buildLinkUrl = linkUrl match {
      case Some(linkUrl) => linkUrl
      case None => "http://playfish.com"
    }
    
    def build() = {
      headerFacetBuilder.addUrlSlot(
        JSonConfiguration.Content.ContentPool.Content(headerFacetBuilder.nextSlotName(), buildImgSrc, buildLinkUrl))
    }
  }

  class HeaderFacetBuilder {
    var slotCount = 1
    var jsonContentList: List[JSonConfiguration.Content.ContentPool.Content] = List()
    
    def nextSlotName() = {
      val slotName = "src" + slotCount
      slotCount = slotCount + 1
      slotName
    }

    def addUrlSlot(newContent: JSonConfiguration.Content.ContentPool.Content) = {
      jsonContentList = newContent :: jsonContentList
    }
    
    def aURLSlot() = {
      new URLSlotBuilder(this, None, None)
    }

    def build() = {
      JSonConfiguration.Content(jsonContentList)
    }

  }


}
