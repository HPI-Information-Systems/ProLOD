/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.google.gwt.http.client.Response;

/**
 * Html Container event type.
 * 
 * <p/>
 * Note: For a given event, only the fields which are appropriate will be filled
 * in. The appropriate fields for each event are documented by the event source.
 * 
 * @see HtmlContainer
 */
public class HtmlContainerEvent extends ContainerEvent<HtmlContainer, Component> {

  private Throwable exception;
  private String html;
  private Response response;
  private HtmlContainer htmlContainer;

  /**
   * Creates a new event.
   * 
   * @param container the source container
   */
  public HtmlContainerEvent(HtmlContainer container) {
    super(container);
    this.htmlContainer = container;
  }

  /**
   * Creates a new event.
   * 
   * @param container the source container
   * @param component the component
   */
  public HtmlContainerEvent(HtmlContainer container, Component component) {
    super(container, component);
    this.htmlContainer = container;
  }

  /**
   * Returns the exception.
   * 
   * @return the exception
   */
  public Throwable getException() {
    return exception;
  }

  /**
   * Returns the html.
   * 
   * @return the html
   */
  public String getHtml() {
    return html;
  }

  /**
   * Returns the source html container.
   * 
   * @return the html container
   */
  public HtmlContainer getHtmlContainer() {
    return htmlContainer;
  }

  /**
   * Returns the response.
   * 
   * @return the response
   */
  public Response getResponse() {
    return response;
  }

  /**
   * Sets the exception.
   * 
   * @param exception the exception
   */
  public void setException(Throwable exception) {
    this.exception = exception;
  }

  /**
   * Sets the html.
   * 
   * @param html the html
   */
  public void setHtml(String html) {
    this.html = html;
  }

  /**
   * Sets the source html container.
   * 
   * @param htmlContainer the html container
   */
  public void setHtmlContainer(HtmlContainer htmlContainer) {
    this.htmlContainer = htmlContainer;
  }

  /**
   * Sets the response.
   * 
   * @param response the response
   */
  public void setResponse(Response response) {
    this.response = response;
  }

}
