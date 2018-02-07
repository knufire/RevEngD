package odyssey.renderers;

import odyssey.models.Message;
import odyssey.models.Pattern;
import soot.SootClass;
import soot.SootMethod;

public class MessageRenderer implements IMessageRenderer {
  protected Message message;
  protected Pattern pattern;

  @Override
  public final String render(Message t) {
    this.message = t;
    StringBuilder builder = new StringBuilder();
    
    builder.append(renderFromClass(t.getMethodCallingClass()));
    builder.append(" ");
    builder.append(renderArrow());
    builder.append(" ");
    builder.append(renderToClass(t.getReceiverClass()));
    builder.append(" ");
    builder.append(renderMessage(t.getMethod()));
    
    return builder.toString();
  }

  @Override
  public final String render(Message t, Pattern pattern) {
    this.pattern = pattern;
    return render(t);
  }

  
  
  @Override
  public String renderFromClass(SootClass clazz) {
    return clazz.getShortName().replaceAll("\\$", "");
  }

  @Override
  public String renderToClass(SootClass clazz) {
    return clazz.getShortName().replaceAll("\\$", "");
  }

  @Override
  public String renderArrow() {
    return "->";
  }

  @Override
  public String renderMessage(SootMethod method) {
    return method.getName();
  }
  
  @Override
  public String getName() {
    return "default";
  }

}
