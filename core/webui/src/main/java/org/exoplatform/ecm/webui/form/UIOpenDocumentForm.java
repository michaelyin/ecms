package org.exoplatform.ecm.webui.form;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPopupComponent;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          toannh@exoplatform.com
 * On Dec 10, 2014
 * Popup window when open read-only a document in SE
 */
@ComponentConfig(
        template = "classpath:groovy/ecm/webui/UIConfirmMessage.gtmpl",
        events = {
                @EventConfig(listeners = UIOpenDocumentForm.ReadOnlyActionListener.class),
                @EventConfig(listeners = UIOpenDocumentForm.CancelActionListener.class)
        }
)
public class UIOpenDocumentForm extends UIComponent implements UIPopupComponent {

  public UIOpenDocumentForm() throws Exception {}

  public String filePath;
  public String mountPath;
  public String absolutePath;
  public String[] getActions() { return new String[] {"ReadOnly", "Cancel"}; }

  /**
   * Only open document with read-only mode.
   */
  public static class ReadOnlyActionListener extends EventListener<UIOpenDocumentForm> {
    @Override
    public void execute(Event<UIOpenDocumentForm> event) throws Exception {
      UIOpenDocumentForm uiConfirm = event.getSource();
      // impl readonly action
      UIPopupWindow popupAction = uiConfirm.getAncestorOfType(UIPopupWindow.class) ;
      popupAction.setShow(false) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(popupAction);

      event.getRequestContext().getJavascriptManager().require("SHARED/openDocumentInOffice")
              .addScripts("eXo.ecm.OpenDocumentInOffice.openDocument('" + uiConfirm.absolutePath + "', '" + uiConfirm.mountPath + "');");
    }
  }

  /**
   * Cancel action, close all popup include popup of ITHIT
   */
  public static class CancelActionListener extends EventListener<UIOpenDocumentForm> {
    @Override
    public void execute(Event<UIOpenDocumentForm> event) throws Exception {
      UIOpenDocumentForm uiConfirm = event.getSource();
      UIPopupWindow popupAction = uiConfirm.getAncestorOfType(UIPopupWindow.class) ;
      popupAction.setShow(false) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(popupAction);
    }
  }

  private String messageKey_;
  private String[] args_ = {};

  public void setMessageKey(String messageKey) { messageKey_ = messageKey; }

  public String getMessageKey() { return messageKey_; }

  public void setArguments(String[] args) { args_ = args; }

  public String[] getArguments() { return args_; }

  public void activate() {

  }

  public void deActivate() {

  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public void setMountPath(String mountPath) {
    this.mountPath = mountPath;
  }

  public void setAbsolutePath(String absolutePath) {
    this.absolutePath = absolutePath;
  }
}
