package odyssey.renderers;

import odyssey.models.Message;
import odyssey.models.Pattern;
import soot.SootClass;
import soot.SootMethod;

public class MessageRenderer implements IMessageRenderer {
  
  Pattern pattern;

  @Override
  public final String render(Message t) {
    StringBuilder builder = new StringBuilder();
    
    builder.append(renderFromClass(t.getMethodCallingClass()));
    builder.append(renderArrow());
    builder.append(renderToClass(t.getReceivingClass()));
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String renderToClass(SootClass clazz) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String renderArrow() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String renderMessage(SootMethod method) {
    // TODO Auto-generated method stub
    return null;
  }

}
